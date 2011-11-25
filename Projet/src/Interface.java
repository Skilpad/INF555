
import java.awt.Point;
import java.awt.Stroke;
import java.util.Stack;

import javax.media.opengl.*;
import javax.swing.JFileChooser;

import Jama.Matrix;

import processing.opengl.*;
import processing.core.*;


public class Interface extends PApplet {

	Plan pa, pb, pc;
	
	Position view = new Position(Matrix.identity(3,3), new Pt3(2,2, 10));
	
	Matrix A, dist_coeffs;
	
	Point windowDim = new Point(1024,768);
	
	
	// Movement
	double da = 0.1;
	double dt = 1;
	int mouse_x_;
	int mouse_y_;

//	PGraphicsOpenGL pgl;
//	GL gl;
	
	//** For test
		Plan pA, pB, pC;
	
	
	public void setup() {

		/** Constants & Variables initialization **/
		// Camera calibration
		A = Matrix.identity(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);
		
//		/** Test **/
//		// Points de référence
//		Stack<Pt3> planApts = new Stack<Pt3>();
//		planApts.push(new Pt3(0,0,-297)); planApts.push(new Pt3(210,0,-297)); planApts.push(new Pt3(210,0,0)); planApts.push(new Pt3(0,0,0));
//		
//		// Localisation des autres points
//		Pt3eval p11 = new Pt3eval(), p12 = new Pt3eval(), p13 = new Pt3eval(), p14 = new Pt3eval(), 
//				p21 = new Pt3eval(), p22 = new Pt3eval(), p23 = new Pt3eval(), p24 = new Pt3eval();
//		Stack<Pt2> locA = new Stack<Pt2>();
//		locA.push(new Pt2(419,172)); locA.push(new Pt2(633,217)); locA.push(new Pt2(618,456)); locA.push(new Pt2(436,409)); 
//		Position POS = new Position(locA, planApts, A, null);
//		p11.add(new Pt2( 51,225), POS); p12.add(new Pt2(247,188), POS); p13.add(new Pt2(288,395), POS); p14.add(new Pt2(121,429),POS); 
//		p21.add(new Pt2(826,277), POS); p22.add(new Pt2(949,423), POS); p23.add(new Pt2(782,499), POS); p24.add(new Pt2(874,643),POS); 
//		locA = new Stack<Pt2>();
//		locA.push(new Pt2(404,217)); locA.push(new Pt2(628,178)); locA.push(new Pt2(607,397)); locA.push(new Pt2(421,435)); 
//		POS = new Position(locA, planApts, A, null);
//		p11.add(new Pt2( 52,435), POS); p12.add(new Pt2(228,302), POS); p13.add(new Pt2(272,495), POS); p14.add(new Pt2(134,617),POS); 
//		p21.add(new Pt2(801,169), POS); p22.add(new Pt2(985,249), POS); p23.add(new Pt2(904,444), POS); p24.add(new Pt2(754,368),POS);
//		// Création des plans
//		pA = new Plan(planApts, loadImage("bookcovers2.png"), locA);
//		planApts = new Stack<Pt3>(); locA = new Stack<Pt2>();
//		planApts.push(p11.p); planApts.push(p12.p); planApts.push(p13.p); planApts.push(p14.p);
//		locA.push(new Pt2( 51,225)); locA.push(new Pt2(247,188)); locA.push(new Pt2(288,395)); locA.push(new Pt2(121,429));
//		pB = new Plan(planApts, loadImage("bookcovers1.png"), locA);
//		planApts = new Stack<Pt3>(); locA = new Stack<Pt2>();
//		planApts.push(p21.p); planApts.push(p22.p); planApts.push(p23.p); planApts.push(p24.p);
//		locA.push(new Pt2(801,169)); locA.push(new Pt2(985,249)); locA.push(new Pt2(904,444)); locA.push(new Pt2(754,368));
//		pC = new Plan(planApts, loadImage("bookcovers2.png"), locA);
//		
//		System.out.println("pA >>>>  "+pA.corners3d);
//		System.out.println("pB >>>>  "+pB.corners3d);
//		System.out.println("pC >>>>  "+pC.corners3d);

		/** Window **/
		size(1024,768);
	}
	
	
	public void draw() {
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
		fill(color(0xFF0000FF)); stroke(color(0xFF0000FF));
		plot(new Pt3( 1, 1,-1));
		plot(new Pt3( 1,-1, 1));
		plot(new Pt3( 1,-1,-1));
		plot(new Pt3(-1, 1, 1));
		plot(new Pt3(-1, 1,-1));
		plot(new Pt3(-1,-1, 1));
		plot(new Pt3(-1,-1,-1));
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		plot(new Pt3( 1, 1, 1));
//		view.R.print(5,5);
//		System.out.println(view.t);
		
//		System.out.println("\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		view.R.print(5,5);
//		System.out.println(view.t);
//		fill(color(0xFF000000));
//		image(pA.toPlot(view, A, windowDim),0,0);
//		image(pB.toPlot(view, A, windowDim),0,0);
//		image(pC.toPlot(view, A, windowDim),0,0);
	}

	
	
	
	
/******************
 **   Movement   **
 ******************/
	

	public void keyPressed() {
		switch (key) {
			case 'z' : view.moveForward( dt); break;
			case 's' : view.moveForward(-dt); break;
		}
	}

	public void mouseDragged() {
//		rotate( (double) (mouseX - mouse_x_)/30, (double) (mouseY - mouse_y_)/30 );
		view.rotate( (double) (mouseY - mouse_y_)*da/30 ,  (double) (mouseX - mouse_x_)*da/30 );
		mouseMoved();
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
	
	public void plot(Drt3 d) {
//		Pt3 tcM = d.M.plus(p)
//		if ()
		Pt3 M = d.M.apply(view.R).plus(view.t).apply(A);      //    M = A ( R d.M + t )
		Pt2 N = d.v.apply(view.R).apply(A).toPt2();           //    N = normalize( A R d.v )
		if (N == null) return;
		double Au = N.x; double Bu = M.x - M.z * N.x;         //    u = Au + Bu X
		double Av = N.y; double Bv = M.y - M.z * N.y;         //    v = Av + Bv X    (X in R)
		if (Math.abs(Bu) > Math.abs(Bv)) {     // More horizontal then vertical
			line(0, (float) (Av-Bv*Au/Bu), windowDim.x, (float) (Av+Bv*(windowDim.x-Au)/Bu));
		} else {
			line((float) (Au-Bu*Av/Bv), 0, (float) (Au+Bu*(windowDim.y-Av)/Bv), windowDim.y); 
		}
	}
	
}
