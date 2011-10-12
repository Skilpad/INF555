// 2011 INF555 Frank NIELSEN
import javax.media.opengl.*;
import processing.opengl.*;
PImage img;
PGraphicsOpenGL pgl;
GL gl;

// pixel colors 
int n;
float [] x;
float [] y;
float [] z;


void createPointSet()
{
 x=new float[n]; y=new float[n]; z=new float[n];
 println("Creating the 3D color point set for "+n+" pixels.");
 for(int i=0;i<n;i++)
 { x[i]=red(img.pixels[i])/255.0;
  y[i]=green(img.pixels[i])/255.0;
  z[i]=blue(img.pixels[i])/255.0;}
}
 
void setup() {
  size(512, 512, OPENGL);  
  img = loadImage("polytechnique.png");
  loadPixels(); 
  n=img.width*img.height;
  createPointSet();
  colorMode(RGB, 1.0, 1.0, 1.0);
  pgl = (PGraphicsOpenGL) g;
  gl = pgl.beginGL(); 
 
}

void draw() {   
  background(1);
 gl.glMatrixMode(GL.GL_PROJECTION);
  gl.glLoadIdentity();
  gl.glOrtho(-2,2,-2,2,-100,100);  
  gl.glMatrixMode(GL.GL_PROJECTION);
  gl.glLoadIdentity();
  gl.glOrtho(-2,2,-2,2,-100,100);  
  renderPointSet(); 
}

float rotx=0.0;

void renderPointSet() { 
  gl.glMatrixMode(GL.GL_MODELVIEW);
  gl.glLoadIdentity();
  gl.glPointSize(1.0);
  
  gl.glRotatef(rotx,1.0,0.0,0.0);

  
  gl.glBegin(GL.GL_POINTS);
  for(int i=0;i<n;i++)
  {
  gl.glColor3f(x[i],y[i],z[i]);  
  gl.glVertex3f(x[i],y[i],z[i]);
  }
   gl.glEnd();
    
    gl.glPointSize(10.0);
      gl.glBegin(GL.GL_POINTS);
    gl.glColor3f(1.0,0.0,0.0);
    gl.glVertex3f(0.5,0.5,0.5);
  gl.glEnd();

 rotx+=1.0;
}
