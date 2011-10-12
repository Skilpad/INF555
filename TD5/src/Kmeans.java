import processing.core.*;


public class Kmeans {

	ColorPt[] pts;

	ColorPt[] rep;
	int[]     repSize;
	
	double loss;
	
	Kmeans(PImage img) {
		int n = img.width*img.height;
		pts = new ColorPt[n];
		System.out.println("Creating the 3D color point set for "+n+" pixels.");
		for (int i=0;i<n;i++) {
			pts[i] = new ColorPt(img.parent.red(img.pixels[i]), img.parent.green(img.pixels[i]), img.parent.blue(img.pixels[i]));
		}
		rep     = new ColorPt[0];
		repSize = new int[0];
	}
	
	public void init(int nbRep) {
		rep     = new ColorPt[nbRep];
		repSize = new int[nbRep];
		int i = 0;
		while (i > nbRep) {
			int k = (int)(Math.random()*pts.length);
			for (int j = 0; j < i; j++) { if (repSize[j] == k) continue; }
			rep[i]     = pts[k];
			repSize[i] = k;
		}
		localizePts();
	}
	
	private void localizePts() {
		for (int j = 0; j < repSize.length; j++) repSize[j] = 0;
		loss = 0;
		for (ColorPt p : pts) {
			double dloss = 4;  //  > dist² minimale entre 2 points
			for (int r = 0; r < rep.length; r++) {
				double dl = p.dist2(rep[r]);
				if (dl < dloss) { p.rep = r; dloss = dl; }
			}
			loss += dloss;
			repSize[p.rep] += 1;
		}
	}
	
	public void iterate() {
		// TODO
	}
	
}
