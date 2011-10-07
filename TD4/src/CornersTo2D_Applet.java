
import java.util.Stack;
import processing.core.*;
import Jama.Matrix;

public class CornersTo2D_Applet extends PApplet {

	PImage from_img;
	PImage from_distorted;	
	Stack<Pt> from;
	
	public void setup() {
		from = new Stack<Pt>();
		from_img = loadImage("bookcovers1.png");
		size(from_img.width, from_img.height);
		image(from_img, 0, 0);
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
	}
			
	
	public void draw() {}
	
	public void mouseClicked() {
		from.push(new Pt(mouseX, mouseY));
		ellipse(mouseX, mouseY, 4, 4);
		if (from.size() == 4) {
			Stack<Pt> to = new Stack<Pt>();
			int m = Math.min(from_img.height, from_img.width);
			to.push(new Pt(0,0));
			to.push(new Pt(m,0));
			to.push(new Pt(m,m));
			to.push(new Pt(0,m));
			Matrix h = Homographie.find(from, to);
			System.out.println("The Homography Matrix is: "); h.print(10,2);
			from_distorted = Homographie.apply(h, from_img);
			background(0);
			image(from_distorted, (from_img.width-m)/2, (from_img.height-m)/2);
			line(  (from_img.width-m)/2,   (from_img.height-m)/2, m+(from_img.width-m)/2, (from_img.height-m)/2);
			line(m+(from_img.width-m)/2,   (from_img.height-m)/2, m+(from_img.width-m)/2, m+(from_img.height-m)/2);
			line(m+(from_img.width-m)/2, m+(from_img.height-m)/2,   (from_img.width-m)/2, m+(from_img.height-m)/2);
		}
	}
	
}
