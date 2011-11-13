import Jama.Matrix;


public class Pt3 {
	
	public double x,y,z;

	
/**********************
 *    Constructors    *
 **********************/
		
	public Pt3() {
		this.x = this.y = this.z = 0;
	}
	
	public Pt3(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Pt3(Matrix M) {
		if (M.getColumnDimension() == 1) {
			this.x = M.get(0,0); this.y = M.get(1,0); this.z = M.get(2,0);
		} else {
			this.x = M.get(0,0); this.y = M.get(0,1); this.z = M.get(0,2);
		}
	}
	
	
/**********************
 *      Converter     *
 **********************/

	public Matrix toMatrix() {
		Matrix M = new Matrix(3,1); M.set(0,0, x); M.set(1,0, x); M.set(2,0, z);
		return M;
	}
	
	public Pt2 cutToPt2() {
		return new Pt2(x,y);
	}
	
	public Pt2 toPt2() {
		if (z==0) return null;
		return new Pt2(x/z,y/z);
	}
	
	public Pt2 toPt2(Matrix R, Pt3 t) {
		return this.apply(R).plus(t).toPt2();
	}
	
	public Pt2 toPt2(Matrix R, Pt3 t, Matrix A) {
		return this.apply(R).plus(t).apply(A).toPt2();
	}
	
	
/*********************
 *      Methods      *
 *********************/
	
	public void rescale(double coeff) {  x *= coeff; y *= coeff; z *= coeff; }
	public void rescale()             { rescale(1./norm()); }
	
	public void add(Pt3 p) {
		x += p.x; y += p.y; z += p.z;
	}
	
	
/**********************
 *    Calculations    *
 **********************/
	
	public double norm() { return Math.sqrt(x*x+y*y+z*z); }
	
	public Pt3 plus(Pt3 p) {
		return new Pt3(x+p.x, y+p.y, z+p.z);
	}

	public Pt3 minus(Pt3 p) {
		return new Pt3(x-p.x, y-p.y, z-p.z);
	}

	public Pt3 times(double d) {
		return new Pt3(x*d, y*d, z*d);
	}
	
	public Pt3 rescaled() {
		return times(1./norm());
	}
	
	public Pt3 cross(Pt3 p) {
		return new Pt3( y*p.z - z*p.y ,
						z*p.x - x*p.z ,
						x*p.y - y*p.x );
	}

	public Pt3 apply(Matrix M) {    // Return MX, where X is the vertical matrix representing the Pts3.
		return new Pt3( M.get(0,0)*x + M.get(0,1)*y + M.get(0,2)*z ,
	 					M.get(1,0)*x + M.get(1,1)*y + M.get(1,2)*z ,
						M.get(2,0)*x + M.get(2,1)*y + M.get(2,2)*z );
	}
	

	
}
