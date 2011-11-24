import java.util.Stack;

import Jama.Matrix;
import processing.core.PApplet;


public class InterfaceMiniball extends PApplet {
	
	int nPoints = 3;
	
	int size = 300;
	
	public void setup() {
		size(2*size, 2*size);
		keyPressed();
	}
	
	public void draw() {}
	
	public void keyPressed() {
		if (key == '+') nPoints++;
		if (key == '-') nPoints--;
		// Preparing
		background(0xFFFFFFFF);
		Stack<Pt> P = new Stack<Pt>();
		for (int k = 0; k < nPoints; k++) P.push(new Pt(Math.random(), Math.random()));
		Miniball mb = new Miniball(P);
		// Show
		plot(mb);
		plot(P);
	}
	
	public void plot(Stack<Pt> P) {
		for (Pt p : P) plot(p);
	}

	public void plot(Pt p) {
		fill(color(0xFF0000FF)); stroke(color(0xFF0000FF));
		ellipse((int) (size*(0.5+p.x)), (int) (size*(0.5+p.y)), 4, 4);
	}
	
	public void plot(Miniball mb) {
		fill(color(0xFFFFFFFF)); stroke(color(0xFFFF0000));
		ellipse((int) (size*(0.5+mb.C.x)), (int) (size*(0.5+mb.C.y)), (int) (2*size*mb.r), (int) (2*size*mb.r));
	}

}
