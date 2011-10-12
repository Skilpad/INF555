
public class ColorPt {

	float r, g, b;
	int rep;
	
	ColorPt (float r, float g, float b) {
		this.r = r/255;
		this.g = g/255;
		this.b = b/255;
		rep = -1;
	}

	double dist2(ColorPt p) {
		return (r-p.r)*(r-p.r) + (g-p.g)*(g-p.g) + (b-p.b)*(b-p.b);
	}
	
}
