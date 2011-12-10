import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;

import processing.core.PImage;


public class Plan {

	public Stack<Pt2> corners2d;
	public Stack<Pt3> corners3d;
	public Image      img;
	public Pt3        M,n;
	
	
	public Plan(Stack<Pt3> corners3d, Image img, Stack<Pt2> ptsInImg) {
		this.img = img;
		// Copying corners2d & corners3d
		this.corners2d = new Stack<Pt2>();
		this.corners3d = new Stack<Pt3>();
		for (Pt2 p : corners2d) this.corners2d.push(p);
		for (Pt3 p : corners3d) this.corners3d.push(p);
		// Getting M
		this.M = new Pt3();
		int k = 0;
		Pt3[] t3 = new Pt3[corners3d.size()];
		for (Pt3 p : corners3d) { M.add(p); t3[k] = p; k++; }
		M.multiply(1/k);
		// Getting n
		Stack<Pt3> nList = new Stack<Pt3>();
		Pt3 n_ = new Pt3();
		double d = 0;
		for (int i = 1; i < k; i++) {
			for (int j = 0; j < i; j++) {
				Pt3 n = t3[i].minus(M).cross(t3[j].minus(M));
				nList.push(n);
				double d_ = n.norm2();
				if (d_ > d) { n_ = n; d = d_; }
			}
		}
		this.n = new Pt3();
		for (Pt3 n : nList) this.n.add(n.times((n.scal(n_) < 0) ? -1 : 1));
		this.n.rescale();
	}
	
	
	private boolean inside(Pt2 p) {
		
	}
	
	private Pt2 intersec(Pt2 a, Pt2 b, Pt2 c, Pt2 d) {
		Matrix A = new Matrix(2,2);
		A.set(0,0, b.x-a.x); A.set(0,1, c.x-d.x);
		A.set(1,0, b.y-a.y); A.set(1,1, c.y-d.y);
		Pt2 lm = c.minus(a).apply(A.inverse());
		return (lm.x < 0 || lm.x > 1 || lm.y < 0 || lm.y > 1) ? null : a.plus(b.minus(a).times(lm.x));
	}

}
