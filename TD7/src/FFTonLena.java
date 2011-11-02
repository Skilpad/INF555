
public class FFTonLena {

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
	
	public static void main(String[] args)
	{

		PPM lena = new PPM("lena.ppm");
		
		double[][] Re = new double[lena.height][lena.width];
		double[][] Im;
		
		lena.show();
		
		ppmTabToNormTab(lena.r,Re);
		Im = new double[lena.height][lena.width];
		FFT.forwardFFT(Re,Im); FFT.inverseFFT(Re,Im);
		normTabToPpmTab(Re,lena.r);
		
		ppmTabToNormTab(lena.g,Re);
		Im = new double[lena.height][lena.width];
		FFT.forwardFFT(Re,Im); FFT.inverseFFT(Re,Im);
		normTabToPpmTab(Re,lena.g);
		
		ppmTabToNormTab(lena.b,Re);
		Im = new double[lena.height][lena.width];
		FFT.forwardFFT(Re,Im); FFT.inverseFFT(Re,Im);
		normTabToPpmTab(Re,lena.b);

		lena.show();

	}

	
}
