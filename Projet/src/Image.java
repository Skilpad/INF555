import java.util.Iterator;
import java.util.Stack;

import Jama.Matrix;
import processing.core.PImage;



public class Image {

	public PImage     		 img;
	public Stack<Pt_corresp> pts3;
	public Stack<Pt2>    	 pts2;
	public Position          pos;
	
	public Matrix A, dist_coeffs;
	
	public Image(PImage img, Matrix A, Matrix dist_coeffs) {
		this.img  = img;
		this.pts3 = new Stack<Pt_corresp>();
		this.pts2 = new Stack<Pt2>();
		this.pos  = null;
		this.A 			 = A;
		this.dist_coeffs = dist_coeffs;
	}
	
	public void update() {     // TODO: Ne prendre en compte que les points de références s'il y en a au moins 4
		Stack<Pt2> pts2_ = new Stack<Pt2>();
		Stack<Pt3> pts3_ = new Stack<Pt3>();
		Stack<Pt2> pts2_known = new Stack<Pt2>();
		Stack<Pt3> pts3_known = new Stack<Pt3>();		
		Iterator<Pt2> p2 = pts2.iterator();
		int n = 0, n_k = 0;
		for (Pt_corresp p : pts3) {
			Pt3 p_ = p.pt3();
			if (p_ == null) {
				p2.next();
			} else {
				Pt2 p2_ = p2.next();
				pts2_.push(p2_);
				pts3_.push(p_);
				if (p.known()) {					
					pts2_known.push(p2_);
					pts3_known.push(p_);
					n_k++;
				}
				n++;
			}
		}
			 if (n  <4) pos = null;
		else if (n_k<4)	pos = new Position(pts2_, pts3_, A, dist_coeffs);
		else           	pos = new Position(pts2_known, pts3_known, A, dist_coeffs);
	}
	
	public void forget_pt(Pt_corresp p) {
		Stack<Pt_corresp> pts3_ = new Stack<Pt_corresp>();
		Stack<Pt2>    	  pts2_ = new Stack<Pt2>();		
		Iterator<Pt2> p2 = pts2.iterator();
		for (Pt_corresp p_ : pts3) {
			if (p_ == p) {
				p2.next();
			} else {
				pts2_.push(p2.next());
				pts3_.push(p_);
			}
		}
		pts2 = pts2_;
		pts3 = pts3_;
	}
	
}
