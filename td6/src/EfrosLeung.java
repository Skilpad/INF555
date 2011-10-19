import processing.core.PImage;



public class EfrosLeung extends Thread {
	
	PImage       src, dst;
	Representant srcRep[][];
	
	public EfrosLeung(PImage src, PImage dst) {
		int s = Constants.s;
		this.src = src;
		this.dst = dst;
		for (int x=0; x<dst.width; x++ )
			for (int y=0; y<dst.height; y++)
				this.dst.set(x, y, Constants.pApplet.color((float) Math.random(),(float) Math.random(),(float) Math.random()));
		srcRep = new Representant[src.width][src.height];
		for (int x=s; x < src.width-s; x++)
			for (int y=s; y < src.height; y++)
				srcRep[x][y] = new Representant(src, x, y);
	}
	
	public void run() {
		int s = Constants.s;
		for (int y=s; y<dst.height;y++)
			for (int x=s; x<dst.width-s; x++)
				reColor(x,y);
	}
	
	private void reColor(int x, int y) {
		int s = Constants.s;
		Representant rep = new Representant(dst, x, y);
		int rX = 0, rY = 0;

		float d2 = 7*s*(s+1);   // > d2max
		for (int xx=s; xx < src.width-s; xx++) {
			for (int yy=s; yy < src.height; yy++) {
				float dd2 = rep.dist2(srcRep[xx][yy]);
				if (dd2 < d2) { rX = xx; rY = yy; d2 = dd2; }
			}
		}
		dst.set(x, y, src.get(rX,rY));
	}
	
}
