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
	double epsilon = 0.0000000001;
	
	float xmag, ymag = 0;
	float newXmag, newYmag = 0;
	
	boolean distMode = true;

	public void setup() {
		size(512, 512, OPENGL);  
		img = loadImage("polytechnique.png");
//		img = loadImage("tux.png");
//		img = loadImage("firefox.png");
//		img = loadImage("pencils.png");
//		img = loadImage("blogcat.png");
		loadPixels(); 
		colorMode(RGB, 1, 1, 1);
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL();
	}
	
	public void draw() {
		if (pts == null) {
			background(0);
			fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(20);
			text("Manual: \n\n" +
				 "  - Use mouse to rotate view. \n" +
				 "  - Press SPACE to run a single iteration \n" +
				 "     of the algorithm. \n" +
				 "  - Press ENTER to run the algorithm \n" +
				 "     until stabilisation. \n" +
				 "  - Press R to run again from start \n" +
				 "     (with new seeds). \n" +
				 "  - Press M to change radius meaning. \n" +
				 "  - Press +/- to change the number of seeds \n" +
				 "     (applied when the algorithme initializes). \n\n" +
				 "Press any key to start.", 20, 50);
		} else {
			background(0);
			fill(color(0xFFFFFFFF)); stroke(color(0xFFFFFFFF)); textSize(20);
			text("Iteration: " + ((pts.rep.length == 0) ? "-" : pts.it), 20, 50);
			text("Loss: " +      ((pts.rep.length == 0) ? "-" : pts.loss), 20, 80);
			text("N: " + nbRep, 450, 50);
			text("Radius <=> ", 20, 450);
			text(((distMode) ? "Maximal distance to \ncorresponding points" : "Number of \ncorresponding points"), 150, 450);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-2,2,-2,2,-100,100);  
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-2,2,-2,2,-100,100);  
			renderPointSet(); 
		}
	}

	public void renderPointSet() { 
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
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
	
	private void plot(Kmeans pts) {
		if (pts.rep.length == 0) {
			gl.glPointSize(1);
			gl.glBegin(GL.GL_POINTS);
			for (ColorPt p : pts.pts) {
				gl.glColor3f(p.r,p.g,p.b);
				gl.glVertex3f(p.r,p.g,p.b);
			}
			gl.glEnd();
		} else {
			for (int i = 0; i < pts.rep.length; i++) {
				gl.glPointSize((distMode) ? 100 * (float) Math.sqrt(pts.repDist2[i]) : pts.repSize[i]*100/pts.pts.length);
				gl.glBegin(GL.GL_POINTS);
				ColorPt p = pts.rep[i];
				gl.glColor3f(p.r,p.g,p.b);
				gl.glVertex3f(p.r,p.g,p.b);
				gl.glEnd();			
			}
		}
	}
	
	private void plotEdges() {
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

	private void initPts() {
		colorMode(RGB, 255, 255, 255); pts = new Kmeans(img); colorMode(RGB, 1, 1, 1);
	}
	
	public void keyPressed() {
		if (pts == null) { initPts(); return; }
		switch (key) {
			case ' ' :  if (pts.rep.length == 0) pts.init(nbRep); else pts.iterate(); 
						break;
			case '\n':  if (pts.rep.length == 0) pts.init(nbRep);
						double l0 = 0;
						while (l0 - pts.loss > epsilon || l0 == 0) {
							l0 = pts.loss;
							pts.iterate();
						}
						break;
			case 'r' :  initPts(); break;
			case 'm' :  distMode = !distMode; break;
			case '+' :  nbRep++; break;
			case '-' :  nbRep--; break;
		}
	}
	
}
