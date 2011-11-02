
public class MixFFT {

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

		PPM lena  = new PPM("lena.ppm");
		PPM fleur = new PPM("fleur.ppm");
		
		lena.show(); fleur.show();
		
		int h = Math.min(lena.height, fleur.height);
		int w = Math.min(lena.width, fleur.width);
		
		double[][] ReL = new double[lena.height][lena.width];
		double[][] ImL;
		double[][] ReF = new double[fleur.height][fleur.width];
		double[][] ImF;
		
		double[][] ReRa = new double[h][w];
		double[][] ImRa = new double[h][w];
		double[][] ReRb = new double[h][w];
		double[][] ImRb = new double[h][w];
		PPM Ra = new PPM(w,h);
		PPM Rb = new PPM(w,h);
		
		System.out.println(lena.height + " - " + fleur.height + " - " + h);
		System.out.println(lena.width + " - " + fleur.width + " - " + w);
		
		ppmTabToNormTab(lena.r,ReL); ppmTabToNormTab(fleur.r,ReF);
		ImL = new double[lena.height][lena.width]; ImF = new double[fleur.height][fleur.width];
		FFT.forwardFFT(ReL,ImL); FFT.forwardFFT(ReF,ImF); 
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) {
			double aL = Math.sqrt(ReL[i][j]*ReL[i][j] + ImL[i][j]*ImL[i][j]);
			double aF = Math.sqrt(ReF[i][j]*ReF[i][j] + ImF[i][j]*ImF[i][j]);
			double pL = Math.atan2(ImL[i][j],ReL[i][j]);
			double pF = Math.atan2(ImF[i][j],ReF[i][j]);
			ReRa[i][j] = aL*Math.cos(pF);
			ReRb[i][j] = aF*Math.cos(pL);
			ImRa[i][j] = aL*Math.sin(pF);
			ImRb[i][j] = aF*Math.sin(pL);
		}
		FFT.inverseFFT(ReRa,ImRa); FFT.inverseFFT(ReRb,ImRb);
		normTabToPpmTab(ReRa,Ra.r); normTabToPpmTab(ReRb,Rb.r);

		ppmTabToNormTab(lena.g,ReL); ppmTabToNormTab(fleur.g,ReF);
		ImL = new double[lena.height][lena.width]; ImF = new double[fleur.height][fleur.width];
		FFT.forwardFFT(ReL,ImL); FFT.forwardFFT(ReF,ImF); 
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) {
			double aL = Math.sqrt(ReL[i][j]*ReL[i][j] + ImL[i][j]*ImL[i][j]);
			double aF = Math.sqrt(ReF[i][j]*ReF[i][j] + ImF[i][j]*ImF[i][j]);
			double pL = Math.atan2(ImL[i][j],ReL[i][j]);
			double pF = Math.atan2(ImF[i][j],ReF[i][j]);
			ReRa[i][j] = aL*Math.cos(pF);
			ReRb[i][j] = aF*Math.cos(pL);
			ImRa[i][j] = aL*Math.sin(pF);
			ImRb[i][j] = aF*Math.sin(pL);
		}
		FFT.inverseFFT(ReRa,ImRa); FFT.inverseFFT(ReRb,ImRb);
		normTabToPpmTab(ReRa,Ra.g); normTabToPpmTab(ReRb,Rb.g);

		ppmTabToNormTab(lena.b,ReL); ppmTabToNormTab(fleur.b,ReF);
		ImL = new double[lena.height][lena.width]; ImF = new double[fleur.height][fleur.width];
		FFT.forwardFFT(ReL,ImL); FFT.forwardFFT(ReF,ImF); 
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) {
			double aL = Math.sqrt(ReL[i][j]*ReL[i][j] + ImL[i][j]*ImL[i][j]);
			double aF = Math.sqrt(ReF[i][j]*ReF[i][j] + ImF[i][j]*ImF[i][j]);
			double pL = Math.atan2(ImL[i][j],ReL[i][j]);
			double pF = Math.atan2(ImF[i][j],ReF[i][j]);
			ReRa[i][j] = aL*Math.cos(pF);
			ReRb[i][j] = aF*Math.cos(pL);
			ImRa[i][j] = aL*Math.sin(pF);
			ImRb[i][j] = aF*Math.sin(pL);
		}
		FFT.inverseFFT(ReRa,ImRa); FFT.inverseFFT(ReRb,ImRb);
		normTabToPpmTab(ReRa,Ra.b); normTabToPpmTab(ReRb,Rb.b);

		Ra.show(); Rb.show();

	}

}
