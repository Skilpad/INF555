import Jama.Matrix;
import processing.core.PApplet;
import processing.core.PImage;


public class BlackNwhite {

	public static PImage blackNwhite(PImage img, int threshold[], int values[], PApplet pa) {
		PImage res = new PImage(img.width, img.height);
//		PApplet pa = new PApplet();
		for (int x = 0; x < img.width; x++) {
			for (int y = 0; y < img.height; y++) {
				int c = img.get(x,y);
				c = (int) (0.3*pa.red(c) + 0.59*pa.green(c) + 0.11*pa.blue(c));
				int value = 0;
				for (int i = 0; i < threshold.length; i++) {
					if (c < threshold[i]) break;
					value = values[i+1];
				}
				res.set(x, y, value);
			}
		}
		return res;
	}
		
}
