// Personnal Image class
// Only for TD2

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import Jama.Matrix;

public class GrayImage {

	private int[] raster; // Array of 32 bits (ARGB). Bit shift operations
	private int width,  height;

	public GrayImage(String filename) {
		System.out.print("Opening image from file "+filename+" ...");
		java.awt.Image img = Toolkit.getDefaultToolkit().getImage(filename);
		System.out.println("done");

		if (img != null) {
			PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
			try { pg.grabPixels(); } catch (InterruptedException e) {}
			raster = (int[]) pg.getPixels();
			height = pg.getHeight();
			width = pg.getWidth();
			
			for (int i=0; i<raster.length; i++) {
				int intensity = (int)( 0.3  * ((raster[i]&0xff0000) >> 16) +
									   0.59 * ((raster[i]&0x00ff00) >> 8 ) +
									   0.11 * ((raster[i]&0x0000ff)      ) );
				raster[i] = (0xFF << 24) | ((intensity&0xff) << 16)
										 | ((intensity&0xff) << 8)
										 | ((intensity&0xff));
			}
		} else {
			throw new IllegalArgumentException("Error opening file " + filename);
		}
	}
	
	public GrayImage(int width, int height) {
		width = this.width;
		height = this.height;
		raster = new int[width*height];
	}
	
	public GrayImage(Matrix m) {
		height= m.getColumnDimension();
		width  = m.getRowDimension();
		raster = new int[width*height];
		double max = 0;
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++)
				if (Math.abs(m.get(x,y)) > max) max = Math.abs(m.get(x,y));
		double r = 255/max;
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {
				int intensity = (int)(r*Math.abs(m.get(x,y)));
				raster[x+y*width] = (0xFF << 24) | ((intensity&0xff) << 16)
												 | ((intensity&0xff) << 8)
												 | ((intensity&0xff));
			}
	}
	
	/* Accesses */
	
	public int getWidth()  { return width; }
	public int getHeight() { return height; }

	public int getPixel(int x, int y) {
		return (raster[x+y*width]&0xff);
	}
	
	public void setPixel(int x, int y, int intensity) {
		raster[x+y*width] = (0xFF << 24) | ((intensity&0xff) << 16)
										 | ((intensity&0xff) << 8)
										 | ((intensity&0xff));
	}

	public void show() {
		new ImageViewer(this);
	}
	
	
	/* Gradients */
	
	public Matrix verticalGradient() {
		Matrix g = new Matrix(width, height);
		for (int x=0; x<width; x++)
			for (int y=1; y<height-1; y++)
				g.set(x,y,getPixel(x,y+1)-getPixel(x,y-1));
		return g;
	}

	public Matrix horizontalGradient() {
		Matrix g = new Matrix(width, height);
		for (int x=1; x<width-1; x++)
			for (int y=0; y<height; y++)
				g.set(x,y,getPixel(x+1,y)-getPixel(x-1,y));
		return g;
	}

	public Matrix gradient() {
		Matrix g = new Matrix(width, height);
		for (int x=1; x<width-1; x++)
			for (int y=1; y<height-1; y++)
			{
				double gx = getPixel(x+1,y)-getPixel(x-1,y);
				double gy = getPixel(x,y+1)-getPixel(x,y-1);
				g.set(x,y,Math.sqrt(gx*gx+gy*gy));
			}
		return g;
	}
	
	
	/* Convolution */
	
	public Matrix convolution(Matrix mask) {
		// NB: To have matrix and images in the same sense, x and y of the matrix must be inverted. 
		// It is only important for the mask, to use a non-transposed mask.
		int w = mask.getColumnDimension();
		int h = mask.getRowDimension();
		Matrix c = new Matrix(width-w, height-h);
		for (int x=0; x<width-w; x++)
			for (int y=0; y<height-h; y++) {
				double pixel = 0;
				for (int cx=0; cx<w; cx++)
					for (int cy=0; cy<h; cy++)
						pixel += mask.get(cy,cx)*getPixel(x+cx, y+cy);
				c.set(x,y,pixel);
			}
		return c;
	}
	
	
	/* Harris-Stephens */
	
	public Stack<Point> harris_stephens(Matrix feature, double k, double threshold) {
		int w = feature.getColumnDimension();
		int h = feature.getRowDimension();
		Matrix gx = horizontalGradient();
		Matrix gy = verticalGradient();
		Matrix rMat = new Matrix(width-w, height-h);   // Matrix of R values
		// Build R Matrix
		for (int x=0; x<width-w; x++)
			for (int y=0; y<height-h; y++) {
				Matrix m = new Matrix(2,2);
				for (int cx=0; cx<w; cx++)
					for (int cy=0; cy<h; cy++) {
						double gxx = gx.get(x+cx,y+cy);
						double gyy = gy.get(x+cx,y+cy);
						double[][] g = {{gxx*gxx,gxx*gyy},{gxx*gyy,gyy*gyy}};
						m.plusEquals( (new Matrix(g) ).times( feature.get(cy,cx) ) );
					}
				double tr = m.trace();
				rMat.set(x, y, m.det() - k*tr*tr);
			}
//		(new GrayImage(rMat)).show();
		// Keep local maxima > threshold
		Stack<Point> res = new Stack<Point>();
		for (int x=1; x<width-w-1; x++)
			for (int y=1; y<height-h-1; y++) {
				double r = rMat.get(x,y);
				if ( r > threshold && r > rMat.get(x-1,y-1)
				                   && r > rMat.get(x-1,y  )
				                   && r > rMat.get(x-1,y+1)
				                   && r > rMat.get(x  ,y-1)
				                   && r > rMat.get(x  ,y+1)
				                   && r > rMat.get(x+1,y-1)
				                   && r > rMat.get(x+1,y  )
				                   && r > rMat.get(x+1,y+1) )
					res.add(new Point(x+w/2,y+h/2));
			}
		return res;
	}

	public void plotHarrisStephensPoints(Matrix feature, double k, double threshold) {
		Stack<Point> pts = harris_stephens(feature, k, threshold);
		while (!pts.isEmpty()) {
			Point pt = pts.pop();
			for (int dx=-1; dx<2; dx++)
				for (int dy=-1; dy<2; dy++)
					raster[pt.x+dx+(pt.y+dy)*width] = (0xFF << 24) | (0xFF << 16)
					                                 | (0&0xff << 8)
					                                 | (0&0xff);			
		}
	}
	
	
	// **** From Frank Nielsen's Image class ****

	public java.awt.Image toImage() {
		ImageProducer ip = new MemoryImageSource(width, height, raster, 0, width);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	// ******************************************

	public static void main(String[] args) {
		GrayImage img = new GrayImage("polytechnique.png");
		img.show();
		
		/* Gradient */
//		new ImageViewer(new GrayImage(img.verticalGradient()));	
//		new ImageViewer(new GrayImage(img.horizontalGradient()));	
		(new GrayImage(img.gradient())).show();	
		
		/* Convolution */
		double[][] dconvol = {{1,0},{0,-1}};
		Matrix convol = new Matrix(dconvol);
		System.out.println("Mask: ");
		convol.print(3,2);
		new ImageViewer(new GrayImage(img.convolution(convol)));	
//		double[][] dconvol2 = {{0,1},{-1,0}};
//		convol = new Matrix(dconvol2);
//		System.out.println("Mask: ");
//		convol.print(3,2);
//		new ImageViewer(new GrayImage(img.convolution(convol)));
		
		/* Harris-Stephens */
		double[][] f = {{1, 4, 7, 4,1},
						{4,16,26,16,4},
						{7,26,41,26,7},
						{4,16,26,16,4},
						{1, 4, 7, 4,1}};
		Matrix feature = (new Matrix(f)).times(1./273.);
		img.plotHarrisStephensPoints(feature, 0.05, 3000);
		// new ImageViewer(img);
	}

}
