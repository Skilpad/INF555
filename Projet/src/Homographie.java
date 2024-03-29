import java.awt.Point;
import java.util.Stack;
import processing.core.*;
import Jama.Matrix;



public class Homographie {
	
	public static Matrix find(Stack<?> from, Stack<?> to) {
		
//		System.out.println("Homographie:");
//		System.out.println("From:");
//		System.out.println("   "+from);
//		System.out.println("To:");
//		System.out.println("   "+to);
		
		Stack fromC = new Stack() , toC = new Stack();
		for (Object p : from) { fromC.push(p); }
		for (Object p : to  ) {   toC.push(p); }
		
		int n = from.size();
		if (n < 4)          {throw new Error("Homographie.find: Not enought point");}
		if (n != to.size()) {throw new Error("Homographie.find: The 2 args don't have the same number of point");}
		if ( ! ( (from.peek() instanceof Pt2 && from.peek() instanceof Pt2) || (from.peek() instanceof Pt3 && from.peek() instanceof Pt3) ) )
							{throw new Error("Homographie.find: The 2 args don't have the same class, among Pt or Vect");}
		boolean typeIsPt = from.peek() instanceof Pt2;
		// On definit la matrice A et Q
		double[][] A_array = new double[2*n][8];
		double[][] Q_array = new double[2*n][1];
		int i = 0;
		while (!fromC.isEmpty()) {
			Pt2 p = (typeIsPt) ? (Pt2) fromC.pop() : new Pt2((Pt3) fromC.pop());
			Pt2 q = (typeIsPt) ? (Pt2)   toC.pop() : new Pt2((Pt3)   toC.pop());
//		while (!from.isEmpty()) {
//			Pt2 p = (typeIsPt) ? (Pt2) from.pop() : new Pt2((Pt3) from.pop());
//			Pt2 q = (typeIsPt) ? (Pt2)   to.pop() : new Pt2((Pt3)   to.pop());
			double[] l1 = {p.x, p.y, 1,   0,   0, 0, -p.x*q.x, -p.y*q.x};
			double[] l2 = {  0,   0, 0, p.x, p.y, 1, -p.x*q.y, -p.y*q.y};
			A_array[i  ] = l1;
			A_array[i+1] = l2;
			Q_array[i  ][0] = q.x;
			Q_array[i+1][0] = q.y;			
			i+=2;
		}
		Matrix A = new Matrix(A_array);
		Matrix Q = new Matrix(Q_array);
		// On calcule son inverse ou son pseudo-inverse B
		Matrix B;
		if (n==4) {
			B = A.inverse();
		} else {
			Matrix At = A.transpose();
			B = At.times(A).inverse().times(At);
		}
		// On calcule l'homographie
		Matrix h_vert = B.times(Q);
		double[][] H_array = {{h_vert.get(0, 0), h_vert.get(1, 0), h_vert.get(2, 0)},
							  {h_vert.get(3, 0), h_vert.get(4, 0), h_vert.get(5, 0)},
							  {h_vert.get(6, 0), h_vert.get(7, 0), 1               }};
		Matrix H = new Matrix(H_array);
		return (typeIsPt) ? H : H.inverse().transpose();
	}
	
	public static PImage invert(Matrix H, PImage img, Point dstDimension) {
		PImage res = new PImage(dstDimension.x, dstDimension.y);
//		PImage res = new PImage(img.width, img.height);
		for (int x = 0; x < img.width; x++) {
			for (int y = 0; y < img.height; y++) {
				// Point inverse theorique (x0,y0) ?
				double[][] q_array = {{x},{y},{1}};
				Matrix P = H.times(new Matrix(q_array));
				double x0 = P.get(0, 0)/P.get(2, 0);
				double y0 = P.get(1, 0)/P.get(2, 0);
				// S'il est hors de l'image originale, on rend le pixel transparant. 
				if (x0 < 0 || x0 >= img.width || y0 < 0 || y0 >= img.height)
					res.set(x, y, 0x00000000);
				else
					res.set(x, y, img.get((int) x0, (int) y0));
			}
		}
		return res;
	}

	public static PImage invert(Matrix H, PImage img) {
		return invert(H, img, new Point(img.width, img.height));
	}
	
	public static PImage apply(Matrix H, PImage img, Point dstDimension) {
		return invert(H.inverse(), img, dstDimension);
	}

	public static PImage apply(Matrix H, PImage img) {
		return invert(H.inverse(), img);
	}
	
}
