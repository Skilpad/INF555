import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;

import processing.core.PImage;


public class Plan {

	public Pt2[] corners2d;
	public Pt3[] corners3d;
	public Image img;
	public Pt3   M,n;
	
	public double xmin, xmax, ymin, ymax;
	public Pt2[] view_corners;
	
	
	public Plan(Stack<Pt3> corners3d, Image img, Stack<Pt2> ptsInImg) {
		this.img = img;
		this.xmin = 0; this.xmax = 0; this.ymin = 0; this.ymax = 0; this.view_corners = null;
		// Copying corners2d & corners3d
		this.corners2d = new Pt2[ptsInImg.size()];
		this.corners3d = new Pt3[corners3d.size()];
		int k;
		k = 0; for (Pt2 p : ptsInImg)  { this.corners2d[k] = p; k++; }
		k = 0; for (Pt3 p : corners3d) { this.corners3d[k] = p; k++; }
		// Getting M
		this.M = new Pt3();
		for (Pt3 p : corners3d) this.M.add(p);
		this.M.multiply(1./this.corners3d.length);
		 // Getting n
//		Stack<Pt3> nList = new Stack<Pt3>();
//		Pt3 n_ = new Pt3();
//		double d = 0;
//		for (int i = 1; i < k; i++) {
//			for (int j = 0; j < i; j++) {
//				Pt3 n = this.corners3d[i].minus(M).cross(this.corners3d[j].minus(M));
//				nList.push(n);
//				double d_ = n.norm2();
//				if (d_ > d) { n_ = n; d = d_; }
//			}
//			Pt3 n = this.corners3d[i].minus(M).cross(this.corners3d[j].minus(M));
//			nList.push(n);
//			double d_ = n.norm2();
//		}
//		this.n = new Pt3();
//		for (Pt3 n : nList) this.n.add(n.times((n.scal(n_) < 0) ? -1 : 1));
//		this.n.rescale();
		this.n = new Pt3();
		for (int i = 1; i < k; i++) {
			this.n.add(this.corners3d[i-1].minus(M).cross(this.corners3d[i].minus(M)));
		}
		this.n.add(this.corners3d[k-1].minus(M).cross(this.corners3d[0].minus(M)));
		this.n.rescale();
	}
	
	
	public boolean inside(Pt2 p) {
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
	
	public boolean inside_view(Pt2 p) {
		if (view_corners == null) return false;
		if (p.x < xmin || p.x > xmax || p.y < ymin || p.y > ymax) return false;
		double sgn = 0;
		int    i = -1;
		Pt2    M = new Pt2(0,0);
		while (sgn == 0 && i < view_corners.length-2) {
			i++;
			M = view_corners[i].plus(view_corners[i+1]).times(0.5);
			sgn = Math.signum(view_corners[i+1].minus(M).cross(p.minus(M)));
		}
		if (sgn == 0) return false;
		int cnt = (sgn > 0) ? 0 : 1;
		for (int j = 0; j < view_corners.length - 1; j++) {
			if (i == j) continue;
			if (intersec(view_corners[j], view_corners[j+1], M, p)) cnt++;
		}
		if (intersec(view_corners[0], view_corners[view_corners.length-1], M, p)) cnt++;
		return cnt%2 == 0;
	}
	
	private static boolean intersec(Pt2 a, Pt2 b, Pt2 c, Pt2 d) {
		Matrix A = new Matrix(2,2);
		A.set(0,0, b.x-a.x); A.set(0,1, c.x-d.x);
		A.set(1,0, b.y-a.y); A.set(1,1, c.y-d.y);
		Pt2 lm;
		try { lm = c.minus(a).apply(A.inverse()); }
		catch (RuntimeException e) { return false; }
		return ! (lm.x < 0 || lm.x > 1 || lm.y < 0 || lm.y > 1);
	}
	
	
	public double dist_from_view(Position pos, Pt2 p, Matrix A) {
		double d = n.scal(p.toPt3().apply(A.inverse()).apply(pos.Rinv));
		if (d == 0) return -1;
		return n.scal(pos.t.apply(pos.Rinv).plus(M))/d;
	}
	
	public int color(Pt3 p) {
		Pt2 p_ = p.apply(img.pos.R).plus(img.pos.t).apply(img.A).toPt2Im();
		if (p_ == null) return 0;	
		if (inside(p_)) return img.img.get((int) p_.x, (int) p_.y);
		return 0;
	}
	
	public boolean prepare_to_view(Position pos, Matrix A, Point view_dim) {
//		if (pos.k.scal(n) < 0) { view_corners = null; return false; }    // Plan vue de derrière
		xmin = Double.POSITIVE_INFINITY; ymin = Double.POSITIVE_INFINITY; xmax = -1; ymax = -1;
		view_corners = new Pt2[corners3d.length];
		for (int i = 0; i < corners3d.length; i++) {
			view_corners[i] = corners3d[i].toPt2(pos, A);
			if (view_corners[i] == null) { view_corners = null; return false; }
			if (view_corners[i].x < xmin) { xmin = view_corners[i].x; }
			if (view_corners[i].x > xmax) { xmax = view_corners[i].x; }
			if (view_corners[i].y < ymin) { ymin = view_corners[i].y; }
			if (view_corners[i].y > ymax) { ymax = view_corners[i].y; }
		}
		if (xmax < 0 || ymax < 0 || xmin > view_dim.x || ymin > view_dim.y) { view_corners = null; return false; }
		return true;
	}
	
	
}
