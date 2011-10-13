// 2011 INF555 Frank NIELSEN
import javax.media.opengl.*;
import processing.opengl.*;
import processing.core.*;


public class Interface extends PApplet {

	int nbRep       = 5;
	double epsilon  = 0.0000000001;
	String[] images = {"polytechnique.png","tux.png","firefox.png","pencils.png","blogcat.png"};
	

	PImage img;
	int indexImg = -2;
	PGraphicsOpenGL pgl;
	GL gl;
	PImage res;

	Kmeans pts;
	
	float xmag, ymag = 0;
	float newXmag, newYmag = 0;
	
	boolean kmeans = false;
	
	int view = 0;
	
	public void setup() {
		size(512, 512, OPENGL);
		colorMode(RGB, 1, 1, 1);
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL();
		fill(color(0xFFFFFFFF)); textSize(20);
	}
	
	public void draw() {
		if (indexImg == -2) { setImg(0); return; }
		if (indexImg >= 0)  { return; }
		if (pts == null)    { return; }
		background(0);
		if (view == 2) {
			showImage(res);
			textAlign(RIGHT);
			text("Initialization: " + ((kmeans) ? "k-means++" : "Random"), 482, 30);
			text("N: " + nbRep, 482, 50);
			textAlign(LEFT);
			text("Iteration: " + ((pts.rep.length == 0) ? "-" : pts.it), 20, 50);
			text("Loss: " +      ((pts.rep.length == 0) ? "-" : pts.loss), 20, 80);
		} else {
			textAlign(RIGHT);
			text("Initialization: " + ((kmeans) ? "k-means++" : "Random"), 482, 30);
			text("N: " + nbRep, 482, 50);
			textAlign(LEFT);
			text("Iteration: " + ((pts.rep.length == 0) ? "-" : pts.it), 20, 50);
			text("Loss: " +      ((pts.rep.length == 0) ? "-" : pts.loss), 20, 80);
			text("Radius <=> ", 20, 450);
			text(((view == 0) ? "Maximal distance to \ncorresponding points" : "Number of \ncorresponding points"), 150, 450);
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
	
	public void keyPressed() {
		if (indexImg >= 0) {
			switch (keyCode) {
				case LEFT : setImg(indexImg+images.length-1); break;
				case RIGHT: setImg(indexImg+1);               break;
				case ENTER: setImg(-1);                       break;
			}
		} else {
			if (pts == null) { initPts(); res = img; return; }
			switch (key) {
				case ' ' :  if (pts.rep.length == 0) pts.init(nbRep, kmeans); else pts.iterate(); 
							if (pts.rep.length == 0) res = img; else res = pts.apply(img); 
							break;
				case '\n':  if (pts.rep.length == 0) pts.init(nbRep, kmeans);
							double l0 = 0;
							while (l0 - pts.loss > epsilon || l0 == 0) {
								l0 = pts.loss;
								pts.iterate();
							}
							if (pts.rep.length == 0) res = img; else res = pts.apply(img); 
							break;
				case 'r' :  initPts(); res = img; break;
				case 'v' :  view = (view+1)%3; break;
				case 'i' :  kmeans   = !kmeans;   break;
				case '+' :  nbRep++; break;
				case '-' :  nbRep--; break;
				case '0' :  pts = null; indexImg = -2; break;
			}
		}
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
				gl.glPointSize((view == 0) ? 100 * (float) Math.sqrt(pts.repDist2[i]) : pts.repSize[i]*100/pts.pts.length);
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
		pts = new Kmeans(img); 
	}
	
	private void setImg(int i) {
		indexImg = i % images.length;
		if (indexImg >= 0) {
			img = loadImage(images[indexImg]);
			showImage(img);
			fill(color(0xFFAAAAAA));
			text("Choose an image.\n(Use arrows. Confirm with ENTER.)", 20, 30) ;
			fill(color(0xFFFFFFFF));
		} else {
			background(0);
			text("Manual: \n\n" +
				 "  - Use mouse to rotate view. \n" +
				 "  - Press SPACE to run a single iteration \n" +
				 "     of the algorithm. \n" +
				 "  - Press ENTER to run the algorithm \n" +
				 "     until stabilisation. \n" +
				 "  - Press R to run again from start \n" +
				 "     (with new seeds). \n" +
				 "  - Press V to switch view (change radius meaning\n" +
				 "     or show corresponding image). \n" +
				 "  - Press I to change initialization mode. \n" +
				 "  - Press +/- to change the number of seeds \n" +
				 "     (applied when the algorithme initializes). \n\n" +
				 "Press any key to start.", 20, 50);
		}
	}
	
	private void showImage(PImage img) {
		background(0);
		int m = Math.max(img.width, img.height);
		float h = 512*((float) img.height)/m ;
		float w = 512*((float) img.width)/m ;
		image(img,(512-w)/2,(512-h)/2,w,h);
	}
	
}
