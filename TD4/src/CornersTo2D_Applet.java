
import java.util.Stack;
import processing.core.*;
import Jama.Matrix;

public class CornersTo2D_Applet extends PApplet {

	PImage from_img;
	PImage from_distorted;	
	Stack<Pt> from;
	int n;
	
	public void setup() {
		from = new Stack<Pt>();
//		from_img = loadImage("bookcovers1.png");
		from_img = loadImage("echec1.jpg");
		n = 0;
		size(from_img.width, from_img.height);
		textSize(32);
		show_img();
	}
			
	
	public void draw() {}
	
	public void show_img() {
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		image(from_img, 0, 0);
		for (Pt p : from) { ellipse((int) p.x, (int) p.y, 4, 4); }
		ellipse(mouseX, mouseY, 4, 4);
		text("Select " + ((n < 2) ? "upper " : "lower ") + ((n == 0 || n == 3) ? "left" : "right") + " corner" , 20, 50);
	}
	
	public void mouseClicked() {
		if (n >= 0) from.push(new Pt(mouseX, mouseY));
		n++;
		if (from.size() < 4) {
			show_img();
		} else {
			Stack<Pt> to = new Stack<Pt>();
			int m = Math.min(from_img.height, from_img.width);
			to.push(new Pt(0,0));
			to.push(new Pt(m,0));
			to.push(new Pt(m,m));
			to.push(new Pt(0,m));
			Matrix h = Homographie.find(from, to);
			System.out.println("The Homography Matrix is: "); h.print(20,15);
			from_distorted = Homographie.apply(h, from_img);
			background(0);
			image(from_distorted, (from_img.width-m)/2, (from_img.height-m)/2);
			fill(color(0xFF000000)); stroke(color(0xFF000000));
			rect(1+m+(from_img.width-m)/2, 0, from_img.width, from_img.height);
		}
	}
	
}
