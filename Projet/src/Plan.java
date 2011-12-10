import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;

import processing.core.PImage;


public class Plan {

	public Pt2[] corners2d;
	public Pt3[] corners3d;
	public Image img;
	public Pt3   M,n;
	
	
	public Plan(Stack<Pt3> corners3d, Image img, Stack<Pt2> ptsInImg) {
		this.img = img;
		// Copying corners2d & corners3d
		this.corners2d = new Pt2[ptsInImg.size()];
		this.corners3d = new Pt3[corners3d.size()];
		int k;
		k = 0; for (Pt2 p : ptsInImg)  { this.corners2d[k] = p; k++; }
		k = 0; for (Pt3 p : corners3d) { this.corners3d[k] = p; k++; }
		// Getting M
		this.M = new Pt3();
		for (Pt3 p : corners3d) M.add(p);
		M.multiply(1/this.corners3d.length);
		// Getting n
		Stack<Pt3> nList = new Stack<Pt3>();
		Pt3 n_ = new Pt3();
		double d = 0;
		for (int i = 1; i < k; i++) {
			for (int j = 0; j < i; j++) {
				Pt3 n = this.corners3d[i].minus(M).cross(this.corners3d[j].minus(M));
				nList.push(n);
				double d_ = n.norm2();
				if (d_ > d) { n_ = n; d = d_; }
			}
		}
		this.n = new Pt3();
		for (Pt3 n : nList) this.n.add(n.times((n.scal(n_) < 0) ? -1 : 1));
		this.n.rescale();
	}
	
	
	public boolean inside(Pt2 p) {   // TODO: set private
		double sgn = 0;
		int    i = -1;
		Pt2    M = new Pt2(0,0);
		while (sgn == 0 && i < corners2d.length-2) {
			i++;
			M = corners2d[i].plus(corners2d[i+1]).times(0.5);
			sgn = Math.signum(corners2d[i+1].minus(M).cross(p.minus(M)));
		}
		if (sgn == 0) return false;
		int cnt = (sgn > 0) ? 0 : 1;
		for (int j = 0; j < corners2d.length - 1; j++) {
			if (i == j) continue;
			if (intersec(corners2d[j], corners2d[j+1], M, p)) cnt++;
		}
		if (intersec(corners2d[0], corners2d[corners2d.length-1], M, p)) cnt++;
		return cnt%2 == 0;
	}
	
	private boolean intersec(Pt2 a, Pt2 b, Pt2 c, Pt2 d) {
		Matrix A = new Matrix(2,2);
		A.set(0,0, b.x-a.x); A.set(0,1, c.x-d.x);
		A.set(1,0, b.y-a.y); A.set(1,1, c.y-d.y);
		Pt2 lm;
		try { lm = c.minus(a).apply(A.inverse()); }
		catch (RuntimeException e) { return false; }
		return ! (lm.x < 0 || lm.x > 1 || lm.y < 0 || lm.y > 1);
	}
	
	
	public double dist_from_view(Position pos, Pt2 p, Matrix A) {
		Matrix Rinv = pos.R.inverse();
		double d = n.scal(p.toPt3().apply(A.inverse()).apply(Rinv));
		if (d == 0) return -1;
		return n.scal(pos.t.apply(Rinv).plus(M));
	}
	
	
}
