
public class Pt {
	
	public float x, y;
	
	public Pt(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Pt() {
		this.x = (float) Math.random();
		this.y = (float) Math.random();
	}
	
	public Pt(Vect v) {
		this.x = v.a/v.c;
		this.y = v.b/v.c;
	}
	
}
