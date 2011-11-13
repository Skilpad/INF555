
import java.awt.Point;
import java.util.Stack;

import javax.media.opengl.*;
import javax.swing.JFileChooser;

import Jama.Matrix;

import processing.opengl.*;
import processing.core.*;


public class Interface extends PApplet {

	Plan pa, pb, pc;
	
	Position view;
	
	Matrix A, dist_coeffs;
	
	Pt3 up    = new Pt3(0,0, 1);
	Pt3 down  = new Pt3(0,0,-1);
	Matrix left;
	Matrix right;

	Point windowDim = new Point(1024,768);

//	PGraphicsOpenGL pgl;
//	GL gl;
	
	//** For test
		Plan pA, pB, pC;
	
	
	public void setup() {

		/** Constants & Variables initialization **/
		// Movements
		double da = 0.1;
		left = new Matrix(3,3); right = new Matrix(3,3);
		left.set(0,0,  Math.cos(da)); left.set(1,1, Math.cos(da)); left.set(2,2, 1); 
		left.set(0,1, -Math.sin(da)); left.set(1,0, Math.sin(da));
		right.set(0,0, Math.cos(da)); right.set(1,1,  Math.cos(da)); right.set(2,2, 1); 
		right.set(0,1,-Math.sin(da)); right.set(1,0, -Math.sin(da));
		Matrix R = new Matrix(3,3); R.set(2,0, 1); R.set(1,1, 1); R.set(0,2, -1);
		Pt3 t = new Pt3(-10,0, 10);
		view = new Position(R,t);
		// Camera calibration
		A = new Matrix(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);
		
		/** Test **/
		// Points de référence
		Stack<Pt3> planApts = new Stack<Pt3>();
		planApts.push(new Pt3(0,0,-297)); planApts.push(new Pt3(210,0,-297)); planApts.push(new Pt3(210,0,0)); planApts.push(new Pt3(0,0,0));
		
		// Localisation des autres points
		Pt3eval p11 = new Pt3eval(), p12 = new Pt3eval(), p13 = new Pt3eval(), p14 = new Pt3eval(), 
				p21 = new Pt3eval(), p22 = new Pt3eval(), p23 = new Pt3eval(), p24 = new Pt3eval();
		Stack<Pt2> locA = new Stack<Pt2>();
		locA.push(new Pt2(419,172)); locA.push(new Pt2(633,217)); locA.push(new Pt2(618,456)); locA.push(new Pt2(436,409)); 
		Position POS = new Position(locA, planApts, A, null);
		p11.add(new Pt2( 51,225), POS); p12.add(new Pt2(247,188), POS); p13.add(new Pt2(288,395), POS); p14.add(new Pt2(121,429),POS); 
		p21.add(new Pt2(826,277), POS); p22.add(new Pt2(949,423), POS); p23.add(new Pt2(782,499), POS); p24.add(new Pt2(874,643),POS); 
		locA = new Stack<Pt2>();
		locA.push(new Pt2(404,217)); locA.push(new Pt2(628,178)); locA.push(new Pt2(607,397)); locA.push(new Pt2(421,435)); 
		POS = new Position(locA, planApts, A, null);
		p11.add(new Pt2( 52,435), POS); p12.add(new Pt2(228,302), POS); p13.add(new Pt2(272,495), POS); p14.add(new Pt2(134,617),POS); 
		p21.add(new Pt2(801,169), POS); p22.add(new Pt2(985,249), POS); p23.add(new Pt2(904,444), POS); p24.add(new Pt2(754,368),POS);
		// Création des plans
		pA = new Plan(planApts, loadImage("bookcovers2.png"), locA);
		planApts = new Stack<Pt3>(); locA = new Stack<Pt2>();
		planApts.push(p11.p); planApts.push(p12.p); planApts.push(p13.p); planApts.push(p14.p);
		locA.push(new Pt2( 51,225)); locA.push(new Pt2(247,188)); locA.push(new Pt2(288,395)); locA.push(new Pt2(121,429));
		pB = new Plan(planApts, loadImage("bookcovers1.png"), locA);
		planApts = new Stack<Pt3>(); locA = new Stack<Pt2>();
		planApts.push(p21.p); planApts.push(p22.p); planApts.push(p23.p); planApts.push(p24.p);
		locA.push(new Pt2(801,169)); locA.push(new Pt2(985,249)); locA.push(new Pt2(904,444)); locA.push(new Pt2(754,368));
		pC = new Plan(planApts, loadImage("bookcovers2.png"), locA);
		
		System.out.println("pA >>>>  "+pA.corners3d);
		System.out.println("pB >>>>  "+pB.corners3d);
		System.out.println("pC >>>>  "+pC.corners3d);

		/** Window **/
		size(1024,768);
	}
	
	public void keyPressed() {
		switch (keyCode) {
			case UP   : view.moveForward(1);  break;
			case DOWN : view.moveForward(-1); break;
			case LEFT : break;
			case RIGHT: break;
		}
	}
	
	
	public void draw() {
		System.out.println("\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		view.R.print(5,5);
		System.out.println(view.t);
		fill(color(0xFF000000));
		image(pA.toPlot(view, A, windowDim),0,0);
		image(pB.toPlot(view, A, windowDim),0,0);
		image(pC.toPlot(view, A, windowDim),0,0);
	}

	
	
	
//	int nbRep       = 5;
//	double epsilon  = 0.0000000001;
//	String[] images = {"polytechnique.png","tux.png","firefox.png","pencils.png","blogcat.png"};
//	
//
//	PImage img;
//	int indexImg = -2;
//	PGraphicsOpenGL pgl;
//	GL gl;
//	PImage res;
//
//	Kmeans pts;
//	
//	float xmag, ymag = 0;
//	float newXmag, newYmag = 0;
//	
//	boolean kmeans = false;
//	
//	int view = 0;
//	
//	
//	public void setup() {
//		size(512, 512, OPENGL);
//		colorMode(RGB, 1, 1, 1);
//		pgl = (PGraphicsOpenGL) g;
//		gl = pgl.beginGL();
//		fill(color(0xFFFFFFFF)); textSize(20);
//	}
//	
//	public void draw() {
//		if (indexImg == -2) { setImg(0); return; }
//		if (indexImg >= 0)  { return; }
//		if (pts == null)    { return; }
//		background(0);
//		if (view == 2) {
//			showImage(res);
//			textAlign(RIGHT);
//			text("Initialization: " + ((kmeans) ? "k-means++" : "Random"), 482, 30);
//			text("N: " + nbRep, 482, 50);
//			textAlign(LEFT);
//			text("Iteration: " + ((pts.rep.length == 0) ? "-" : pts.it), 20, 50);
//			text("Loss: " +      ((pts.rep.length == 0) ? "-" : pts.loss), 20, 80);
//		} else {
//			textAlign(RIGHT);
//			text("Initialization: " + ((kmeans) ? "k-means++" : "Random"), 482, 30);
//			text("N: " + nbRep, 482, 50);
//			textAlign(LEFT);
//			text("Iteration: " + ((pts.rep.length == 0) ? "-" : pts.it), 20, 50);
//			text("Loss: " +      ((pts.rep.length == 0) ? "-" : pts.loss), 20, 80);
//			text("Radius <=> ", 20, 450);
//			text(((view == 0) ? "Maximal distance to \ncorresponding points" : "Number of \ncorresponding points"), 150, 450);
//			gl.glMatrixMode(GL.GL_PROJECTION);
//			gl.glLoadIdentity();
//			gl.glOrtho(-2,2,-2,2,-100,100);  
//			gl.glMatrixMode(GL.GL_PROJECTION);
//			gl.glLoadIdentity();
//			gl.glOrtho(-2,2,-2,2,-100,100);  
//			renderPointSet(); 
//		}
//	}
//
//	public void renderPointSet() { 
//		gl.glMatrixMode(GL.GL_MODELVIEW);
//		gl.glLoadIdentity();
//		
//		newXmag = ((float) mouseX)/width * 360;
//		newYmag = ((float) mouseY)/height * 360;
//		 
//		float diff = xmag-newXmag;
//		if (abs(diff) >  0.01) { xmag -= diff/4.0; }
//		
//		diff = ymag-newYmag;
//		if (abs(diff) >  0.01) { ymag -= diff/4.0; }
//		  
//		gl.glRotatef(+ymag,1,0,0);
//		gl.glRotatef(-xmag,0,1,0);
//		gl.glTranslatef((float) -0.5, (float) -0.5, (float) -0.5); 
//
//		plot(pts);
//		
//		plotEdges();
//	}
//	
//	public void keyPressed() {
//		if (indexImg >= 0) {
//			switch (keyCode) {
//				case LEFT : setImg(indexImg+images.length-1); break;
//				case RIGHT: setImg(indexImg+1);               break;
//				case ENTER: setImg(-1);                       break;
//				case 79   : openF();                           break;
//			}
//		} else {
//			if (pts == null) { initPts(); res = img; return; }
//			switch (key) {
//				case ' ' :  if (pts.rep.length == 0) pts.init(nbRep, kmeans); else pts.iterate(); 
//							if (pts.rep.length == 0) res = img; else res = pts.apply(img); 
//							break;
//				case '\n':  if (pts.rep.length == 0) pts.init(nbRep, kmeans);
//							double l0 = 0;
//							while (l0 - pts.loss > epsilon || l0 == 0) {
//								l0 = pts.loss;
//								pts.iterate();
//							}
//							if (pts.rep.length == 0) res = img; else res = pts.apply(img); 
//							break;
//				case 'r' :  initPts(); res = img; break;
//				case 'v' :  view = (view+1)%3; break;
//				case 'i' :  kmeans   = !kmeans;   break;
//				case '+' :  nbRep++; break;
//				case '-' :  nbRep--; break;
//				case 's' :  saveF(); break;
//				case '0' :  pts = null; indexImg = -2; break;
//			}
//		}
//	}
//	
//	private void plot(Kmeans pts) {
//		if (pts.rep.length == 0) {
//			gl.glPointSize(1);
//			gl.glBegin(GL.GL_POINTS);
//			for (ColorPt p : pts.pts) {
//				gl.glColor3f(p.r,p.g,p.b);
//				gl.glVertex3f(p.r,p.g,p.b);
//			}
//			gl.glEnd();
//		} else {
//			for (int i = 0; i < pts.rep.length; i++) {
//				gl.glPointSize((view == 0) ? 100 * (float) Math.sqrt(pts.repDist2[i]) : pts.repSize[i]*100/pts.pts.length);
//				gl.glBegin(GL.GL_POINTS);
//				ColorPt p = pts.rep[i];
//				gl.glColor3f(p.r,p.g,p.b);
//				gl.glVertex3f(p.r,p.g,p.b);
//				gl.glEnd();
//			}
//		}
//	}
//	
//	private void plotEdges() {
//		gl.glBegin(GL.GL_LINES);
//		
//			 gl.glColor3f(0,0,0);
//			gl.glVertex3f(0,0,0);
//			 gl.glColor3f(0,0,1);
//			gl.glVertex3f(0,0,1);
//			gl.glVertex3f(0,0,1);
//			 gl.glColor3f(0,1,1);
//			gl.glVertex3f(0,1,1);
//			gl.glVertex3f(0,1,1);
//			 gl.glColor3f(0,1,0);
//			gl.glVertex3f(0,1,0);
//			gl.glVertex3f(0,1,0);
//			 gl.glColor3f(0,0,0);
//			gl.glVertex3f(0,0,0);
//
//			 gl.glColor3f(1,0,0);
//			gl.glVertex3f(1,0,0);
//			 gl.glColor3f(1,0,1);
//			gl.glVertex3f(1,0,1);
//			gl.glVertex3f(1,0,1);
//			 gl.glColor3f(1,1,1);
//			gl.glVertex3f(1,1,1);
//			gl.glVertex3f(1,1,1);
//			 gl.glColor3f(1,1,0);
//			gl.glVertex3f(1,1,0);
//			gl.glVertex3f(1,1,0);
//			 gl.glColor3f(1,0,0);
//			gl.glVertex3f(1,0,0);
//
//			 gl.glColor3f(0,0,0);
//			gl.glVertex3f(0,0,0);
//			 gl.glColor3f(1,0,0);
//			gl.glVertex3f(1,0,0);
//			 gl.glColor3f(0,0,1);
//			gl.glVertex3f(0,0,1);
//			 gl.glColor3f(1,0,1);
//			gl.glVertex3f(1,0,1);
//			 gl.glColor3f(0,1,0);
//			gl.glVertex3f(0,1,0);
//			 gl.glColor3f(1,1,0);
//			gl.glVertex3f(1,1,0);
//			 gl.glColor3f(0,1,1);
//			gl.glVertex3f(0,1,1);
//			 gl.glColor3f(1,1,1);
//			gl.glVertex3f(1,1,1);
//
//		gl.glEnd();
//	}
//
//	private void initPts() {
//		pts = new Kmeans(img); 
//	}
//	
//	private void setImg(int i) {
//		indexImg = i % images.length;
//		if (indexImg >= 0) {
//			img = loadImage(images[indexImg]);
//			showImage(img);
//			fill(color(0xFFAAAAAA));
//			text("Choose an image.\n(Use arrows. Confirm with ENTER.)", 20, 30) ;
//			text("Press O to open a personal image", 20, 490) ;
//			fill(color(0xFFFFFFFF));
//		} else {
//			background(0);
//			text("Manual: \n\n" +
//				 "  - Use mouse to rotate view. \n" +
//				 "  - Press SPACE to run a single iteration \n" +
//				 "     of the algorithm. \n" +
//				 "  - Press ENTER to run the algorithm \n" +
//				 "     until stabilisation. \n" +
//				 "  - Press R to run again from start \n" +
//				 "     (with new seeds). \n" +
//				 "  - Press V to switch view (change radius meaning\n" +
//				 "     or show corresponding image). \n" +
//				 "  - Press I to change initialization mode. \n" +
//				 "  - Press +/- to change the number of seeds \n" +
//				 "     (applied when the algorithme initializes). \n" +
//				 "  - Press S to save the result. \n" +
//				 "  - Press 0 to restart the program. \n\n " +
//				 "Press any key to start.", 20, 50);
//		}
//	}
//	
//	private void showImage(PImage img) {
//		background(0);
//		int m = Math.max(img.width, img.height);
//		float h = 512*((float) img.height)/m ;
//		float w = 512*((float) img.width)/m ;
//		image(img,(512-w)/2,(512-h)/2,w,h);
//	}
//	
//	
//	private void openF() {
//		String f = selectInput();
//		if (f != null) {
//			img = loadImage(f);
//			setImg(-1);
//		}
//	}
//
//	private void saveF() {
//		String f = selectOutput();
//		if (f != null) res.save(f);
//	}

}
