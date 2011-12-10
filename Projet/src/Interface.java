
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

	Matrix A, Ainv, dist_coeffs;
	double px = 512, py = 384, f = 929;
	
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
	
	Stack<Plan>       plans   = new Stack<Plan>();
	int               n_plans = 0;
	
	int step = 0;
	Stack<Pt2> polygone  = null;
	Stack<Pt3> poly3     = null;
	Pt_corresp pol_start = null;
	
	boolean textured = true;
	int LD_size = 15;
	
	
	
	public void setup() {
		
		/**  Constants & Variables initialization  **/
		// Camera calibration
		A = Matrix.identity(3,3);
		A.set(0,0, f); A.set(1,1, f); A.set(0,2, px); A.set(1,2, py);
		Ainv = A.inverse();
		
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
		if (step < 2) {
			if (iImg < 0) {
				plot3d_skel();
			} else {
				background(0xFF000000);
				plot(imgs[iImg]);
			}
		} else {
			if (textured) plot3d_textured();
			else          plot3d_skel();
		}
	}
	
	
	
/******************
 **   Movement   **
 ******************/
	

	public void keyPressed() {
		switch (key) {
			case 'z'  : if (iImg < 0 || step == 2) view.moveForward( dt); break;
			case 's'  : if (iImg < 0 || step == 2) view.moveForward(-dt); break;
			case 'd'  : if (iImg < 0 || step == 2) view.moveRight( dt);   break;
			case 'q'  : if (iImg < 0 || step == 2) view.moveRight(-dt);   break;
			case 'l'  : if (step == 0) openF(); break;
			case ' '  : if (step == 0) openF(); break;
			case 'a'  : if (polygone == null) iImg = (iImg == -1)      ? (nImgs-1) : (iImg-1); break;
			case 'e'  : if (polygone == null) iImg = (iImg == nImgs-1) ? (-1)      : (iImg+1); break;
			case 'x'  : delete_from_img(); break;
			case '\n' : nextStep(); break;
			case 'v'  : textured = !textured; break;
		}
		plot();
	}

	public void mouseDragged() {
		if (iImg < 0 || step == 2) view.rotate( (double) (mouseY - mouse_y_)*da/30 ,  (double) (mouseX - mouse_x_)*da/30 );
		mouseMoved();
		plot();
	}
	
	public void mouseMoved() {
		mouse_x_ = mouseX; mouse_y_ = mouseY;
		if (step == 1) plot();
	}
	
	public void mouseClicked() {
		switch (step) {
			case 0:
				switch (mouseButton) {
					case RIGHT:   // Select Point
						select_point();
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
				break;
			case 1:
				if (iImg < 0 || mouseButton != LEFT) return;
				select_point();
				if (ptSel == pol_start) {
					plans.push(new Plan(poly3, imgs[iImg], polygone));
					n_plans++;
					polygone = null; poly3 = null; pol_start = null; ptSel = null;
				} else {
					if (polygone == null) { 
						polygone = new Stack<Pt2>();
						poly3    = new Stack<Pt3>();						
						pol_start = ptSel; 
					}
					polygone.push(ptSel.pt2_in_img(imgs[iImg]));
					poly3.push(ptSel.pt3());
				}				
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
		Pt3 p1 = new Pt3( 2, 2,7).apply(pos.R.inverse()).plus(p0);
		Pt3 p2 = new Pt3( 2,-2,7).apply(pos.R.inverse()).plus(p0);
		Pt3 p3 = new Pt3(-2, 2,7).apply(pos.R.inverse()).plus(p0);
		Pt3 p4 = new Pt3(-2,-2,7).apply(pos.R.inverse()).plus(p0);
		Pt3 p5 = new Pt3( 0, 0,70).apply(pos.R.inverse()).plus(p0);		
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
		if (step == 0) return;
		fill(0xFFFF0000); stroke(0xFFFF0000);		
		for (Plan pl : plans) {
			if (pl.img != img) continue;
			Pt2 a = null, b = null;
			for (Pt2 p : pl.corners2d) {
				if (a == null) a = p;
				plot(p,b);
				b = p;
			}
			plot(a,b);
		}
		if (polygone == null) return;
		fill(0xFF00FF00); stroke(0xFF00FF00);
		Pt2 a = null;
		for (Pt2 p : polygone) {
			plot(a,p);
			a = p;
		}
		plot(a, new Pt2(mouseX,mouseY));
	}
	
	public void plot3d_skel() {
		
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
		
//		for (Image img : imgs) if (img != null  && img.pos != null) plot(img.pos);
//		if (ptSel != null) {
//			if (ptSel.pt.known)
//				for (Pt_in_img pii : ptSel.pt2_list) 
//					if (pii.img.pos != null)
//						plot(new Drt3(pii.pt, pii.img.pos, pii.img.A)); 
//			else
//				try { for (Drt3 d : ptSel.pt.drt) plot(d); } catch(Error r) {}
//		}
		
		if (step == 0) return;
		fill(0xFFFF0000); stroke(0xFFFF0000);		
		for (Plan pl : plans) {
			Pt3 a = null, b = null;
			for (Pt3 p : pl.corners3d) {
				if (a == null) a = p;
				plot(p,b);
				b = p;
			}
			plot(a,b);
		}
		
	}
	
	public void plot3d_textured() {
		background(0xFFFFFFFF);
		for (int x = 0; x < this.width; x += LD_size) {
			for (int y = 0; y < this.height; y += LD_size) {
				int c = texture_color(new Pt2(x,y));
				if (c != 0) {
					for (int x_ = x; x_ < x+LD_size; x_++)
						for (int y_ = y; y_ < y+LD_size; y_++)
							set(x_,y_,c);
				}
			}
		}
	}
	
	public int texture_color(Pt2 p) {
		Plan[]   pls   = new Plan[n_plans];
		double[] dists = new double[n_plans];
		int i = 0;
		for (Plan pl : plans) {
			pls[i]   = pl;
			dists[i] = pl.dist_from_view(view, p, A);
			i++;
		}
		for (i = 0; i < n_plans-1; i++) {
			int    k = i;
			for (int j = i+1; j < n_plans; j++)
				if (dists[j] < dists[k]) k = j;
			double d  = dists[i]; dists[i] = dists[k]; dists[k] = d;
			Plan   pl = pls[i];   pls[i]   = pls[k];   pls[k]   = pl;
		}
		Matrix Rinv = view.R.inverse();
		Pt3 a = p.toPt3().apply(Ainv).apply(Rinv);
		Pt3 b = view.t.apply(Rinv);
		for (i = 0; i < n_plans; i++) {
			if (dists[i] < 0) return 0;
			int c = pls[i].color(a.times(dists[i]).minus(b));
			if (c != 0) return c;
		}
		return 0;
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
	

	private void select_point() {
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
	}
	
	private void delete_from_img() {
		switch (step) {
			case 0:
				if (ptSel == null || iImg < -1) return;
				ptSel.delete_from_img(imgs[iImg]);
				if (ptSel.isEmpty()) {
					Stack<Pt_corresp> pts_ = new Stack<Pt_corresp>();
					for (Pt_corresp p : pts) if (p != ptSel) pts_.push(p);
					pts = pts_;
					ptSel = null;
				}
				break;
			case 1:
				if (polygone == null) return;
				polygone.pop(); poly3.pop();
				if (polygone.isEmpty()) { polygone = null; poly3 = null; pol_start = null; ptSel = null; }
				break;
		}
	}
	
	private void nextStep() {
		if (step  < 2) { step++; ptSel = null; }
		if (step == 2) iImg = -1;
	}
	
	
}


