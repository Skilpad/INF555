
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
	
	public Pt(Vect v) {
		this.x = v.a/v.c;
		this.y = v.b/v.c;
	}
	
}
