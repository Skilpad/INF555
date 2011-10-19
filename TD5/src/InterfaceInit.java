// 2011 INF555 Frank NIELSEN
import javax.media.opengl.*;
import processing.opengl.*;
import processing.core.*;


public class InterfaceInit extends PApplet {

	PImage img;
	PGraphicsOpenGL pgl;
	GL gl;

	// pixel colors 
	int n;
	float [] x;
	float [] y;
	float [] z;


	public void createPointSet()
	{
		x=new float[n]; y=new float[n]; z=new float[n];
		println("Creating the 3D color point set for "+n+" pixels.");
		for(int i=0;i<n;i++) { 
			x[i]=(float) (   red(img.pixels[i])/255.0 );
			y[i]=(float) ( green(img.pixels[i])/255.0 );
			z[i]=(float) (  blue(img.pixels[i])/255.0 );
		}
	}

	public void setup() {
		size(512, 512, OPENGL);  
		img = loadImage("polytechnique.png");
		loadPixels(); 
		n=img.width*img.height;
		createPointSet();
		colorMode(RGB, 1, 1, 1);
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL(); 
	}

	public void draw() {   
		background(1);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-2,2,-2,2,-100,100);  
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-2,2,-2,2,-100,100);  
		renderPointSet(); 
	}

	float rotx = 0;

	public void renderPointSet() { 
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPointSize(1);

		gl.glRotatef(rotx,1,0,0);

		gl.glBegin(GL.GL_POINTS);
		for(int i=0;i<n;i++)
		{
			gl.glColor3f(x[i],y[i],z[i]);
			gl.glVertex3f(x[i],y[i],z[i]);
		}
		gl.glEnd();

		gl.glPointSize(10);
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(1,0,0);
		gl.glVertex3f((float) 0.5, (float) 0.5, (float) 0.5);
		gl.glEnd();

		rotx+=1.0;
	}

}
