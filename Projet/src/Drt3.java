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
		this.M = pos.t.apply(pos.Rinv).times(-1);
		this.v = p.toPt3().apply(pos.Rinv).rescaled();
	}
	
	public Drt3(Pt2 p, Position pos, Matrix A) {
		this.M = pos.t.apply(pos.Rinv).times(-1);
		this.v = p.toPt3().apply(A.inverse()).apply(pos.Rinv).rescaled();
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
