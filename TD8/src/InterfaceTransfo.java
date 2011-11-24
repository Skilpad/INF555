import Jama.Matrix;
import processing.core.PApplet;


public class InterfaceTransfo extends PApplet {
	
	int    nPoints = 70;
	double true_matching_rate  = 0.8;
	double asked_matching_rate = 0.7;
	double true_error  = 0.04; 
	double asked_error = 0.05; 
	
	int size = 350;
	
	public void setup() {
		// Preparing
		size(2*size, 2*size);
		background(0xFFFFFFFF);
		Matrix T = new Matrix(2,2);
		double th = Math.random()*2*Math.PI;
		T.set(0,0, Math.cos(th)); T.set(0,1, -Math.sin(th));
		T.set(1,0, Math.sin(th)); T.set(1,1,  Math.cos(th));
		Pt[] S = new Pt[nPoints];
		Pt[] P = new Pt[nPoints];
		// Create corresponding points
		int k;
		for (k = 0; k < nPoints*true_matching_rate; k++) {
			double r = Math.random();
			double a = Math.random()*2*Math.PI;
			S[k] = new Pt(r*Math.cos(a),r*Math.sin(a));
			double dr = true_error*(1+Math.cos(Math.PI*Math.random()))/2;
			double da = Math.random()*2*Math.PI;
			P[k] = S[k].apply(T).plus(new Pt(dr*Math.cos(da),dr*Math.sin(da)));
		}
		// Adding random points
		for (; k < nPoints; k++) {
			double r = Math.random();
			double a = Math.random()*2*Math.PI;
			S[k] = new Pt(r*Math.cos(a),r*Math.sin(a));
			r = Math.random();
			a = Math.random()*2*Math.PI;
			P[k] = new Pt(r*Math.cos(a),r*Math.sin(a));
		}
		// Using
		fill(color(0xFF0000FF)); stroke(color(0xFF0000FF));
		plot(P, 1, 0); plot(P, 0, 1); plot(P, 1, 1); 
		fill(color(0xFFFF0000)); stroke(color(0xFFFF0000));
		plot(S, 0, 0);
		Matrix T_RANSAC = Transformation.matchRANSAC(S, P, asked_error, asked_matching_rate);
		print("\n");
		print("True T matrix:");       T.print(10,5);
		print("Calculated T matrix:"); T_RANSAC.print(10,5);
		Transformation.giveCaracs(T_RANSAC, S, P, asked_error);
		print("See result on bottom-left quarter.\n\n");
		plot(Transformation.apply(T_RANSAC, S), 0, 1); 
		print("----------------------------------------\n\n");
		Matrix T_NAIVE  = Transformation.matchNAIVE(S, P, asked_error, asked_matching_rate);
		print("\n");
		print("True T matrix:");       T.print(10,5);
		print("Calculated T matrix:"); T_NAIVE.print(10,5);
		Transformation.giveCaracs(T_NAIVE, S, P, asked_error);
		print("See result on bottom-right quarter.\n\n");
		plot(Transformation.apply(T_NAIVE , S), 1, 1); 
		print("----------------------------------------\n\n");
	}
	
	public void plot(Pt[] P, int dx, int dy) {
		for (Pt p : P) plot(p, dx, dy);
	}

	public void plot(Pt p, int dx, int dy) {
		ellipse((int) (size*(dx+(1+p.x)/2)), (int) (size*(dy+(1+p.y)/2)), 4, 4);
	}

}
