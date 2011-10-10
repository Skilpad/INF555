//============================================================================
//
//                           MODE D'EMPLOI
//
//============================================================================
//
//    Cliquer une première fois pour démarrer la description d'une droite,
//    une seconde fois pour terminer sa description.
//    
//    Pour annuler une description en court, faire un clic droit.
//
//    Après avoir décrit une droite dans la première image,
//    décrire la droite correspondante dans la seconde.
//
//    Une fois qu'au moins 4 droites ont ainsi été décrites dans chaque image
//    (il faut donc être sur la première image), tapper espace pour calculer
//    l'homographie correspondante.
//
//    Un apperçu de son application est alors affiché.
//    
//    Retapper espace pour avoir un apperçu de l'application de son inverse.
//
//============================================================================



import java.util.Stack;
import processing.core.*;
import Jama.Matrix;

public class LinesToHomographie_Applet extends PApplet {

	PImage from_img;
	PImage to_img;
	PImage from_img_gray;
	PImage to_img_gray;

	PImage from_distorted;
	PImage to_distorted;
	
	Stack<Vect> from;
	Stack<Vect> to;
	
	int clicked;
	
	Pt firstPt;
	
	
	public void setup() {
		clicked = 0;
		from = new Stack<Vect>();
		to   = new Stack<Vect>();
		from_img = loadImage("bookcovers1.png");
		to_img   = loadImage("bookcovers2.png");
		from_img_gray = loadImage("bookcovers1.png");
		to_img_gray   = loadImage("bookcovers2.png");
		from_img_gray.filter(GRAY);
		to_img_gray.filter(GRAY);
		firstPt = new Pt(-1, -1);
		size(Math.max(from_img.width, to_img.width), Math.max(from_img.height,to_img.height));
		drawCurrent();
	}
			
	
	public void draw() {
		if (firstPt.x < 0) return;
		drawCurrent();
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		line(firstPt.x, firstPt.y, mouseX, mouseY);
	}
	
	private void drawCurrent() {
		background(0);
		image(((clicked==0) ? from_img : to_img), 0, 0);
		plotLines();		
	}
	
	public void mouseClicked() {
		if (clicked < 0) 		  return;
		if (mouseButton == RIGHT) { 
			firstPt = new Pt(-1,-1);
			drawCurrent();
			return;
		}
		if (firstPt.x < 0) {
			firstPt = new Pt(mouseX, mouseY);
		} else {
			if (clicked == 0) {
				clicked = 1;
				from.push(new Vect(firstPt, new Pt(mouseX, mouseY)));
				firstPt = new Pt(-1,-1);
			} else {
				clicked = 0;
				to.push(new Vect(firstPt, new Pt(mouseX, mouseY)));
				firstPt = new Pt(-1,-1);
			}
			drawCurrent();
		}
	}
	
	private void plotLines() {
		for (Vect v : ((clicked==0) ? from : to) ) plotLine(v);
	}
	
	public void plotLine(Vect v) {
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		if (v.a == 0) { 
			float w = ((clicked==0) ? from_img : to_img).width;
			line(0, -v.c/v.b, w, -(v.a*w+v.c)/v.b);
		} else {
			float h = ((clicked==0) ? from_img : to_img).height;
			line(-v.c/v.a, 0, -(v.b*h+v.c)/v.a, h);
		}
	}
	
	
	public void keyPressed() {
		if (clicked == -1) {
			tint(255, 255, 255, 255); 
			background(0);
			image(to_img, 0, 0);
			tint(255, 255, 255, 126); 
			image(from_distorted, 0, 0);
			fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(32);
			text("Forward Mapping", 20, 50);
		}
		if ( !( key == ' ' && clicked == 0 && from.size() > 3 ) ) return;
		Matrix h = Homographie.find(from, to);
		System.out.println("The Homography Matrix is: "); h.print(10,2);
		to_distorted   = Homographie.invert(h, to_img); 
		from_distorted = Homographie.apply(h, from_img); 
		image(from_img, 0, 0);
		tint(255, 255, 255, 126); 
		image(to_distorted, 0, 0);
		fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(32);
		text("Backward Mapping", 20, 50);
		clicked = -1;
	}
	
}
