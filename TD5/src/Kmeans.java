import processing.core.*;


public class Kmeans {

	ColorPt[] pts;
	
	Kmeans(PImage img, int n) {
		pts = new ColorPt[n];
		System.out.println("Creating the 3D color point set for "+n+" pixels.");
		for(int i=0;i<n;i++) {
			pts[i] = new ColorPt(img.parent.red(img.pixels[i]), img.parent.green(img.pixels[i]), img.parent.blue(img.pixels[i]));
		}
	}
	
	
	
	
}
