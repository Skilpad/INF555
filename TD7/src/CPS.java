import java.awt.Point;


public class CPS {
	
	private static void ppmTabToNormTab(int[][] src, double[][] dst) {
		for (int i=0; i < src.length; i++)
			for (int j=0; j < src[i].length; j++)
				dst[i][j] = ((double) src[i][j])/256;
	}

	private static void normTabToPpmTab(double[][] src, int[][] dst) {
		for (int i=0; i < src.length; i++)
			for (int j=0; j < src[i].length; j++)
				dst[i][j] = (int)(src[i][j]*256);
	}

	static int h, w;
	static PPM src1, src2;
	static double[][] CPS_Re, CPS_Im, CPS;
	
	public static void init(PPM srcA, PPM srcB) {
		src1 = srcA;
		src2 = srcB;
		h = src1.height; w = src1.width;
	}
	
	public static void calcCPS() {
		
		double[][] Re1 = new double[h][w];
		double[][] Re2 = new double[h][w];
		double[][] Im1 = new double[h][w];
		double[][] Im2 = new double[h][w];
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) 
			{ Re1[i][j] = (0.3*src1.r[i][j]+0.59*src1.g[i][j]+0.11*src1.b[i][j])/256;
			  Re2[i][j] = (0.3*src2.r[i][j]+0.59*src2.g[i][j]+0.11*src2.b[i][j])/256; }
		FFT.forwardFFT(Re1,Im1);
		FFT.forwardFFT(Re2,Im2);
		
		CPS_Re = new double[h][w];
		CPS_Im = new double[h][w];
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) {
			CPS_Re[i][j] = Re1[i][j]*Re2[i][j]+Im1[i][j]*Im2[i][j];
			CPS_Im[i][j] = Im1[i][j]*Re2[i][j]-Re1[i][j]*Im2[i][j];
			double n = Math.sqrt(CPS_Re[i][j]*CPS_Re[i][j]+CPS_Im[i][j]*CPS_Im[i][j]);
			CPS_Re[i][j] /= n;
			CPS_Im[i][j] /= n;
		}
		
		FFT.inverseFFT(CPS_Re,CPS_Im);
	
	}
	
	public static Point optimalTrans() {		
		Point r = new Point();
		double mx = -1;
		for (int i=0; i<h; i++) for (int j=0; j<w; j++)
			if (CPS_Re[i][j] > mx) { mx = CPS_Re[i][j]; r = new Point(i,j); }	
		return r;
	}

}
