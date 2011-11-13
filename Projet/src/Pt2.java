import Jama.Matrix;


public class Pt2 {
	
	public double x,y;

	
/**********************
 *    Constructors    *
 **********************/
		
	public Pt2(double x, double y) {
		this.x = x; this.y = y;
	}
	
	public Pt2(Pt3 p) {
		this.x = p.x/p.z; this.y = p.y/p.z;
	}
	
	
/**********************
 *      Converter     *
 **********************/

	public Matrix toMatrix() {
		Matrix M = new Matrix(2,1); M.set(0,0, x); M.set(1,0, x);
		return M;
	}
	
	public Pt3 toPt3() {
		return new Pt3(x,y,1);
	}
	
	public String toString() {
		return ("("+x+","+y+")");
	}
	
	
/*********************
 *      Methods      *
 *********************/
	
	public double norm() { return Math.sqrt(x*x+y*y); }
	
}
