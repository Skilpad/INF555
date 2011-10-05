// (c) Frank NIELSEN, INF555-TD3-2009
import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;

public class BilateralFiltering extends Applet {
	static java.awt.Image img, imgo;
	static int raster []; int rastero [];
	static PixelGrabber pg;
	static int width, height;
	static double sigmas=15.0;  // 30
	static double sigmai=0.75;  // 1.25
	static int k=1;

	static int [] BilateralFiltering(int [] raster, double sigmas, double sigmai, int k) {
		int [] result = null, rasteri;
		int grey = 0, greyc, ngrey, alpha, index, indexc;	
		int i, j, ii, jj, l;
		int bound = (int)(3.0*sigmas);
		double m,p, wg, wr,w;
		
		double sigmas2 = sigmas*sigmas;
		double sigmai2 = sigmai*sigmai;


		for (l=0; l<k; l++) {
			System.out.println("Pass #"+l);
			result=new int[width*height];

			for (i=0; i<height; i++)
				for (j=0; j<width; j++) {
					m = p = 0.0;
					index = j+i*width;
					alpha = -1;

					for (ii = -bound; ii < bound; ii++)
						for (jj = -bound; jj < bound; jj++) {
							if((j+jj>=0) && (i+ii>=0) && (j+jj<width) && (i+ii<height)) {
								indexc=j+jj+(i+ii)*width; 
								grey  = (raster[index]  & 0xFF); 
								greyc = (raster[indexc] & 0xFF);  

//								double f = Math.exp(-0.5*(ii*ii+jj*jj)/(sigmas*sigmas))*Math.exp(-0.5*(grey-grey)*(greyc-greyc)/(sigmai*sigmai));
								double f = Math.exp(-0.5*((ii*ii+jj*jj)/(sigmas2)+(grey-grey)*(greyc-greyc)/(sigmai2)));
								m += f*greyc;  // Moyenne
								p += f;        // Normalisation
							}
						}
					ngrey=(int)(m/p);
					result [ index ] = ( (alpha << 24) | (ngrey << 16) | (ngrey << 8) | ngrey);	
				}
			raster=result;
		}

		return result;
	}

	public void init() {
		String nameimage = "polytechnique.png";

		java.awt.Image image = getImage(getDocumentBase() , nameimage);
		pg = new PixelGrabber(image , 0 , 0 , -1 , -1 , true);
		try { pg.grabPixels();} 	catch (InterruptedException e) { }
		height=pg.getHeight(); width=pg.getWidth();
		System.out.println("Image filename:"+nameimage+" "+width+" "+height);
		raster = (int[])pg.getPixels();

		rastero=BilateralFiltering(raster, sigmas, sigmai, k);

		ImageProducer ip = new MemoryImageSource(width , height ,rastero , 0 , width);
		imgo = createImage(ip);
	}

	public void paint(Graphics g) {
		g.drawImage(imgo , 0 , 0 , this);
	}
	
//	public static void main(String[] args) { init(); }
}
