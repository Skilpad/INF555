
//
//   Extending PPM for 2D plot
//


public class PPM3 extends PPM {
	
    public PPM3(int Width, int Height) { 
    	super(Width, Height);
    }

	
	public void plot(Pt p) {
		int x = Math.round((float) p.x*width);
		int y = Math.round((float) p.y*height);
		int rr = 255; 
		int gg = 255;
		int bb = 255;
		for (int dx = -2; dx < 3; dx++)
			for (int dy = -2; dy < 3; dy++)
				try { r[y+dy][x+dx] = rr; g[y+dy][x+dx] = gg; b[y+dy][x+dx] = bb; } catch(Exception e) {}
	}
	
	public void plot(Vect l) {
		int rr = 255; 
		int gg = 255;
		int bb = 255;
		for (int x = 0; x<width; x++) {
			int y = Math.round((float) (- height*(l.a*x/width + l.c)/l.b));
			try { r[y  ][x  ] = rr; g[y  ][x  ] = gg; b[y  ][x  ] = bb; } catch(Exception e) {}
		}
		for (int y = 0; y<height; y++) {
			int x = Math.round((float) (- width*(l.b*y/height + l.c)/l.a));
			try { r[y  ][x  ] = rr; g[y  ][x  ] = gg; b[y  ][x  ] = bb; } catch(Exception e) {}
		}
	}

}
