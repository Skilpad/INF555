import java.util.Stack;
import processing.core.*;
import Jama.Matrix;

public class Tests extends PApplet {

//	public static void main(String args[]) {
//		
//		Stack<Pt> from = new Stack<Pt>();
//		Stack<Pt> to   = new Stack<Pt>();
//		from.push(new Pt(831,281));
//		from.push(new Pt(948,423));
//		from.push(new Pt(788,505));
//		from.push(new Pt(874,641));
//		to.push(new Pt(811,173));
//		to.push(new Pt(985,251));
//		to.push(new Pt(763,371));
//		to.push(new Pt(904,443));
//		
//		Matrix h = Homographie.find(from, to);
//		h.print(9,2);
//		
//		
//	}
//	
	
	PImage a;
	Stack<Pt> from;
	Stack<Pt> to;
	
	
	public void setup() {
		from = new Stack<Pt>();
		to   = new Stack<Pt>();
		a = loadImage("otter.png");
		size(a.width,a.height);
		image(a, 0, 0);
	}
	
	public void draw() {
	}
	
	public void mouseClicked() {
		System.out.println(mouseX + "," + mouseY);
		from.push(new Pt(mouseX, mouseY));
		fill(color(255,0,0)); stroke(color(255,0,0));
		ellipse(mouseX, mouseY, 4, 4);
	}
	
	
}
