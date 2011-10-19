import processing.core.PImage;


public class Representant {

	float[] values;
	int     color;
	
	public Representant(PImage img, int x, int y) {
		this.color = img.get(x, y);
		int s = Constants.s;
		int i = 0;
		values = new float[6*s*(s+1)];
		for (int xx = x-s; xx <= x+s; xx++) {
			for (int yy = y-s; yy < y; yy++) {
				int c = img.get(xx,yy);
				values[i] = Constants.pApplet.red(c);   i++;
				values[i] = Constants.pApplet.green(c); i++;
				values[i] = Constants.pApplet.blue(c);  i++;
			}
		}
		for (int xx = x-s; xx < x; xx++) {
				int c = img.get(xx,0);
				values[i] = Constants.pApplet.red(c);   i++;
				values[i] = Constants.pApplet.green(c); i++;
				values[i] = Constants.pApplet.blue(c);  i++;
		}
	}
	
	public float dist2(Representant rep) {
		float d = 0;
		for (int i = 0; i < values.length; i++)
			d += (values[i]-rep.values[i])*(values[i]-rep.values[i]);
		return d;
	}

	public float dist(Representant rep) {
		return (float) Math.sqrt(dist2(rep));
	}

}
