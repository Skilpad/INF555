import Jama.Matrix;


public class Pt {
	
	public double x, y;
	
	public Pt(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Pt() {
		this.x = Math.random();
		this.y = Math.random();
	}
	
	public double dist2(Pt p) {
		return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y);
	}
	
	public double dist(Pt p) {
		return Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y));
	}
	
	public Pt apply(Matrix M) {
		return new Pt( M.get(0,0)*x + M.get(0,1)*y , M.get(1,0)*x + M.get(1,1)*y );
	}
	
	public Pt plus(Pt p) {
		return new Pt(x+p.x, y+p.y);
	}

	public Pt minus(Pt p) {
		return new Pt(x-p.x, y-p.y);
	}

	public Pt times(double t) {
		return new Pt(t*x,t*y);
	}
	
	public double scal(Pt p) {
		return x*p.x + y*p.y;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public boolean equals(Pt p) {
		return x == p.x && y == p.y;
	}
	
}
