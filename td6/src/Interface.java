import processing.core.PApplet;
import processing.core.PImage;


public class Interface extends PApplet {

	PImage src, dst;
	EfrosLeung pgm;

	public void setup() { 
		Constants.pApplet = this;
		colorMode(RGB, 1); 
		size(Constants.destWidth, Constants.destHeight);
		src = loadImage("grass.png");
		println(src.width + " - " + src.height);
		dst = new PImage(Constants.destWidth, Constants.destHeight);
		pgm = new EfrosLeung(src, dst);
		pgm.start();
	}

	public void draw() {
		image(dst, 0, 0);
	}
	
}
