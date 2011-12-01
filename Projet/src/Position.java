import java.util.Iterator;
import java.util.Stack;

import Jama.Matrix;
import Jama.SingularValueDecomposition;


public class Position {

	public Matrix R;
	public Pt3 t;
	public double aX, aY;
	
	private static double threshold2 = 0.0000001;
	
	
/**********************
 *    Constructors    *
 **********************/	
	
	public Position(Matrix R, Pt3 t) {
		this.R = new Matrix(3,3); this.t = new Pt3(); 
		this.R.set(0,0, R.get(0,0)); this.R.set(0,1, R.get(0,1)); this.R.set(0,2, R.get(0,2));
		this.R.set(1,0, R.get(1,0)); this.R.set(1,1, R.get(1,1)); this.R.set(1,2, R.get(1,2));
		this.R.set(2,0, R.get(2,0)); this.R.set(2,1, R.get(2,1)); this.R.set(2,2, R.get(2,2));
		this.t.x = t.x; this.t.y = t.y; this.t.z = t.z;
		this.aX = 0; this.aY = 0;
	}
	
	public Position(Stack<Pt2> pts2D, Stack<Pt3> pts3D, Matrix A, Matrix dist_coeffs) {    // Built solving PnP. DistCoeff = {{k1,k2,k3,p1,p2}} || {{k1,k2,p1,p2}} || {{k1,k2,k3,p1,p2}}^T || {{k1,k2,p1,p2}}^T || null.

		System.out.println();
		
		double fx = A.get(0, 0), fy = A.get(1,1), cx = A.get(0,2), cy = A.get(1,2);
		double k1, k2, k3, p1, p2;
		if (dist_coeffs == null) { k1 = k2 = k3 = p1 = p2 = 0; }
		else if (dist_coeffs.getColumnDimension() == 1) {
			k1 = dist_coeffs.get(0,0); k2 = dist_coeffs.get(1,0);
			if (dist_coeffs.getColumnDimension() == 4) { k3 = 0; p1 = dist_coeffs.get(2,0); p2 = dist_coeffs.get(3,0); }
			else { k3 = dist_coeffs.get(2,0); p1 = dist_coeffs.get(3,0); p2 = dist_coeffs.get(4,0); }
		} else {
			k1 = dist_coeffs.get(0,0); k2 = dist_coeffs.get(0,1);
			if (dist_coeffs.getRowDimension() == 4) { k3 = 0; p1 = dist_coeffs.get(0,2); p2 = dist_coeffs.get(0,3); }
			else { k3 = dist_coeffs.get(0,2); p1 = dist_coeffs.get(0,3); p2 = dist_coeffs.get(0,4); }
		}
		// Correct distortion
		Stack<Pt2> im  = new Stack<Pt2>();
		for (Pt2 p : pts2D) {
			if (dist_coeffs == null) {
				im.push(new Pt2((p.x-cx)/fx,(p.y-cy)/fy));
				
				System.out.println(p + "  ->  " + (new Pt2((p.x-cx)/fx,(p.y-cy)/fy)));
				
			} else {
				double r2 = p.x*p.x + p.y*p.y;
				double k = 1 + k1*r2 + k2*r2*r2 + k3*r2*r2*r2;
				double x = p.x * k + 2*p1*p.x*p.y + p2*(r2+2*p.x*p.x);
				double y = p.y * k + 2*p2*p.x*p.y + p1*(r2+2*p.y*p.y);
				im.push(new Pt2((x-cx)/fx,(y-cy)/fy));
				
				System.out.println(p + "  ->  " + (new Pt2((x-cx)/fx,(y-cy)/fy)));
				
			}
		}
		
		System.out.println();
		System.out.println("Seen:        " + pts2D);
		System.out.println("Corrected:   " + im);
		System.out.println();
		
		// Average point
		Pt3 M0 = new Pt3(); int nPts = 0;
		for (Pt3 p : pts3D) {
			nPts++;
			M0.add(p);
		}
		M0.multiply( 1. / (double) nPts );
		double d2 = Double.POSITIVE_INFINITY;
		Pt3 M3 = null; Pt2 M2 = null;           // M3 := Nearest 3D point to the average 3D points. M2 := M3 seen point 
		Iterator<Pt2> itIm = im.iterator();
		for (Pt3 p : pts3D) {
			if (p.dist2(M0) < d2) { M3 = p; M2 = itIm.next(); d2 = p.dist2(M0); }
			else itIm.next();
		}
		
		System.out.println("M3:   "+M3+"\n");
		System.out.println("M2:   "+M2+"\n");
		
		// Construct system matrix (object reference re-centered on M0)
		Matrix MList = new Matrix(nPts,3);
		nPts = 0;
		for (Pt3 p : pts3D) {
			MList.set(nPts,0, p.x-M3.x);
			MList.set(nPts,1, p.y-M3.y);
			MList.set(nPts,2, p.z-M3.z);
			nPts++;
		}
		SingularValueDecomposition svd = MList.svd();
		Matrix U = svd.getU(), S = svd.getS(), V = svd.getV();
		
		double a = S.get(0,0), b = S.get(1,1), c = S.get(2,2);
		a = 1/a; b = 1/b;
		c = (c > 0.001*a) ? 1/c : 0;
		S.set(0,0, a); S.set(1,1, b); S.set(2,2, c);	

		S = V.times(S).times(U.transpose());           // S = pseudo-inverse(MList)
		
		Matrix I = null, J = null, K = new Matrix(3,1);      //   I = s i , J = s j , K = s k   until the end of while loop
		double Z0 = 0;
		if (c == 0) {     // Coplanar object points   [ http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=00341055&tag=1 ]
			Matrix up = new Matrix(3,1); up.set(2,0, 1);
			up = V.times(up); up = up.times(1/up.norm2());   // up: orthogonal to {Mi}'s plan, with ||up|| = 1
			Matrix epsList1 = new Matrix(nPts,1);
			Matrix epsList2 = null;
			Matrix xList = new Matrix(nPts, 1) , yList = new Matrix(nPts, 1);
			int i = 0; for (Pt2 p : im) { xList.set(i,0, p.x*(1+epsList1.get(i,0))-M2.x ); i++; }
				i = 0; for (Pt2 p : im) { yList.set(i,0, p.y*(1+epsList1.get(i,0))-M2.y ); i++; }
			Matrix I1 = S.times(xList), J1 = S.times(yList), K1 = new Matrix(3,1);
			Matrix I2 = null, J2 = null, K2 = new Matrix(3,1);;
			a = - ( I1.get(0,0)*J1.get(0,0) + I1.get(1,0)*J1.get(1,0) + I1.get(2,0)*J1.get(2,0) );		// a = -I1.J1 
			b = J1.norm2() - I1.norm2();																// b = J1² - I1²
			double lambda = 0, mu = 0;
			double delta = Math.sqrt(b*b+4*a*a);
			lambda = Math.sqrt((delta+b)/2);
			mu     = Math.sqrt((delta-b)/2) * ((a < 0) ? -1 : 1);
			
			I2 = I1.plus(up.times(-lambda));
			J2 = J1.plus(up.times(-mu));
			
			I1 = I1.plus(up.times(lambda));
			J1 = J1.plus(up.times(mu));
			
			K2.set(0,0, I2.get(1,0)*J2.get(2,0) - I2.get(2,0)*J2.get(1,0)); 
			K2.set(1,0, I2.get(2,0)*J2.get(0,0) - I2.get(0,0)*J2.get(2,0)); 
			K2.set(2,0, I2.get(0,0)*J2.get(1,0) - I2.get(1,0)*J2.get(0,0));	
			double s1 = I2.norm2(), s2 = J2.norm2(); 
			K2.times((s1+s2)/(2*s1*s2));
			
			K1.set(0,0, I1.get(1,0)*J1.get(2,0) - I1.get(2,0)*J1.get(1,0)); 
			K1.set(1,0, I1.get(2,0)*J1.get(0,0) - I1.get(0,0)*J1.get(2,0)); 
			K1.set(2,0, I1.get(0,0)*J1.get(1,0) - I1.get(1,0)*J1.get(0,0));		// K <- I^J = s1 s2 k = (s1 s2 / s) s k , s = (s1+s2)/2
			s1 = I1.norm2(); s2 = J1.norm2(); 
			K1.times((s1+s2)/(2*s1*s2));		// K <- K * ( s / (s1 s2)) = K * ( (s1+s2) / (2 s1 s2) )
			
			Matrix epsList2_ = MList.times(K2);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = M3Mi.(sk) = M3Mi.K
			double d22 = epsList2_.minus(epsList1).norm2();
			epsList2 = epsList2_;

			Matrix epsList1_ = MList.times(K1);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = M3Mi.(sk) = M3Mi.K
			double d21 = epsList1_.minus(epsList1).norm2();
			epsList1 = epsList1_;
			
			
			
			// ....
			
			
			
		} else {          // Non-coplanar object points   [ http://www.cfar.umd.edu/~daniel/daniel_papersfordownload/Pose25Lines.pdf ]
			Matrix epsList = new Matrix(nPts,1);
			double s1 = 0, s2 = 0;
			d2 = Double.POSITIVE_INFINITY;
			while (d2 > threshold2) {
				Matrix xList = new Matrix(nPts, 1) , yList = new Matrix(nPts, 1);
				int i = 0; for (Pt2 p : im) { System.out.println(p+" <=> ("+(MList.get(i,0)+M3.x)+","+(MList.get(i,1)+M3.y)+","+(MList.get(i,2)+M3.z)+")");  xList.set(i,0, p.x*(1+epsList.get(i,0))-M2.x ); i++; }
					i = 0; for (Pt2 p : im) { yList.set(i,0, p.y*(1+epsList.get(i,0))-M2.y ); i++; }
				I = S.times(xList); J = S.times(yList);
				s1 = I.norm2(); s2 = J.norm2(); 
				I = I.times(1/s1); J = J.times(1/s2);
				K.set(0,0, I.get(1,0)*J.get(2,0) - I.get(2,0)*J.get(1,0)); 
				K.set(1,0, I.get(2,0)*J.get(0,0) - I.get(0,0)*J.get(2,0)); 
				K.set(2,0, I.get(0,0)*J.get(1,0) - I.get(1,0)*J.get(0,0));		// K <- I^J = s1 s2 k = (s1 s2 / s) s k , s = (s1+s2)/2
				Matrix epsList_ = MList.times(K).times((s1+s2)/2);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = s M3Mi.k
				d2 = epsList_.minus(epsList).norm2();
				epsList = epsList_;
			}
		}
		
		R = new Matrix(3,3);
		R.set(0,0, I.get(0,0)); R.set(0,1, I.get(1,0)); R.set(0,2, I.get(2,0));
		R.set(1,0, J.get(0,0)); R.set(1,1, J.get(1,0)); R.set(1,2, J.get(2,0));
		R.set(2,0, K.get(0,0)); R.set(2,1, K.get(1,0)); R.set(2,2, K.get(2,0));
		
		Stack<Drt3> drts = new Stack<Drt3>();
		itIm = im.iterator();
		for (Pt3 p : pts3D) drts.push(new Drt3(p,itIm.next().toPt3().apply(R.inverse()).times(-1)));
		t = new Pt3eval(drts).p.apply(R).times(-1);          // On évalue t comme l'intersection des droites correspondant aux points vues plutôt qu'avec le modèle utilisé pour plus de précision.

	}
	
		
	
//	public Position(Stack<Pt2> pts2D, Stack<Pt3> pts3D, Matrix A, Matrix dist_coeffs) {    // Built solving PnP. DistCoeff = {{k1,k2,k3,p1,p2}} || {{k1,k2,p1,p2}} || {{k1,k2,k3,p1,p2}}^T || {{k1,k2,p1,p2}}^T || null.
//		int nPts = pts3D.size();
//		double fx = A.get(0, 0), fy = A.get(1,1), cx = A.get(0,2), cy = A.get(1,2);
//		double k1, k2, k3, p1, p2;
//		if (dist_coeffs == null) { k1 = k2 = k3 = p1 = p2 = 0; }
//		else if (dist_coeffs.getColumnDimension() == 1) {
//			k1 = dist_coeffs.get(0,0); k2 = dist_coeffs.get(1,0);
//			if (dist_coeffs.getColumnDimension() == 4) { k3 = 0; p1 = dist_coeffs.get(2,0); p2 = dist_coeffs.get(3,0); }
//			else { k3 = dist_coeffs.get(2,0); p1 = dist_coeffs.get(3,0); p2 = dist_coeffs.get(4,0); }
//		} else {
//			k1 = dist_coeffs.get(0,0); k2 = dist_coeffs.get(0,1);
//			if (dist_coeffs.getRowDimension() == 4) { k3 = 0; p1 = dist_coeffs.get(0,2); p2 = dist_coeffs.get(0,3); }
//			else { k3 = dist_coeffs.get(0,2); p1 = dist_coeffs.get(0,3); p2 = dist_coeffs.get(0,4); }
//		}
//		// Correct distortion
//		Stack<Pt2> im  = new Stack<Pt2>();
//		for (Pt2 p : pts2D) {
//			if (dist_coeffs == null) im.push(new Pt2((p.x-cx)/fx,(p.y-cy)/fy));
//			else {
//				double r2 = p.x*p.x + p.y*p.y;
//				double k = 1 + k1*r2 + k2*r2*r2 + k3*r2*r2*r2;
//				double x = p.x * k + 2*p1*p.x*p.y + p2*(r2+2*p.x*p.x);
//				double y = p.y * k + 2*p2*p.x*p.y + p1*(r2+2*p.y*p.y);
//				im.push(new Pt2((x-cx)/fx,(y-cy)/fy));
//			}
//		}
//		// Calcul du centre de gravité
//		Pt3 mc = new Pt3();
//		for (Pt3 p : pts3D) { mc.x += p.x; mc.y += p.y; mc.z += p.z; }
//		mc.x /= nPts; mc.y /= nPts; mc.z /= nPts;
//		// Calcul de MM
//		
//		mc = new Pt3();
//		
//		double mm[][] = new double[3][3];
//		for (Pt3 p : pts3D) { 
//			mm[0][0] += (p.x - mc.x)*(p.x - mc.x);
//			mm[0][1] += (p.x - mc.x)*(p.y - mc.y);
//			mm[0][2] += (p.x - mc.x)*(p.z - mc.z);
//			mm[1][1] += (p.y - mc.y)*(p.y - mc.y);
//			mm[1][2] += (p.y - mc.y)*(p.z - mc.z);
//			mm[2][2] += (p.z - mc.z)*(p.z - mc.z);
//		}
//		mm[1][0] = mm[0][1]; mm[2][0] = mm[0][2]; mm[2][1] = mm[1][2]; 
//		SingularValueDecomposition svd = (new Matrix(mm)).svd();
//		// initialize extrinsic parameters
//
////System.out.println("\n***********************************************\n");
//		
//		if (true) { // planar case
//			R = svd.getV();
////System.out.println("\nR t -------------------------------");
////R.print(10,3);
//			if ( R.get(0,0)*R.get(0,0)+R.get(1,1)*R.get(1,1) < 0.000000001 ) R = Matrix.identity(3,3);
//			if ( R.det() < 0 ) R = R.times(-1);
//			t = mc.apply(R).times(-1);
////System.out.println(t);
//			
//			Stack<Pt2> objXY = new Stack<Pt2>();
//			for (Pt3 p : pts3D) {
//				objXY.push(p.apply(R).plus(t).cutToPt2());
//			}
//			Matrix H = Homographie.find(objXY, im);
//
////System.out.println("\n\nH ---------------------------------");
////H.print(10,3);
//
//			Pt3 h1 = new Pt3(H.get(0,0), H.get(1,0), H.get(2,0));
//			Pt3 h2 = new Pt3(H.get(0,1), H.get(1,1), H.get(2,1));
//			Pt3 tt = new Pt3(H.get(0,2), H.get(1,2), H.get(2,2));
//			double n1 = h1.norm(), n2 = h2.norm();
//			h1.rescale(1/n1); h2.rescale(1/n2); tt.rescale(2/(n1+n2));
//			Pt3 h3 = h1.cross(h2);
//			
//			H.set(0,0, h1.x); H.set(0,1, h2.x); H.set(0,2, h3.x);
//			H.set(1,0, h1.y); H.set(1,1, h2.y); H.set(1,2, h3.y);
//			H.set(2,0, h1.z); H.set(2,1, h2.z); H.set(2,2, h3.z);
//			
////System.out.println("H ---------------------------------");
////H.print(10,3);
//
//			H = rodrigues(rodrigues(H));
////System.out.println("H ---------------------------------");
////H.print(10,3);
//
//			t = t.apply(H).plus(tt);
//			R = H.times(R);
//			
//		} else {    // non planar case
//		}
//
////System.out.println("\n***********************************************\n");	
//	
//	}


/*********************
 *     Modifying     *
 *********************/
	
	public void moveForward(double dist) {
		t.add(new Pt3(0,0, -dist));
	}
	
	public void moveRight(double dist) {
		t.add(new Pt3(-dist,0,0));
	}
	
	public void rotate(double daX, double daY) {
		aX -= daX; aY += daY;
		if (aX > Math.PI/2) aX  = Math.PI/2; else if (aX < -Math.PI/2) aX = -Math.PI/2;
		if (aY > Math.PI)   aY -= Math.PI*2; else if (aY < -Math.PI)   aY += Math.PI*2;
		double c, s;
		Matrix rX = Matrix.identity(3,3);
		c = Math.cos(aX); s = Math.sin(aX);
		rX.set(1,1, c); rX.set(2,2, c); rX.set(2,1, s); rX.set(1,2, -s);
		Matrix rY = Matrix.identity(3,3);
		c = Math.cos(aY); s = Math.sin(aY);
		rY.set(0,0, c); rY.set(2,2, c); rY.set(0,2, s); rY.set(2,0, -s);
		t = t.apply(R.inverse());
		R = rY.times(rX).inverse();
		t = t.apply(R);
	}

		
	
	
/**********************
 *    Calculations    *
 **********************/	

	public static Matrix rodrigues(Matrix Rot) {
		Matrix res;
		Matrix r = Rot;
		if (r.getColumnDimension() == 1 || r.getRowDimension() == 1) {
			if (r.getRowDimension() == 1) r = Rot.transpose();
			double theta = r.norm2();
			r = r.times((theta == 0) ? 0 : 1/theta);
			double cos = Math.cos(theta), sin = Math.sin(theta);
			res = new Matrix(3,3);
			res.set(0, 1, -r.get(2,0)); res.set(0, 2,  r.get(1,0));  //        0  -rz  ry
			res.set(1, 0,  r.get(2,0)); res.set(1, 2, -r.get(0,0));  // res =  rz  0  -rx
			res.set(2, 0, -r.get(1,0)); res.set(2, 1,  r.get(0,0));  // 	  -ry  rx  0
			res = Matrix.identity(3,3).times(cos).plus(r.times(r.transpose()).times(1-cos)).plus(res.times(sin));
		} else {
			SingularValueDecomposition svd = r.svd();
			r = svd.getU().transpose().times(svd.getV());
			res = new Matrix(3,1);
			res.set(0,0, r.get(2,1) - r.get(1,2));
			res.set(1,0, r.get(0,2) - r.get(2,0));
			res.set(2,0, r.get(1,0) - r.get(0,1));
			double c = (r.get(0,0) + r.get(1,1) + r.get(2,2) - 1) / 2.;
			if (c > 1) { c = 1; } else if (c < -1) { c = -1; }
			double theta = Math.acos(c);
			if (res.norm2() < 0.00001) {
				if (c>0) {
					res = new Matrix(3,1);
				} else {
					res.set(0,0, Math.sqrt(Math.max( (r.get(0,0)+1)/2 , 0 )));
					res.set(1,0, Math.sqrt(Math.max( (r.get(1,1)+1)/2 , 0 )) * ((r.get(0,1) < 0) ? -1 : 1));
					res.set(2,0, Math.sqrt(Math.max( (r.get(2,2)+1)/2 , 0 )) * ((r.get(0,2) < 0) ? -1 : 1));
					if (Math.abs(res.get(0,0)) < Math.abs(res.get(1,0)) && Math.abs(res.get(0,0)) < Math.abs(res.get(2,0)) && (r.get(1,2) > 0) != (res.get(1,0)*res.get(2,0) > 0))
						res.set(3,0, -res.get(3,0));
					res = res.times(theta / res.norm2());
				}
			} else {
				res = res.times(theta / res.norm2());
			}
		}
		return res;
	}
	

}
