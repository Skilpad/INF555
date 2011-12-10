
import java.awt.Point;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JFileChooser;

import Jama.Matrix;

import processing.opengl.*;
import processing.core.*;


public class Interface extends PApplet {

	Position view = new Position(Matrix.identity(3,3), new Pt3(2,2, 500));

	Matrix A, dist_coeffs;
	
	Point windowDim = new Point(1024,768);

	
	static boolean WITH_PTS = true, NO_PTS = false;
	
	// Movement
	double da = 0.1;
	double dt = 10;
	int mouse_x_;
	int mouse_y_;
	
	
	
	// Data
	int               nImgs = 0;
	Image[]           imgs  = new Image[10];
	Stack<Pt_corresp> pts   = new Stack<Pt_corresp>();
	int               iImg  = -1;
	Pt_corresp        ptSel = null;

	
	
	
	public void setup() {
		
		/**  Constants & Variables initialization  **/
		// Camera calibration
		A = Matrix.identity(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);
		
		/**  Reference points **/
		pts.add(new Pt_corresp(new Pt3(  0,  0,0)));
		pts.add(new Pt_corresp(new Pt3(100,  0,0)));
		pts.add(new Pt_corresp(new Pt3(  0,100,0)));
		pts.add(new Pt_corresp(new Pt3(100,100,0)));
		
		/**  Window  **/
		size(windowDim.x,windowDim.y);
		plot();
	}
	
	public void draw() {}
	
	public void plot() {
		if (iImg < 0) {
			// TODO
			background(0xFFFFFFFF);
			fill(color(0xFF0000FF)); stroke(color(0xFF0000FF));
			plot(new Drt3(new Pt3(0,0,0), new Pt3(1,0,0)));
			plot(new Pt3( 1, 0, 0));
			fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
			plot(new Drt3(new Pt3(0,0,0), new Pt3(0,1,0)));
			plot(new Pt3( 0, 1, 0));
			fill(color(0xFF00FF00)); stroke(color(0xFF00FF00));
			plot(new Drt3(new Pt3(0,0,0), new Pt3(0,0,1)));
			plot(new Pt3( 0, 0, 1));
			
			fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
			for (Pt_corresp p : pts) plot(p.pt3());
			if (ptSel != null) { fill(color(0xFF00FF00)); stroke(color(0xFF00FF00)); plot(ptSel.pt3()); }
			
			for (Image img : imgs) if (img != null && img.pos != null) plot(img.pos);
			if (ptSel != null) {
				try { for (Drt3 d : ptSel.pt.drt) plot(d); } catch(Error r) {}
			}
			
		} else {
			background(0xFF000000);
			plot(imgs[iImg]);
		}
	}

	
	
	
	
/******************
 **   Movement   **
 ******************/
	

	public void keyPressed() {
		switch (key) {
			case 'z' : if (iImg < 0) view.moveForward( dt); break;
			case 's' : if (iImg < 0) view.moveForward(-dt); break;
			case 'd' : if (iImg < 0) view.moveRight( dt); break;
			case 'q' : if (iImg < 0) view.moveRight(-dt); break;
			case 'l' : openF(); break;
			case ' ' : openF(); break;
			case 'a' : iImg = (iImg == -1)      ? (nImgs-1) : (iImg-1); break;
			case 'e' : iImg = (iImg == nImgs-1) ? (-1)      : (iImg+1); break;
			case 'x' : delete_from_img(); break;
		}
		plot();
	}

	public void mouseDragged() {
		if (iImg < 0) view.rotate( (double) (mouseY - mouse_y_)*da/30 ,  (double) (mouseX - mouse_x_)*da/30 );
		mouseMoved();
		plot();
	}
	
	public void mouseMoved() {
		mouse_x_ = mouseX; mouse_y_ = mouseY;
	}
	
	public void mouseClicked() {
		switch (mouseButton) {
			case RIGHT:   // Select Point
				Pt2 here = new Pt2(mouseX,mouseY);
				if (iImg < 0) {
					double d2 = Double.POSITIVE_INFINITY;
					for (Pt_corresp p : pts) {
						Pt3 p3 = p.pt3();
						if (p3 == null) continue;
						Pt2 p_ = p3.toPt2Im(view, A);
						if (p_ == null) continue;
						double d2_ = p_.dist2(here);
						if (d2_ < d2) { d2 = d2_; ptSel = p; }
					}
				} else {
					double d2 = Double.POSITIVE_INFINITY;
					for (Pt_corresp p : pts) {
						Pt2 p_ = p.pt2_in_img(imgs[iImg]);
						if (p_ == null) continue;
						double d2_ = p_.dist2(here);
						if (d2_ < d2) { d2 = d2_; ptSel = p; }
					}
				}
				break;
			case LEFT:    // Add a new Pt_in_img to the select Pt_corresp				
				if (iImg < 0) return;
				if (ptSel == null) return;
				Pt_in_img p = ptSel.pt_in_img(imgs[iImg]);
				if (p == null)
					ptSel.add(new Pt_in_img(new Pt2(mouseX,mouseY), imgs[iImg]));
				else
					p.changePt(new Pt2(mouseX,mouseY));
				break;
			case CENTER:  // New point
				if (iImg < 0) return;
				Pt_corresp p_ = new Pt_corresp();
				p_.add(new Pt_in_img(new Pt2(mouseX,mouseY), imgs[iImg]));
				pts.push(p_);
				ptSel = p_;
				break;
		}
		plot();
	}
	
	
	
/******************
 **   Plotting   **
 ******************/
	
	
	public void plot(Pt2 p) {
		if (p == null) return;
		ellipse((float) p.x, (float) p.y, 4, 4);
	}
	
	public void plot(Pt3 p) {
		if (p == null) return;
		plot(p.toPt2Im(view, A));
	}
	
	public void plot(Stack<Pt3> P, boolean pts) {
		Pt3 p_last = null;
		Pt3 p_0    = null;
		for (Pt3 p : P) {
			if (pts) plot(p);
			if (p_0    == null) p_0 = p;
			if (p_last != null) plot(p_last, p);
			p_last = p;
		}
		plot(p_last, p_0);
	}
	
	public void plot(Pt_corresp p) {
		plot(p.pt3());
	}
	
	public void plot(Stack<Pt_corresp> P) {
		for (Pt_corresp p : P) plot(p);
	}
	
	public void plot(Pt2 a, Pt2 b) {
		if (a == null || b == null) return;
		line((float) a.x,(float) a.y, (float) b.x, (float) b.y);
	}
	
	public void plot(Pt3 a, Pt3 b) {
		if (a == null || b == null) return;
		plot(a.toPt2Im(view, A), b.toPt2Im(view, A));
	}
	
	public void plot(Pt_corresp a, Pt_corresp b) {
		plot(a.pt3(), b.pt3());
	}
	
	public void plot(Drt3 d) {
		Pt3 M = d.M.apply(view.R).plus(view.t).apply(A);     //    M = A ( R d.M + t )
		Pt3 N = d.v.apply(view.R).apply(A);
		double Au, Bu, Av, Bv;
		if (N.z == 0) {
			if (M.z <= 0) return;
			Au = M.x/M.z; Bu = N.x/M.z;         		//    u = Au + Bu X
			Av = M.y/M.z; Bv = N.y/M.z;         		//    v = Av + Bv X    (X in R)
		} else {
			Au = N.x/N.z; Bu = (M.x - M.z * Au);         //    u = Au + Bu X
			Av = N.y/N.z; Bv = (M.y - M.z * Av);         //    v = Av + Bv X    (X in R)
		}
		double tmin, tmax;
		if (Bu == 0) {
			if (Bv == 0) return;
			tmin = ( ((Bv>0)?0:getHeight()) - Av ) / Bv;
			tmax = ( ((Bv>0)?getHeight():0) - Av ) / Bv;			
		} else if (Bv == 0) {
			tmin = ( ((Bu>0)?0:getWidth()) - Au ) / Bu;
			tmax = ( ((Bu>0)?getWidth():0) - Au ) / Bu;
		} else {
			tmin = Math.max( ( ((Bu>0)?0:getWidth()) - Au ) / Bu ,  ( ((Bv>0)?0:getHeight()) - Av ) / Bv );
			tmax = Math.min( ( ((Bu>0)?getWidth():0) - Au ) / Bu ,  ( ((Bv>0)?getHeight():0) - Av ) / Bv );
		}
		if (N.z != 0) { tmin = Math.max(0, tmin); tmax = Math.max(0, tmax); }
		double dt   = (tmax - tmin)/10; 
		if (dt < 0.0001) { line((float) (Au+Bu*tmin), (float) (Av+Bv*tmin), (float) (Au+Bu*(tmax)), (float) (Av+Bv*(tmax))); return; }
		tmax += dt/2;
		for (double t = tmin; t < tmax; t += dt) {
			line((float) (Au+Bu*t), (float) (Av+Bv*t), (float) (Au+Bu*(t+dt)), (float) (Av+Bv*(t+dt)));
		}
	}
	
	public void plot(Position pos) {
		plot(pos, 0xFFFF0000);
	}
	
	public void plot(Position pos, int c) {
		Pt3 p0 = pos.t.apply(pos.R.inverse()).times(-1);
		Pt3 p1 = new Pt3( 2, 2,7).apply(pos.R.inverse()).minus(pos.t.apply(pos.R.inverse()));
		Pt3 p2 = new Pt3( 2,-2,7).apply(pos.R.inverse()).minus(pos.t.apply(pos.R.inverse()));
		Pt3 p3 = new Pt3(-2, 2,7).apply(pos.R.inverse()).minus(pos.t.apply(pos.R.inverse()));
		Pt3 p4 = new Pt3(-2,-2,7).apply(pos.R.inverse()).minus(pos.t.apply(pos.R.inverse()));
		Pt3 p5 = new Pt3( 0, 0,70).apply(pos.R.inverse()).minus(pos.t.apply(pos.R.inverse()));		
		fill(color(c)); stroke(color(c));
		plot(p0);
		plot(p0,p1); plot(p0,p2); plot(p0,p3); plot(p0,p4); plot(p0,p5);
		fill(color(0xFFFFFFFF));
		plot(p1); plot(p2); plot(p3); plot(p4);
	}

	public void plot(Image img) {
		image(img.img, 0,0);
		fill(0xFFFF0000); stroke(0xFFFF0000);
		for (Pt2 p : img.pts2) plot(p);
		fill(0xFF00FF00); stroke(0xFF00FF00);
		if (ptSel != null) plot(ptSel.pt2_in_img(img));
		if (img.pos == null) 
			{ fill(0x88FF0000); stroke(0x88FF0000); textSize(32); text("Unknow pose", 20, 50); }
	}
	
	
	
	
/*******************
 **     Files     **
 *******************/
		

	private void openF() {
		String f = selectInput();
		if (f != null) {
			if (nImgs == imgs.length) {
				Image[] imgs_ = new Image[nImgs+10];
				for (int i = 0; i < nImgs; i++) imgs_[i] = imgs[i];
				imgs = imgs_;
			}
			imgs[nImgs] = new Image(loadImage(f), A, dist_coeffs);
			iImg = nImgs;
			nImgs++;
		}
	}
	
	
	

/**********************
 **   Select Point   **
 **********************/
	
	
	private void delete_from_img() {
		if (ptSel == null || iImg < -1) return;
		ptSel.delete_from_img(imgs[iImg]);
		if (ptSel.isEmpty()) {
			Stack<Pt_corresp> pts_ = new Stack<Pt_corresp>();
			for (Pt_corresp p : pts) if (p != ptSel) pts_.push(p);
			pts = pts_;
			ptSel = null;
		}
	}

	
}


