import java.util.Stack;

import Jama.Matrix;


public class Transformation {

	
	public static Matrix match(Pt s1, Pt s2, Pt p1, Pt p2) {    // Transformation T with: T si = pi  (i = 1 or 2)
		if (s1.equals(s2)) return new Matrix(2,2);
		Matrix S = new Matrix(2,2);
		S.set(0,0, s1.x); S.set(0,1, s2.x);
		S.set(1,0, s1.y); S.set(1,1, s2.y);
		Matrix P = new Matrix(2,2);
		P.set(0,0, p1.x); P.set(0,1, p2.x);
		P.set(1,0, p1.y); P.set(1,1, p2.y);
		return P.times(S.inverse());
	}

	
	public static Matrix matchRANSAC(Pt[] S, Pt[] P, double error_max, double matching_rate) {
		double err2 = error_max*error_max;
		int matching = 0;
		int n = S.length;
		Matrix T = null;
		int it = 0;
		while (matching < n * matching_rate) {
			it++;		
			matching = 0;
			int a = (int) (n * Math.random());
			int b = (int) (n * Math.random());
			int i = (int) (n * Math.random());
			int j = (int) (n * Math.random());
			T = match(S[a], S[b], P[i], P[j]);
			Pt[] TS = apply(T, S);
			for (Pt p : P) for (Pt Ts : TS) { if (Ts.dist2(p) < err2) { matching++; break; } }
		}
		System.out.println("RANSAC matching: "+it+" iterations");
		return T;
	}
	
	public static Matrix matchNAIVE(Pt[] S, Pt[] P, double error_max, double matching_rate) {
		double err2 = error_max*error_max;
		int matching  = 0;
		int n = S.length;
		Matrix T = null;
		int it = 0;
		for (int a = 1; a < S.length; a++) {
			for (int b = 0; b < a; b++) {
				for (int i = 1; i < P.length; i++) {
					for (int j = 0; j < i; j++) {
						it++;
						Matrix T_ = match(S[a], S[b], P[i], P[j]);
						int matching_ = 0;
						Pt[] TS = apply(T_, S);						
						for (Pt p : P) for (Pt Ts : TS) { if (Ts.dist2(p) < err2) { matching_++; break; } }
						if (matching_ > matching) { T = T_; matching = matching_; }
					}
				}
			}
		}
		System.out.println("NAIVE  matching: "+it+" iterations");
		return T;		
	}

	public static void giveCaracs(Matrix T, Pt[] S, Pt[] P, double error_max) {
		double err2 = error_max*error_max;
		int    nMatching  = 0;
		double eMatching2 = 0;
		for (Pt s : S) {
			Pt Ts = s.apply(T);
			for (Pt p : P) { if (Ts.dist2(p) < err2) { nMatching++; eMatching2 += Ts.dist2(p); break; } }
		}
		System.out.println();
		System.out.println("Number of matching points: " + nMatching);
		System.out.println("Matching rate:             " + (((double) nMatching)/((double) S.length)));
		System.out.println();
		System.out.println("Error for matching points: " + Math.sqrt(eMatching2)/((double) S.length));
		System.out.println("(Quadratic. Average.)");
		System.out.println();
	}
	
	public static Pt[] apply(Matrix T, Pt[] S) {
		Pt[] P = new Pt[S.length];
		for (int k = 0; k < S.length; k++) {
			P[k] = S[k].apply(T);
		}
		return P;
	}
	
}
