import java.util.LinkedList;

import processing.core.PImage;


public class MetricBall {

	public float r;                      // Rayon.
	public Representant center;           // Centre de la metric ball.
	public LinkedList<Representant> pts;  // Points contenus ssi la MetricBall est une feuille. (Vide si elle a des fils.)
	public MetricBall a, b;               // Fils.
	
	public  MetricBall(PImage img) {
		this.pts = new LinkedList<Representant>();
		this.r = Constants.inf();
		int s = Constants.s;
		for (int x=s; x < img.width-s; x++)
			for (int y=s; y < img.height; y++)
				this.pts.add(new Representant(img,x,y));
	}
	
	private MetricBall(Representant center) {
		this.pts = new LinkedList<Representant>();
		this.pts.add(center);
		this.center = center;
		this.r = 0;
	}
	
	private void add(Representant rep) {
		pts.add(rep);
		float d = rep.dist(center);
		if (d > r) r = d;
	}

	private void add(Representant rep, float dist2) {
		pts.add(rep);
		r = (float) Math.sqrt(dist2);
	}

	private void divide() {
		int n = pts.size();
		if (n < Constants.n0) return;
		int ia = (int) (Math.random()*n);
		int ib = (int) (Math.random()*n);
		while (ib == ia) ib = (int) (Math.random()*n);
		Representant ra = pts.remove(ia);
		Representant rb = pts.remove(ib);
		a = new MetricBall(ra);
		b = new MetricBall(rb);
		while (!pts.isEmpty()) {
			Representant rep = pts.remove();
			float d2a = rep.dist2(ra);
			float d2b = rep.dist2(rb);
			if (d2a < d2b)   a.add(rep, d2a);   else   b.add(rep, d2b);
		}
		a.divide();
		b.divide();
	}
	
	public  float dist(Representant rep) {
		return rep.dist(center) - r;
	}
		
	
	
	private class DistNColor {
		public float dist; public int color;
		DistNColor() { dist = Constants.inf(); color = 0; }
	}
	
	private void findColor(Representant rep, DistNColor dnc) {
		if (a == null) {
			for (Representant x : pts) {
				float d = rep.dist(x);
				if (d < dnc.dist) { dnc.dist = d; dnc.color = x.color;}
			}
		} else {
			if (a.dist(rep) < dnc.dist) a.findColor(rep, dnc);
			if (a.dist(rep) < dnc.dist) b.findColor(rep, dnc);
		}
	}

	public int color(Representant rep) {
		DistNColor dnc = new DistNColor();
		findColor(rep, dnc);
		return dnc.color;
	}
	
		
}
