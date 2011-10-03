// (c) 2010 Frank Nielsen
//

public class ShowImageSample {
	
	public static Image ImgFlipY(Image img)
	{int i,j;
	Image imgres=new Image(img.getWidth(),img.getHeight());
	
	for(i=0;i<img.getHeight();i++)
		for(j=0;j<img.getWidth();j++)
		{
			imgres.setPixel(j,i,img.getPixel(j,img.getHeight()-1-i));
		}
		
		return imgres;
	}
	
	
	public static Image ImgProcess(Image img)
	{int i,j;
	Image imgres=new Image(img.getWidth(),img.getHeight());
	
	for(i=0;i<img.getHeight();i++)
		for(j=0;j<img.getWidth();j++)
		{
			
			int r,g,b;
			r=img.getRed(j,i);
			g=img.getGreen(j,i);
			b=img.getBlue(j,i);
			if (i==j) System.out.println(r+" "+g+" "+b);		
			
			
			//imgres.setRed(j,i,255);
			//imgres.setGreen(j,i,255);
			//imgres.setBlue(j,i,255);
	
	
	
	imgres.setRed(j,i,0);
	imgres.setGreen(j,i,0);
	imgres.setBlue(j,i,b);
	
		}
		
		return imgres;
	}
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Image img = new Image("forfloodfilling.png");
                ImageViewer imgv = new ImageViewer(img);
                
                Image imgflip=ImgFlipY(img);
                ImageViewer imgvflip = new ImageViewer(imgflip);
                
                Image imgc=ImgProcess(img);
                ImageViewer imgvp = new ImageViewer(imgc);
                
                img.writeBinaryPPM("convertedToPPM.ppm");
                imgc.writeBinaryPPM("convertedToBlue.ppm");
                imgflip.writeBinaryPPM("imgflip.ppm");
                
                System.out.println("INF555: end of run code");
            }
        });
    }
}