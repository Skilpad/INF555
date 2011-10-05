import java.util.Stack;

import Jama.Matrix;



public class Homographie {
	
	static Matrix find(Stack<Pt> from, Stack<Pt> to) {
		int n = from.size();
		if (n < 4)          {throw new Error("Homographie.find: Not enought point");}
		if (n != to.size()) {throw new Error("Homographie.find: The 2 args don't have the same number of point");}
		// On definit la matrice A et Q
		double[][] A_array = new double[2*n][8];
		double[][] Q_array = new double[2*n][1];
		int i = 0;
		while (!from.isEmpty()) {
			Pt p = from.pop();
			Pt q = to.pop();
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
//		System.out.println("A:");A.print(6,2);System.out.println(""); 
//		System.out.println("Q:");Q.print(6,2);System.out.println(""); 
//		System.out.println("B:");B.print(6,2);System.out.println("");
		
		Matrix h_vert = B.times(Q);
		double[][] H_array = {{h_vert.get(0, 0), h_vert.get(1, 0), h_vert.get(2, 0)},
							  {h_vert.get(3, 0), h_vert.get(4, 0), h_vert.get(5, 0)},
							  {h_vert.get(6, 0), h_vert.get(7, 0), 1               }};
		return new Matrix(H_array);
	}

}
