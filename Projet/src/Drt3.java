import Jama.Matrix;


public class Drt3 {
	
	public Pt3 M, v;

	
/**********************
 *    Constructors    *
 **********************/
		
	public Drt3(Pt3 M, Pt3 v) {
		this.M = M; this.v = v;
	}
	
	public Drt3(Pt2 p, Position pos) {
		Matrix Rinv = pos.R.inverse();
		this.M = pos.t.apply(Rinv).times(-1);
		this.v = p.toPt3().apply(Rinv).rescaled();
	}
	
	public Drt3(Pt2 p, Position pos, Matrix A) {
		Matrix Rinv = pos.R.inverse();
		this.M = pos.t.apply(Rinv).times(-1);
		this.v = p.toPt3().apply(A.inverse()).apply(Rinv).rescaled();
	}


	
/**********************
 *      Converter     *
 **********************/

	public String toString() {
		return "[ M: " + M + " ; v: " + v + " ]"; 
	}
	
	
/*********************
 *      Methods      *
 *********************/
	

	
}
