import processing.core.PImage;



public class EfrosLeung extends Thread {
	
	PImage       src, dst;
	MetricBall   metricBallTree;
	
	public EfrosLeung(PImage src, PImage dst) {
		this.src = src;
		this.dst = dst;
		for (int x = 0; x < dst.width; x++ )
			for (int y = 0; y < dst.height; y++)
				this.dst.set(x, y, Constants.pApplet.color((float) Math.random(),(float) Math.random(),(float) Math.random()));
		metricBallTree = new MetricBall(src);
	}
	
	public void run() {
		int s = Constants.s;
		for (int y = s; y < dst.height;y++)
			for (int x = s; x < dst.width-s; x++)
				dst.set(x, y, metricBallTree.color(new Representant(dst, x, y)));
	}
	
}
