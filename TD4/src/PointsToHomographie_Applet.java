//============================================================================
//
//                           MODE D'EMPLOI
//
//============================================================================
//
//    Cliquer sur l'image pour indiquer un point caractéristique.
//    Cliquer alors sur la seconde image pour indiquer le point correspondant.
//
//    Une fois qu'au moins 4 points ont ainsi été décrits dans chaque image
//    (il faut donc être sur la première image), tapper espace pour calculer
//    l'homographie correspondante.
//
//    Un apperçu de son application est alors affiché.
//    
//    Retapper espace pour avoir un apperçu de l'application de son inverse.
//    
//    NB: En remplaçant dans la fonction setup() "wide_screen = false;" par
//        "wide_screen = true;", l'interface est adaptée à un écran large,
//        capable d'afficher les deux images l'une à côté de l'autre.
//
//============================================================================



import java.util.Stack;
import processing.core.*;
import Jama.Matrix;

public class PointsToHomographie_Applet extends PApplet {

	PImage from_img;
	PImage to_img;
	PImage from_img_gray;
	PImage to_img_gray;

	PImage from_distorted;
	PImage to_distorted;
	
	
	boolean wide_screen;
	
	Stack<Pt> from;
	Stack<Pt> to;
	
	int clicked;
	
	public void setup() {
		wide_screen = false;
//		wide_screen = true;
		clicked = 0;
		from = new Stack<Pt>();
		to   = new Stack<Pt>();
		from_img = loadImage("bookcovers1.png");
		to_img   = loadImage("bookcovers2.png");
		from_img_gray = loadImage("bookcovers1.png");
		to_img_gray   = loadImage("bookcovers2.png");
		from_img_gray.filter(GRAY);
		to_img_gray.filter(GRAY);
		if (wide_screen) {
			size(from_img.width + 5 + to_img.width, Math.max(from_img.height,to_img.height));
			background(0);
			image(from_img, 0, 0);
			image(to_img, from_img.width + 5, 0);
		} else {
			size(Math.max(from_img.width, to_img.width), Math.max(from_img.height,to_img.height));
			background(0);
			image(from_img, 0, 0);
		}
	}
			
	
	public void draw() {}
	
	public void mouseClicked() {
		if (clicked < 0) return;
		if (wide_screen) {
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
		} else {
			if (clicked == 0) {
				background(0);
				image(to_img, 0, 0);
				clicked = 1;
				from.push(new Pt(mouseX, mouseY));
			} else {
				background(0);
				image(from_img, 0, 0);
				clicked = 0;
				to.push(new Pt(mouseX, mouseY));
			}
			plotPoints();
		}
	}
	
	private void plotPoints() {
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		if (wide_screen) {
			for (Pt p : from)   ellipse((int) p.x, (int) p.y, 4, 4);
			for (Pt p : to  )   ellipse((int) p.x + from_img.width + 5, (int) p.y, 4, 4);
		} else {
			for (Pt p : ((clicked==0) ? from : to) )   ellipse((int) p.x, (int) p.y, 4, 4);
		}
	}
	
	public void keyPressed() {
		if (!wide_screen && clicked == -1) {
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
		if (wide_screen) {
			image(from_img, 0, 0);
			image(to_img, from_img.width + 5, 0);
			tint(255, 255, 255, 126); 
			image(to_distorted, 0, 0);
			image(from_distorted, from_img.width + 5, 0);
			fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(32);
			text("Backward Mapping", 20, 50); 
			text("Forward Mapping", from_img.width + 25, 50); 
			clicked = -1;
		} else {
			image(from_img, 0, 0);
			tint(255, 255, 255, 126); 
			image(to_distorted, 0, 0);
			fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(32);
			text("Backward Mapping", 20, 50);
			clicked = -1;
		}
	}
	
}
