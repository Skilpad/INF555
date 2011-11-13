import Jama.Matrix;


public class Drt3 {
	
	public Pt3 M, v;

	
/**********************
 *    Constructors    *
 **********************/
		
	public Drt3(Pt2 p, Matrix R, Pt3 t) {
		Matrix Rinv = R.inverse();
		this.M = t.apply(Rinv).times(-1);
		this.v = p.toPt3().apply(Rinv).rescaled();
	}

	
/**********************
 *      Converter     *
 **********************/


	
	
/*********************
 *      Methods      *
 *********************/
	

	
}
