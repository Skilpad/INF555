
//
//   Personnal version from TD1
//



import java.util.StringTokenizer;
import java.io.*;


// ** Moved **

class Color
{
	int R,G,B;
	
	Color(int r, int g, int b) { R = r; G = g; B = b; }
	
	// ** Personnal **
	Color() {  // Random Color
		R = (int)(Math.random() * 256);
		G = (int)(Math.random() * 256);
		B = (int)(Math.random() * 256);		
	}
	
	public int dist(Color c) {
		return (Math.abs(R - c.R) + Math.abs(G - c.G) + Math.abs(B - c.B)); 
	}
	// ***************
	
	// **Modified**
	public boolean equals(Color A)
	{
		return ((R == A.R) && (G == A.G) && (B == A.B));
	}
	// ************

}

// ***********



public class PPM {
    public int [][] r; // Red channel
    public int [][] g; // Green channel
    public int [][] b; // Blue channel
    public int depth,width,height;

    public PPM() {
    	depth=255;  
    	width = height = 0;
    }

    public PPM(int Width, int Height) { 
        depth = 255; 
        width = Width;
        height = Height;
        r = new int[height][width];
        g = new int[height][width];
        b = new int[height][width];
    }
    
    public PPM(String name) {  // Creating object from file
    	System.out.print("Reading image in ppm format...");    	
    	read(name);
    	System.out.println("done");
    	System.out.println(name+"\n"+width+"x"+height+" pixels");
    }

    public void read(String fileName) { // Read the file, setting the object fields
        String line;
        StringTokenizer st;
        int i;

        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            in.readLine();
            do {
                line = in.readLine();
            } while (line.charAt(0) == '#');

            st = new StringTokenizer(line);
            width = Integer.parseInt(st.nextToken());
            height = Integer.parseInt(st.nextToken());
			r = new int[height][width];
        	g = new int[height][width];
        	b = new int[height][width];
            line = in.readLine();
            st = new StringTokenizer(line);
            depth = Integer.parseInt(st.nextToken());

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
					r[y][x] = in.readUnsignedByte();
					g[y][x] = in.readUnsignedByte();
					b[y][x] = in.readUnsignedByte();
				}
			}
            in.close();
        } catch(IOException e) {}
    }

    public void save(String filename) { // Save the object in the file
        String line;
        StringTokenizer st;
        int i;
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
            out.writeBytes("P6\n");
            out.writeBytes("# Frank NIELSEN\n");
            out.writeBytes(width+" "+height+"\n255\n");

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					out.writeByte((byte)r[y][x]);
					out.writeByte((byte)g[y][x]);
					out.writeByte((byte)b[y][x]);
				}
			}
            out.close();
        } catch(IOException e) {}
    }
    
    
	// **PERSONNAL**
    
    public Color getColor(int x, int y) {
    	return new Color(r[y][x], g[y][x], b[y][x]);
    }
    
    public void setColor(int x, int y, Color c) {
    	r[y][x] = c.R;
    	g[y][x] = c.G;
    	b[y][x] = c.B;
    }
    
    public void show() {  						// Show the ppm in window
    	new ImageViewer(new Image(this));
    }
    
    
    // *************
    
}