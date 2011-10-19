
public class ColorPt {

	float r, g, b;
	int rep;
	
	ColorPt (float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.rep = -1;
	}
	
	ColorPt () {
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.rep = 0;
	}

	public double dist2(ColorPt p) {
		return (r-p.r)*(r-p.r) + (g-p.g)*(g-p.g) + (b-p.b)*(b-p.b);
	}
	
	public void add(ColorPt p) {
		r += p.r;
		g += p.g;
		b += p.b;
		rep++;
	}
	
	public void mid() {
		r /= rep;
		g /= rep;
		b /= rep;
	}
}
