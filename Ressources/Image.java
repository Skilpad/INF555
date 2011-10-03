// INF555 Image class
//
// (c) 2009 Frank Nielsen
//

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class Image {

    private int[] raster; // Array of 32 bits (ARGB). Bit shift operations
    private int width,  height;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        raster = new int[width*height];
        
        for(int i=0;i<width*height;i++)
        	raster[i]=0xFF << 24; // alpha channel
    }
    

    public Image(String filename) {
    	System.out.print("Opening image from file "+filename+" ...");
        java.awt.Image img = Toolkit.getDefaultToolkit().getImage(filename);
        System.out.println("done");

        if (img != null) {
            PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
            }
            raster = (int[]) pg.getPixels();
            height = pg.getHeight();
            width = pg.getWidth();

        } else {
            throw new IllegalArgumentException("Error opening file " + filename);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        return raster[y * width + x];
    }
    
    public void setPixel(int x, int y, int p) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        raster[y * width + x] = p;
    }
    
    public boolean isBlack(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        return (raster[y * width + x] & 0xFFFFFF) == 0;
    }
    
     public int getBlue(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        return (raster[y * width + x] & 0xFF);
    }

    public int getGreen(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        return ((raster[y * width + x] & 0xFF00) >> 8);
    }

    public int getRed(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        return ((raster[y * width + x] & 0xFF0000) >> 16);
        
  }
    
    //
    // Set colors
    //
    
    
//    int red = (pixel >> 16)& 0xff;
//int green = (pixel >> 8) & 0xff;
//int blue = pixel & 0xff;
    
    public void setGreen(int x, int y,  int value) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        raster[y * width + x] = raster[y * width + x] & 0xFFFF00FF; // reset green
       raster[y * width + x]  = raster[y * width + x] | ((value&0xff)<<8);
    }
    
    
    public void setRed(int x, int y,  int value) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        raster[y * width + x] = raster[y * width + x] & 0xFF00FFFF; // reset red
        raster[y * width + x]   = raster[y * width + x] | ((value&0xff)<<16);
    }
    
    public void setBlue(int x, int y,  int value ) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        raster[y * width + x] = raster[y * width + x] & 0xFFFFFF00; // reset blue
        raster[y * width + x]   = raster[y * width + x] | (value&0xff);
    }
    
    public void toGray(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
        int r=this.getRed(x,y);
        int g=this.getGreen(x,y);
        int b=this.getBlue(x,y);
        
        int gray=(int)(0.3*r+0.59*g+0.11*b);
        this.setPixel(x,y,gray);
    }
    
    public void toGray() {
    	System.out.print("Computing gray conversion...");
    	for(int i=0;i<width;i++)
    		for(int j=0;j<height;j++)
    			this.toGray(i,j);
    	System.out.println("done");
    }

    
    public static void printColor(int value)
    {
    int i, b;
    int alpha, red, blue, green;
    
    
    alpha=(value >> 24) & 0xFF; 
    red= ( value >>16) & 0xFF ;
    green= ( value >>8)  & 0xFF;
    blue=   value & 0xFF ;
    
    
    
    //System.out.print("Alpha=");
    for(i=0;i<8;i++)
    	{
    		b=Math.abs((value>> (31-i))%2);
    		//System.out.print(b);
    	} 
    //System.out.println("("+alpha+")");	
    	
    //System.out.print("Red=");	
    for(i=8;i<16;i++)
    	{
    		b=Math.abs((value>> (31-i))%2);
    		//System.out.print(b);
    	} 
    //System.out.println("("+red+")");		
    	
    //System.out.print("Green=");	
    for(i=16;i<24;i++)
    	{
    		b=Math.abs((value>> (31-i))%2);
    		//System.out.print(b);
    	} 
    System.out.println("("+green+")");		
    	
    //System.out.print("Blue=");	
    for(i=24;i<32;i++)
    	{
    		b=Math.abs((value>> (31-i))%2);
    		//System.out.print(b);
    	} 
    	//System.out.println("("+blue+")");				
    }
    

//
// Image class of Java
//
    public java.awt.Image toImage() {
        ImageProducer ip = new MemoryImageSource(width, height, raster, 0, width);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    
    
   

    public void writeBinaryPPM(String filename)
    {
        String line;
        StringTokenizer st;
        int i;
        try {
            DataOutputStream out =new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
            out.writeBytes("P6\n");
            out.writeBytes("# INF555 Ecole Polytechnique\n");
            out.writeBytes(width+" "+height+"\n255\n");

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int r,g,b;
					r=getRed(x,y);
					g=getGreen(x,y);
					b=getBlue(x,y);
					
					out.writeByte((byte)r);
					out.writeByte((byte)g);
					out.writeByte((byte)b);
				}
			}
            out.close();
        } catch(IOException e) {}
    }
    
    public static void main(String[] args) {
    	Image example=new Image("polytechnique.png");
    	//example.toGray();
    	new ImageViewer(example);
    }

    
}
