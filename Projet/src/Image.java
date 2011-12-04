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
		this.img = img;
		this.pts3 = new Stack<Pt_corresp>();
		this.pts2 = new Stack<Pt2>();
		this.pos = null;
		this.A 			 = A;
		this.dist_coeffs = dist_coeffs;
	}
	
	public void update() {
		Stack<Pt2> pts2_ = new Stack<Pt2>();
		Stack<Pt3> pts3_ = new Stack<Pt3>();
		Iterator<Pt2> p2 = pts2.iterator();
		int n = 0;
		for (Pt_corresp p : pts3) {
			Pt3 p_ = p.pt3();
			if (p_ == null) {
				p2.next();
			} else {
				pts2_.push(p2.next());
				pts3_.push(p_);
				n++;
			}
		}
		if (n<4)  pos = null;
		else	  pos = new Position(pts2_, pts3_, A, dist_coeffs);
	}
	
}
