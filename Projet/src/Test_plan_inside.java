import java.util.Stack;

import processing.core.PApplet;
import processing.core.PImage;
import Jama.Matrix;


public class Test_plan_inside extends PApplet {

	Stack<Pt2> pts;
	Stack<Pt2> tsts;
	
	Stack<Pt3> lp3;
	
	public void setup() {
		pts  = new Stack<Pt2>();
		tsts = new Stack<Pt2>();
		for (int i = 0; i < 800; i+=10) for (int j = 0; j < 600; j+=10) tsts.push(new Pt2(i,j));
		lp3 = new Stack<Pt3>(); lp3.push(new Pt3());
		size(800,600);
		background(0xFFFFFFFF);
	}
	
	public void draw() {}
	
	public void plot() {
		background(0xFFFFFFFF);
		Pt2 a = null, b = null;
		fill(0xFFFF0000); stroke(0xFFFF0000);
		for (Pt2 p : pts)
			{ if (a == null) a = p; plot(b,p); b = p; }
		fill(0xFF00FF00); stroke(0xFF00FF00);
		plot(a,b);
		if (pts.size() < 3) return;
		Plan pl = new Plan(lp3, null, pts);
		for (Pt2 p : tsts) {
			if (pl.inside(p)) { fill(0xFF00FF00); stroke(0xFF00FF00); }
			else 			  { fill(0xFFFF0000); stroke(0xFFFF0000); }
			plot(p);
		}
	}
	
	public void mouseClicked() {
		switch (mouseButton) {
			case LEFT:
				pts.push(new Pt2(mouseX,mouseY));
				break;
			case RIGHT:
				setup();
				break;
		}
		plot();
	}
	
	
	public void plot(Pt2 p) {
		if (p == null) return;
		ellipse((float) p.x, (float) p.y, 4, 4);
	}
	
	private void plot(Pt2 a, Pt2 b) {
		if (a == null || b == null) return;
		line((float) a.x,(float) a.y, (float) b.x, (float) b.y);
	}
	

	

}
