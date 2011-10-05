
public class Vect {

	public double a, b, c;
	
	public Vect(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public Vect(Pt p) {  // The 2D point as a 3D point in plan z = 1
		this.a = p.x;
		this.b = p.y;
		this.c = 1;
	}
	
	public Vect(Pt p1, Pt p2) { // The line passing by p1 and p2
		this.a = p1.y - p2.y ;
		this.b = p2.x - p1.x;
		this.c = p1.x*p2.y - p1.y*p2.x;
	}
	
	public Vect crossProduct(Vect v) {  // Cross Product
		return new Vect(b*v.c - c*v.b, c*v.a - a*v.c, a*v.b - b*v.a);
	}
	
	public Pt intersect(Vect l) { // Intersection point of this and l see as lines
		return new Pt(this.crossProduct(l));
	}
	
}

