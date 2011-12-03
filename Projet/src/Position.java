import java.util.Iterator;
import java.util.Stack;

import Jama.Matrix;
import Jama.SingularValueDecomposition;


public class Position {

	public Matrix R;
	public Pt3 t;
	public double aX, aY;
	
	private static double threshold2  = 0.0000001;
	private static double threshold2d = 0.0000001;
	
	
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
		c = (c > 0.00001*a) ? 1/c : 0;
		S.set(0,0, a); S.set(1,1, b); S.set(2,2, c);	

		S = V.times(S).times(U.transpose());           // S = pseudo-inverse(MList)
		
		
		
		if (c == 0) {     // Coplanar object points   [ http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=00341055&tag=1 ]
			
			
			
			Matrix up = new Matrix(3,1); up.set(2,0, 1);
			up = V.times(up); up = up.times(1/up.norm2());   // up: orthogonal to {Mi}'s plan, with ||up|| = 1
			
			System.out.println("up: "); up.print(5,5);
			
			boolean[] done    = new boolean[2];   
			Matrix[]  epsList = new Matrix[2];
			Matrix[]  R       = new Matrix[2];
			Pt3[]     t       = new Pt3[2];
			double[]  d2d     = new double[2];
			double[]  s       = new double[2];
			
			done[0] = false; done[1] = false;
			epsList[0] = new Matrix(nPts,1); epsList[1] = null;
			
			while ( ! (done[0] && done[1])) {
				for (int i = 0; i < 2; i++) {
					Matrix xList = new Matrix(nPts, 1) , yList = new Matrix(nPts, 1);
					int k = 0; for (Pt2 p : im) { xList.set(k,0, p.x*(1+epsList[i].get(k,0))-M2.x ); k++; }
						k = 0; for (Pt2 p : im) { yList.set(k,0, p.y*(1+epsList[i].get(k,0))-M2.y ); k++; }
					Matrix I0 = S.times(xList), J0 = S.times(yList);
					
					Matrix K0 = new Matrix(3,1);
					K0.set(0,0, I0.get(1,0)*J0.get(2,0) - I0.get(2,0)*J0.get(1,0));
					K0.set(1,0, I0.get(2,0)*J0.get(0,0) - I0.get(0,0)*J0.get(2,0));
					K0.set(2,0, I0.get(0,0)*J0.get(1,0) - I0.get(1,0)*J0.get(0,0));
					Matrix Ivu = new Matrix(3,1);
					Ivu.set(0,0, I0.get(1,0)*up.get(2,0) - up.get(2,0)*J0.get(1,0));
					Ivu.set(1,0, I0.get(2,0)*up.get(0,0) - up.get(0,0)*J0.get(2,0));
					Ivu.set(2,0, I0.get(0,0)*up.get(1,0) - up.get(1,0)*J0.get(0,0));
					Matrix Jvu = new Matrix(3,1);
					Jvu.set(0,0, J0.get(1,0)*up.get(2,0) - up.get(2,0)*J0.get(1,0));
					Jvu.set(1,0, J0.get(2,0)*up.get(0,0) - up.get(0,0)*J0.get(2,0));
					Jvu.set(2,0, J0.get(0,0)*up.get(1,0) - up.get(1,0)*J0.get(0,0));
					
					
					a = - ( I0.get(0,0)*J0.get(0,0) + I0.get(1,0)*J0.get(1,0) + I0.get(2,0)*J0.get(2,0) );		// a = -I0.J0
					b = J0.get(0,0)*J0.get(0,0) + J0.get(1,0)*J0.get(1,0) + J0.get(2,0)*J0.get(2,0) - I0.get(0,0)*I0.get(0,0) - I0.get(1,0)*I0.get(1,0) - I0.get(2,0)*I0.get(2,0);	// b = J0² - I0²
					double delta = Math.sqrt(b*b+4*a*a);
					double lambda = Math.sqrt((delta+b)/2);
					double mu     = Math.sqrt((delta-b)/2) * ((a < 0) ? -1 : 1);

					System.out.println("##################################");
					System.out.println("\nI0");
					I0.print(20,20); 
					System.out.println(I0.norm2());
					System.out.println("\nJ0");
					J0.print(20,20);
					System.out.println(J0.norm2());
					System.out.println("\nK0");
					K0.print(20,20);
					System.out.println(K0.norm2());
					System.out.println("\nup");
					up.print(20,20);
					System.out.println(up.norm2());
					System.out.println("\nIvu");
					Ivu.print(20,20);
					System.out.println(Ivu.norm2());
					System.out.println("\nJvu");
					Jvu.print(20,20);
					System.out.println(Jvu.norm2());
					System.out.println("a: " + a);
					System.out.println("b: " + b);
					System.out.println("lambda: " + lambda);
					System.out.println("mu: "     + mu);
					System.out.println("##################################");
					
					Matrix[] I    = new Matrix[2]; Matrix[] J = new Matrix[2]; Matrix[] K = new Matrix[2];
					double[] d2d_ = new double[2];
					Matrix[]  R_  = new Matrix[2];
					Pt3[]     t_  = new Pt3[2];
					I[0] =  I0.plus(up.times(lambda)); J[0] =  J0.plus(up.times(mu)); K[0] = K0.plus(Ivu.times(mu)).plus(Jvu.times(lambda));
					I[1] = I0.minus(up.times(lambda)); J[1] = J0.minus(up.times(mu)); K[1] = K0.minus(Ivu.times(mu)).minus(Jvu.times(lambda));

					
					I[0].print(20,20); 
					System.out.println(I[0].norm2());
					J[0].print(20,20);
					System.out.println(J[0].norm2());
					K[0].print(20,20);
					System.out.println(K[0].norm2());

//					I[0] = new Matrix(3,1); I[0].set(0,0,1);  I[1] = new Matrix(3,1); I[1].set(0,0,1);
//					J[0] = new Matrix(3,1); J[0].set(1,0,1);  J[1] = new Matrix(3,1); J[1].set(1,0,-1);
//					K[0] = new Matrix(3,1); K[0].set(2,0,1);  K[1] = new Matrix(3,1); K[1].set(2,0,-1);
					
					for (int j = 0; j < 2; j++) {
//						double s1 = I[j].norm2(); double s2 = J[j].norm2(); 
						s[j] = K[j].norm2();
						I[j] = I[j].times(1/I[j].norm2()); J[j] = J[j].times(1/J[j].norm2()); K[j] = K[j].times(1/K[j].norm2());
//						K[j] = new Matrix(3,1);
//						K[j].set(0,0, I[j].get(1,0)*J[j].get(2,0) - I[j].get(2,0)*J[j].get(1,0)); 
//						K[j].set(1,0, I[j].get(2,0)*J[j].get(0,0) - I[j].get(0,0)*J[j].get(2,0)); 
//						K[j].set(2,0, I[j].get(0,0)*J[j].get(1,0) - I[j].get(1,0)*J[j].get(0,0));		// K <- I^J = s1 s2 k = (s1 s2 / s) s k , s = (s1+s2)/2
						
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						I[j].print(10,10); J[j].print(10,10); K[j].print(10,10);
						System.out.println(I[j].norm2() + " - " + J[j].norm2() + " - " + K[j].norm2() + "\n");
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						
						R_[j] = new Matrix(3,3);
						R_[j].set(0,0, I[j].get(0,0)); R_[j].set(0,1, I[j].get(1,0)); R_[j].set(0,2, I[j].get(2,0));
						R_[j].set(1,0, J[j].get(0,0)); R_[j].set(1,1, J[j].get(1,0)); R_[j].set(1,2, J[j].get(2,0));
						R_[j].set(2,0, K[j].get(0,0)); R_[j].set(2,1, K[j].get(1,0)); R_[j].set(2,2, K[j].get(2,0));		
//						t_[j] = new Pt3(K[j]).times(1/s[j]).apply(R_[j]).times(-1).plus(M3);
						t_[j] = new Pt3(K[j]).times(1/s[j]).times(-1).plus(M3);
						
						Position pos = new Position(R_[j],t_[j]);
						d2d_[j] = 0;
						itIm = im.iterator();
//						for (Pt3 p : pts3D)  d2d_[j] += p.toPt2Im(pos, Matrix.identity(3,3)).dist2(itIm.next());
						
//						this.R = pos.R; this.t = pos.t; if (true) return;
						
						pos.R.print(5, 5); System.out.println(pos.t+"\n");
						for (Pt3 p : pts3D) {
							Pt2 pp  = p.toPt2Im(pos, Matrix.identity(3,3));
							if (pp == null) { d2d_[j] = Double.POSITIVE_INFINITY; break; }
							Pt2 pp_ = itIm.next();
							d2d_[j] += pp.dist2(pp_);
						}
					}
					int j = (d2d_[0] < d2d_[1]) ? 0 : 1;
					if (d2d_[j] < threshold2d*nPts) { this.R = R_[j]; this.t = t_[j]; return; }
					if (epsList[1] == null) {
						R[0] = R_[0]; t[0] = t_[0]; d2d[0] = d2d_[0];
						epsList[0] = MList.times(K[0]).times(s[0]);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = s M3Mi.k
						R[1] = R_[1]; t[1] = t_[1]; d2d[1] = d2d_[1];
						epsList[1] = MList.times(K[1]).times(s[1]);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = s M3Mi.k
						break;
					} else {
						R[i] = R_[j]; t[i] = t_[j]; d2d[i] = d2d_[j];
						Matrix epsList_ = MList.times(K[j]).times(s[j]);			//  after one iteration: Z0 = f/s, and thus 1/Z0 = s/f = s. So: (M3Mi.k)/Z0 = s M3Mi.k
						if (epsList_.minus(epsList[i]).norm2() < threshold2*nPts) done[i] = true;
						epsList[i] = epsList_;
					}					
				}
			}
			int j = (d2d[0] < d2d[1]) ? 0 : 1;
			this.R = R[j]; this.t = t[j];
			
			
			
		} else {          // Non-coplanar object points   [ http://www.cfar.umd.edu/~daniel/daniel_papersfordownload/Pose25Lines.pdf ]
			
			
			
			Matrix I = null, J = null, K = new Matrix(3,1);      //   I = s i , J = s j , K = s k   until the end of while loop
			Matrix epsList = new Matrix(nPts,1);
			double s1 = 0, s2 = 0;
			d2 = Double.POSITIVE_INFINITY;
			while (d2 > threshold2*nPts) {
				Matrix xList = new Matrix(nPts, 1) , yList = new Matrix(nPts, 1);
				int i = 0; for (Pt2 p : im) { xList.set(i,0, p.x*(1+epsList.get(i,0))-M2.x ); i++; }
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
			R = new Matrix(3,3);
			R.set(0,0, I.get(0,0)); R.set(0,1, I.get(1,0)); R.set(0,2, I.get(2,0));
			R.set(1,0, J.get(0,0)); R.set(1,1, J.get(1,0)); R.set(1,2, J.get(2,0));
			R.set(2,0, K.get(0,0)); R.set(2,1, K.get(1,0)); R.set(2,2, K.get(2,0));	
			Stack<Drt3> drts = new Stack<Drt3>();
			itIm = im.iterator();
			for (Pt3 p : pts3D) drts.push(new Drt3(p,itIm.next().toPt3().apply(R.inverse()).times(-1)));
			t = new Pt3eval(drts).p.apply(R).times(-1);          // On évalue t comme l'intersection des droites correspondant aux points vues plutôt qu'avec le modèle utilisé pour plus de précision.
			
			
			
//			t = new Pt3(K.times(2/(s1+s2))).apply(R).times(-1).plus(M3);
		}
		
		

	}
	
		

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
