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
	
	PImage from_img;
	PImage to_img;
	PImage from_img_gray;
	PImage to_img_gray;
	
	Stack<Pt> from;
	Stack<Pt> to;
	
	int clicked;
	
	public void setup() {
		from = new Stack<Pt>();
		to   = new Stack<Pt>();
		from_img = loadImage("bookcovers1.png");
		to_img   = loadImage("bookcovers2.png");
		from_img_gray = loadImage("bookcovers1.png");
		to_img_gray   = loadImage("bookcovers2.png");
		from_img_gray.filter(GRAY);
		to_img_gray.filter(GRAY);
		size(from_img.width + 5 + to_img.width, Math.max(from_img.height,to_img.height));
		background(0);
		image(from_img, 0, 0);
		image(to_img, from_img.width + 5, 0);
	}
	
	public void draw() {}
	
	public void mouseClicked() {
		if (clicked < 0) return;
		if (mouseX < from_img.width) {
			if (clicked == 1) return;
			if (clicked == 0) {
				clicked = 1;
				image(from_img_gray, 0, 0);
			} else {
				clicked = 0;
				image(to_img, from_img.width + 5, 0);
			}
			from.push(new Pt(mouseX, mouseY));
		} else if (mouseX > from_img.width + 5) {
			if (clicked == 2) return;
			if (clicked == 0) {
				clicked = 2;
				image(to_img_gray, from_img.width + 5, 0);
			} else {
				clicked = 0;
				image(from_img, 0, 0);
			}
			to.push(new Pt(mouseX - from_img.width - 5, mouseY));
		}
		plotPoints();
	}
	
	private void plotPoints() {
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		for (Pt p : from)   ellipse((int) p.x, (int) p.y, 4, 4);
		for (Pt p : to  )   ellipse((int) p.x + from_img.width + 5, (int) p.y, 4, 4);
	}
	
	public void keyPressed() {
		if ( !( key == ' ' && clicked == 0 && from.size() > 3 ) ) return;
		Matrix h = Homographie.find(from, to);
		System.out.println("The Homography Matrix is: "); h.print(10,2);
		PImage to_distorted   = Homographie.invert(h, to_img); 
		PImage from_distorted = Homographie.apply(h, from_img); 
		image(from_img, 0, 0);
		image(to_img, from_img.width + 5, 0);
		tint(255, 255, 255, 126); 
		image(to_distorted, 0, 0);
		image(from_distorted, from_img.width + 5, 0);
		fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(32);
		text("Backward Mapping", 20, 50); 
		text("Forward Mapping", from_img.width + 25, 50); 
		clicked = -1;
	}
	
}









