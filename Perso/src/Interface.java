import javax.media.opengl.*;
import javax.swing.JFileChooser;

import processing.core.*;


public class Interface extends PApplet {
	
	String file ="sea-turtle-2.jpg";
	PImage img;
	PImage res;
	
	int threshold[] = {128};
	int values[]    = {0xFF000000,0xFFFFFFFF};
	int n = 1;
	int k;
	int kk = 0;
	int mode = 0; // 0 : threshold ; 1 : value ; 2 : n
	
	public void setup() {
		img = loadImage(file);
		size(img.width, img.height);
		keyPressed();
	}
	
	public void draw() {
		image(res, 0,0);
	}

	
	public void keyPressed() {
		switch (key) {
			case 't' :  mode = 0; if (k > n-1) k = n-1; break;
			case 'v' :  mode = 1; if (k > n)   k = n;   break;
			case 'n' :  mode = 2; break;
			case 'o' :  openF(); break;
			case 's' :  saveF(); break;
		}
		switch (keyCode) {
			case RIGHT :  if (mode == 0) threshold[k]++; else if (mode == 1) values[k]+=(1+256+256*256); else setN(n+1); break;
			case LEFT  :  if (mode == 0) threshold[k]--; else if (mode == 1) values[k]-=(1+256+256*256); else setN(n-1); break;
			case UP    :  kk--; break;
			case DOWN  :  kk++; break;
//			case UP    :  if (mode == 0) threshold[k]++; else if (mode == 1) values[k]+=(1+256+256*256); else setN(n+1); break;
//			case DOWN  :  if (mode == 0) threshold[k]--; else if (mode == 1) values[k]-=(1+256+256*256); else setN(n-1); break;
//			case RIGHT :  k++; if (mode == 0 && k > n-1) k = n-1; if (mode == 1 && k > n) k = n; break;
//			case LEFT  :  if (k>0) k--; break;
		}
		readKK();
		System.out.println("=============================\n");
		System.out.println(n + " :" + ((mode == 2) ? "            <-" : ""));
		System.out.println();
		for (int i = 0; i < n; i++) {
			System.out.println(values[i] + ((mode == 1 && i == k) ? "   <-" : "")); 
			System.out.println("  >  "+threshold[i] + ((mode == 0 && i == k) ? "   <-" : ""));
		}   System.out.println(values[n] + ((mode == 1 && n == k) ? "   <-" : ""));
		res = BlackNwhite.blackNwhite(img, threshold, values, this);
	}
	
	private void setN(int k) {
		if (k==0) return;
		this.k = 0;
		n = k;
		threshold = new int[n];
		values    = new int[n+1];
		for (int i = 0; i < n; i++) {
			threshold[i] = (int) ( 256 * (double) (i+1) / (double) (n+1) );
			int x = (int) (((double) 256) / ((double) n));
			values[i]    = 0xFF000000 + x * (1+256+256*256);
		}
		values[n] = 0xFFFFFFFF;
	}
	
	private void readKK() {
		if (kk < 0)     kk = 0;
		if (kk > 2*n+1) kk = 2*n+1;
		if (kk == 0) {
			mode = 2;
		} else
		if ( 2*(kk/2) == kk ) {
			mode = 0; k = (kk/2)-1;
		} else {
			mode = 1; k = kk/2;
		}
	}
	
	private void openF() {
		String f = selectInput();
		if (f != null) {
			img = loadImage(f);
			size(img.width, img.height);
			keyPressed();
		}
	}

	private void saveF() {
		String f = selectOutput();
		if (f != null) res.save(f);
	}
}
