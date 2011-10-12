// 2011 INF555 Frank NIELSEN
import javax.media.opengl.*;
import processing.opengl.*;
import processing.core.*;


public class Interface extends PApplet {

	PImage img;
	PGraphicsOpenGL pgl;
	GL gl;

	// pixel colors 
	Kmeans pts;
	
	int nbRep = 5;

	float xmag, ymag = 0;
	float newXmag, newYmag = 0; 

	public void setup() {
		size(512, 512, OPENGL);  
		img = loadImage("polytechnique.png");
		loadPixels(); 
		pts = new Kmeans(img);
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

	public void renderPointSet() { 
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPointSize(1);

		
		newXmag = ((float) mouseX)/width * 360;
		newYmag = ((float) mouseY)/height * 360;
		 
		float diff = xmag-newXmag;
		if (abs(diff) >  0.01) { xmag -= diff/4.0; }
		
		diff = ymag-newYmag;
		if (abs(diff) >  0.01) { ymag -= diff/4.0; }
		  
		gl.glRotatef(+ymag,1,0,0);
		gl.glRotatef(-xmag,0,1,0);
		gl.glTranslatef((float) -0.5, (float) -0.5, (float) -0.5); 

		plot(pts);
		
		plotEdges();
	}
	
	private void plot(Kmeans pts) {       // TODO : Ploté les représentants si nbRep > 0 (Tailles proportionnelles à rep)
		gl.glBegin(GL.GL_POINTS);
		for (ColorPt p : pts.pts) {
			gl.glColor3f(p.r,p.g,p.b);
			gl.glVertex3f(p.r,p.g,p.b);
		}
		gl.glEnd();		
	}
	
	private void plotEdges() {
		gl.glPointSize(5);
		gl.glBegin(GL.GL_LINES);
		
			 gl.glColor3f(0,0,0);
			gl.glVertex3f(0,0,0);
			 gl.glColor3f(0,0,1);
			gl.glVertex3f(0,0,1);
			gl.glVertex3f(0,0,1);
			 gl.glColor3f(0,1,1);
			gl.glVertex3f(0,1,1);
			gl.glVertex3f(0,1,1);
			 gl.glColor3f(0,1,0);
			gl.glVertex3f(0,1,0);
			gl.glVertex3f(0,1,0);
			 gl.glColor3f(0,0,0);
			gl.glVertex3f(0,0,0);

			 gl.glColor3f(1,0,0);
			gl.glVertex3f(1,0,0);
			 gl.glColor3f(1,0,1);
			gl.glVertex3f(1,0,1);
			gl.glVertex3f(1,0,1);
			 gl.glColor3f(1,1,1);
			gl.glVertex3f(1,1,1);
			gl.glVertex3f(1,1,1);
			 gl.glColor3f(1,1,0);
			gl.glVertex3f(1,1,0);
			gl.glVertex3f(1,1,0);
			 gl.glColor3f(1,0,0);
			gl.glVertex3f(1,0,0);

			 gl.glColor3f(0,0,0);
			gl.glVertex3f(0,0,0);
			 gl.glColor3f(1,0,0);
			gl.glVertex3f(1,0,0);
			 gl.glColor3f(0,0,1);
			gl.glVertex3f(0,0,1);
			 gl.glColor3f(1,0,1);
			gl.glVertex3f(1,0,1);
			 gl.glColor3f(0,1,0);
			gl.glVertex3f(0,1,0);
			 gl.glColor3f(1,1,0);
			gl.glVertex3f(1,1,0);
			 gl.glColor3f(0,1,1);
			gl.glVertex3f(0,1,1);
			 gl.glColor3f(1,1,1);
			gl.glVertex3f(1,1,1);

		gl.glEnd();
	}

}
