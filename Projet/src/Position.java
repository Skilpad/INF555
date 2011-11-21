import java.util.Stack;

import Jama.Matrix;
import Jama.SingularValueDecomposition;


public class Position {

	public Matrix R;
	public Pt3 t;
	
	
	
/**********************
 *    Constructors    *
 **********************/	
	
	public Position(Matrix R, Pt3 t) {
		this.R = R; this.t = t;
	}
	
	public Position(Stack<Pt2> pts2D, Stack<Pt3> pts3D, Matrix A, Matrix dist_coeffs) {    // Built solving PnP. DistCoeff = {{k1,k2,k3,p1,p2}} || {{k1,k2,p1,p2}} || {{k1,k2,k3,p1,p2}}^T || {{k1,k2,p1,p2}}^T || null.
		int nPts = pts3D.size();
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
			if (dist_coeffs == null) im.push(new Pt2((p.x-cx)/fx,(p.y-cy)/fy));
			else {
				double r2 = p.x*p.x + p.y*p.y;
				double k = 1 + k1*r2 + k2*r2*r2 + k3*r2*r2*r2;
				double x = p.x * k + 2*p1*p.x*p.y + p2*(r2+2*p.x*p.x);
				double y = p.y * k + 2*p2*p.x*p.y + p1*(r2+2*p.y*p.y);
				im.push(new Pt2((x-cx)/fx,(y-cy)/fy));
			}
		}
		// Calcul du centre de gravité
		Pt3 mc = new Pt3();
		for (Pt3 p : pts3D) { mc.x += p.x; mc.y += p.y; mc.z += p.z; }
		mc.x /= nPts; mc.y /= nPts; mc.z /= nPts;
		// Calcul de MM
		
		mc = new Pt3();
		
		double mm[][] = new double[3][3];
		for (Pt3 p : pts3D) { 
			mm[0][0] += (p.x - mc.x)*(p.x - mc.x);
			mm[0][1] += (p.x - mc.x)*(p.y - mc.y);
			mm[0][2] += (p.x - mc.x)*(p.z - mc.z);
			mm[1][1] += (p.y - mc.y)*(p.y - mc.y);
			mm[1][2] += (p.y - mc.y)*(p.z - mc.z);
			mm[2][2] += (p.z - mc.z)*(p.z - mc.z);
		}
		mm[1][0] = mm[0][1]; mm[2][0] = mm[0][2]; mm[2][1] = mm[1][2]; 
		SingularValueDecomposition svd = (new Matrix(mm)).svd();
		// initialize extrinsic parameters

System.out.println("\n***********************************************\n");
		
		if (true) { // planar case
			R = svd.getV();
System.out.println("\nR t -------------------------------");
R.print(10,3);
			if ( R.get(0,0)*R.get(0,0)+R.get(1,1)*R.get(1,1) < 0.000000001 ) R = Matrix.identity(3,3);
			if ( R.det() < 0 ) R = R.times(-1);
			t = mc.apply(R).times(-1);
System.out.println(t);
			
			Stack<Pt2> objXY = new Stack<Pt2>();
			for (Pt3 p : pts3D) {
				objXY.push(p.apply(R).plus(t).cutToPt2());
			}
			Matrix H = Homographie.find(objXY, im);

System.out.println("\n\nH ---------------------------------");
H.print(10,3);

			Pt3 h1 = new Pt3(H.get(0,0), H.get(1,0), H.get(2,0));
			Pt3 h2 = new Pt3(H.get(0,1), H.get(1,1), H.get(2,1));
			Pt3 tt = new Pt3(H.get(0,2), H.get(1,2), H.get(2,2));
			double n1 = h1.norm(), n2 = h2.norm();
			h1.rescale(1/n1); h2.rescale(1/n2); tt.rescale(2/(n1+n2));
			Pt3 h3 = h1.cross(h2);
			
			H.set(0,0, h1.x); H.set(0,1, h2.x); H.set(0,2, h3.x);
			H.set(1,0, h1.y); H.set(1,1, h2.y); H.set(1,2, h3.y);
			H.set(2,0, h1.z); H.set(2,1, h2.z); H.set(2,2, h3.z);
			
System.out.println("H ---------------------------------");
H.print(10,3);

			H = rodrigues(rodrigues(H));
System.out.println("H ---------------------------------");
H.print(10,3);

			t = t.apply(H).plus(tt);
			R = H.times(R);
			
		} else {    // non planar case
		}

System.out.println("\n***********************************************\n");	
	
	}


/*********************
 *     Modifying     *
 *********************/
	
	public void moveForward(double dist) {
		t.add((new Pt3(0,0, dist)).apply(R));
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
