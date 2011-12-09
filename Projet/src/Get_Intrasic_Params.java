
import java.awt.Point;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JFileChooser;

import Jama.Matrix;

import processing.opengl.*;
import processing.core.*;


public class Get_Intrasic_Params extends PApplet {

	
	Pt2[] pts;
	int n;
	
	double ft = 0;
	int k = 0;
	
	PImage img;
	
	
	public void setup() {
		pts = new Pt2[10];
		n = 0;
		openF();
		fill(0xFFFF0000); stroke(0xFFFF0000);
		size(img.width, img.height);
	}
	
	public void draw() {
		image(img,0,0);
		for (int k = 0; k+1 < n; k += 2) plot(pts[k], pts[k+1]);
		if (n%2 == 1) plot(pts[n-1], new Pt2(mouseX, mouseY));
		if (n==8) {
			plot(pts[8]);
			plot(pts[9]);
		}
	}
	
	public void mouseClicked() {
		if (n == 8) { setup(); return; }
		if (mouseButton == LEFT) {
			pts[n] = new Pt2(mouseX, mouseY);
			n++;
		} else {
			n--;
		}
		if (n == 8) giveParams();
	}
	
	
	public void plot(Pt2 p) {
		if (p == null) return;
		ellipse((float) p.x, (float) p.y, 4, 4);
	}
	
	private void plot(Pt2 a, Pt2 b) {
		if (a == null || b == null) return;
		line((float) a.x,(float) a.y, (float) b.x, (float) b.y);
	}
	
	private void openF() {
		String f = selectInput();
		if (f != null) {
			img = loadImage(f);
		}
	}
	
	private Pt2 intersec(Pt2 a, Pt2 b, Pt2 c, Pt2 d) {
		Matrix A = new Matrix(2,2);
		A.set(0,0, b.x-a.x); A.set(0,1, c.x-d.x);
		A.set(1,0, b.y-a.y); A.set(1,1, c.y-d.y);
		Pt2 lm = c.minus(a).apply(A.inverse());
		return a.plus(b.minus(a).times(lm.x));
	}
	
	
	private void giveParams() {
		pts[8] = intersec(pts[0], pts[1], pts[2], pts[3]);
		pts[9] = intersec(pts[4], pts[5], pts[6], pts[7]);
		double a = pts[8].x, b = pts[8].y, d = pts[9].x, e = pts[9].y;
		double px = img.width/2, py = img.height/2;
		double f = Math.sqrt( - ( a*(d-px) + b*(e-py) + ((px*px+py*py) - d*px-e*py ) ) );
		ft += f; k++;
		println("=========================== \n");
		println("px:  "+px);
		println("py:  "+py);
		println("f:   "+(ft/k) );
	}
	
	
}


