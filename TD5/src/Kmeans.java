import processing.core.*;


public class Kmeans {

	ColorPt[] pts;

	ColorPt[] rep;
	int[]     repSize;
	double[]  repDist2;
	
	double loss;
	int it;
	
	Kmeans(PImage img) {
		int n = img.width*img.height;
		pts = new ColorPt[n];
		System.out.println("Creating the 3D color point set for "+n+" pixels.");
		for (int i=0;i<n;i++) {
			pts[i] = new ColorPt(img.parent.red(img.pixels[i]), img.parent.green(img.pixels[i]), img.parent.blue(img.pixels[i]));
		}
		rep      = new ColorPt[0];
		repSize  = new int[0];
		repDist2 = new double[0];
		it = 0;
	}
	
	public void init(int nbRep, boolean kmeans) {
		rep = new ColorPt[nbRep];
		if (kmeans) {
			double[] dist2 = new double[pts.length];
			rep[0] = pts[(int) (Math.random()*pts.length)];
			for (int i = 1; i < nbRep; i++) {
				// calcul des poids
				loss = 0;
				for (int j = 0; j < pts.length; j++) {
					double dloss = 4;
					for (int k = 0; k < i; k++) {
						double dl = pts[j].dist2(rep[k]);
						if (dl < dloss) dloss = dl;
					}
					dist2[j] = dloss;
					loss += dloss;
				}
				// random
				double r = Math.random()*loss;
				int j = 0;
				while (r > dist2[j]) {
					r -= dist2[j];
					j++;
				}
				rep[i] = pts[j];
			}	
		} else {
			for (int i = 0; i < nbRep; i++) {
				rep[i] = pts[(int) (Math.random()*pts.length)];
				for (int j = 0; j < i; j++) {
					if (rep[j].equals(rep[i])) { i--; continue; }
				}
			}
		}
		repSize  = new int[nbRep];
		repDist2 = new double[nbRep];
		localizePts();
	}
	
	private void localizePts() {
		for (int j = 0; j < repSize.length; j++) { repSize[j] = 0; repDist2[j] = 0; }
		loss = 0;
		for (ColorPt p : pts) {
			double dloss = 4;  //  > dist² minimale entre 2 points
			for (int r = 0; r < rep.length; r++) {
				double dl = p.dist2(rep[r]);
				if (dl < dloss) { p.rep = r; dloss = dl; }
			}
			loss += dloss;
			repSize[p.rep]++;
			repDist2[p.rep] = Math.max(repDist2[p.rep], dloss);
		}
	}
	
	public void iterate() {
		for (int r = 0; r < rep.length; r++) { rep[r] = new ColorPt(); }
		for (ColorPt p : pts) { rep[p.rep].add(p); }
		for (ColorPt r : rep) { r.mid(); }
		localizePts();
		it++;
	}
	
}
