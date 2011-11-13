import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class Mat extends Matrix {

/**********************
 *    Constructors    *
 **********************/
	
	public Mat(int n) {
		super(n, 1);
	}

	public Mat(double[] arg0, int arg1) {
		super(arg0, arg1);
	}
	
	public Mat(double[][] arg0) {
		super(arg0);
	}

	public Mat(double[][] arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public Mat(int width, int length) {
		super(width, length);
	}

	public Mat(int arg0, int arg1, double arg2) {
		super(arg0, arg1, arg2);
	}
	
	public Mat(Matrix m) {
		super(m.getArray());
	}
	

	
	
/*********************
 *      Methods      *
 *********************/
	
	public Mat rodrigues() {
		Mat res = new Mat(1,1);
		if (getColumnDimension() == 1 || getRowDimension() == 1) {
			if (getRowDimension() == 1) this.rodrigues();
		}
		return res;
	}
	
	
	
	
	
	public static void main(String [] a) {
		double[][] dr = new double[3][3];
		dr[0][0] = 1;  dr[0][1] = 6;  dr[0][2] = 8;
		dr[1][0] = 5;  dr[1][1] = 6;  dr[1][2] = 5;
		dr[2][0] = 4;  dr[2][1] = 5;  dr[2][2] = 7;
		
		Mat r = new Mat(dr);
		r.print(5,5);
		SingularValueDecomposition svd = r.svd();
		System.out.println("U,S,V:");
		svd.getU().print(5,5);
		svd.getS().print(5,5);
		svd.getV().print(5,5);
		System.out.println("USV:");
		svd.getU().times(svd.getS()).times(svd.getV()).print(5,5);
		System.out.println("USVt:");
		svd.getU().times(svd.getS()).times(svd.getV().transpose()).print(5,5);
		System.out.println("VVt:");
		svd.getV().times(svd.getV().transpose()).print(5,5);		
	}
	
}
