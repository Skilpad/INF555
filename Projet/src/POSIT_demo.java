
import java.awt.Point;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JFileChooser;

import Jama.Matrix;

import processing.opengl.*;
import processing.core.*;


public class POSIT_demo extends PApplet {

	Position view;
	
	Matrix A, dist_coeffs;
	
	Point windowDim = new Point(1024,768);
	
	boolean coplanar_demo = false;
	boolean init_before   = true;
	
	static boolean WITH_PTS = true, NO_PTS = false;
	
	// Movement
	double da = 0.1;
	double dt = 10;
	int mouse_x_;
	int mouse_y_;


	
	Position POS, POS0;
	Stack<Pt3> planApts;
	
	
	public void setup() {
		
		if (view == null) print("Use z,q,s,d and mouse (clicking) to move in 3D view.\n" +
								"Use 'r' to launch another randomized demonstration.\n" +
								"Use 'c' to switch from non-coplanar to coplanar reference points.\n" +
								"Initial camera position is in blue. Calculated camera position is un red.\n" +
								"Use 'a' to change camera plotting order (to see when camera are superposed).\n\n\n");
		
		view = new Position(Matrix.identity(3,3), new Pt3(0,0, 500));
		view.rotate(0.2, 0.2);
		view.t = view.t.apply(view.R.inverse());
		
		/** Constants & Variables initialization **/
		// Camera calibration
		A = Matrix.identity(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);
		
		planApts = new Stack<Pt3>();
		Stack<Pt2> seen = new Stack<Pt2>();
		if (coplanar_demo)
			for (int k = 0; k < 20; k++) { double az = Math.random(); planApts.push(new Pt3(60*(Math.random()-0.5),60*(az-0.5),10*(az-0.5)));}
		else
			for (int k = 0; k < 20; k++) planApts.push(new Pt3(60*(Math.random()-0.5),60*(Math.random()-0.5),60*Math.random()));
		for (Pt3 p : planApts) seen.push(p.toPt2Im(view, A));
		
		POS0 = new Position(view.R, view.t);
		POS = new Position(seen, planApts, A, null);
		
		print("==============================\n\n\n");
		print("Original values (R, t):");
		POS0.R.print(10,5); print(view.t + "\n\n\n");
		print("Found values (R, t):");
		POS.R.print(10,5);  print(POS.t + "\n\n\n");

		/** Window **/
		size(1024,768);
		plot();
	}
	
	public void draw() {}
	
	public void plot() {
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
		for (Pt3 p : planApts) plot(p);
		if (init_before) {
			plot(POS0,0xFF0000FF);
			plot(POS);
		} else {
			plot(POS);
			plot(POS0,0xFF0000FF);
		}
		fill(color(0xFF000000)); stroke(color(0xFF000000));
		Stack<Drt3> drts = new Stack<Drt3>();
		for (Pt3 p : planApts) {
			Drt3 d = new Drt3(p,p.toPt2Im(POS0, A).toPt3().apply(A.times( POS.R).inverse()).times(-1));
			drts.push(d);
			plot(d);
		}
//		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
//		plot(new Pt3eval(drts).p);		
	}

	
	
	
	
/******************
 **   Movement   **
 ******************/
	

	public void keyPressed() {
		switch (key) {
			case 'z' : view.moveForward( dt); break;
			case 's' : view.moveForward(-dt); break;
			case 'd' : view.moveRight( dt); break;
			case 'q' : view.moveRight(-dt); break;
			case '+' : dt += 0.1; break;
			case '-' : if (dt > 0.1) dt -= 0.1; break;
			case 'r' : setup(); break;
			case 'c' : coplanar_demo = !coplanar_demo; setup(); break;
			case 'a' : init_before = !init_before; break;
		}
		plot();
	}

	public void mouseDragged() {
		view.rotate( (double) (mouseY - mouse_y_)*da/30 ,  (double) (mouseX - mouse_x_)*da/30 );
		mouseMoved();
		plot();
	}
	
	public void mouseMoved() {
		mouse_x_ = mouseX; mouse_y_ = mouseY;
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
	
	public void plot(Stack<Pt3> P) {
		plot(P, WITH_PTS);
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
	
	public void plot(Pt2 a, Pt2 b) {
		if (a == null || b == null) return;
		line((float) a.x,(float) a.y, (float) b.x, (float) b.y);
	}
	
	public void plot(Pt3 a, Pt3 b) {
		if (a == null || b == null) return;
		plot(a.toPt2Im(view, A), b.toPt2Im(view, A));
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
	
}
