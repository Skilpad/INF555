import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;


public class Tests {

	private static void pr(Object s) { System.out.println(s); }

	
	private static void pnp() {
		
		Point windowDim = new Point(1024,768);
		Matrix A = Matrix.identity(3,3);
		A.set(0,0, 100); A.set(1,1, 100); // A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);

		Stack<Pt3> pts = new Stack<Pt3>();
		pts.push(new Pt3( 0, 0,0));
		pts.push(new Pt3( 1, 1,0)); 
		pts.push(new Pt3( 1,-1,0)); 
		pts.push(new Pt3(-1, 1,0)); 
		pts.push(new Pt3(-1,-1,0)); 
		
		Matrix R = Matrix.identity(3,3), R2 = Matrix.identity(3,3);
		double th = 0.1; double c = Math.cos(th); double s = Math.sin(th);
		 R.set(1,1,c);  R.set(2,2,c);  R.set(2,1,s);  R.set(1,2,-s);
		R2.set(0,0,c); R2.set(2,2,c); R2.set(0,2,s); R2.set(2,0,-s);
		R = R.times(R2);
		R2 = R.inverse();
		
		Pt3    t = new Pt3(0,0,50);
		
		Position pos0 = new Position(R, t);
				
		Stack<Pt2> seen = new Stack<Pt2>();
		for (Pt3 p : pts) { seen.push(p.apply(R).plus(t).apply(A).toPt2()); }
		
		pr("3D points: " + pts); 
		pr("Seen:      " + seen);
		pr("");
		pr("Actual pos (invert):"); pos0.R.print(5,5); pr(pos0.t); pr("");

		Position pos1 = new Position(seen, pts, A, null);
		
		pr("Calc.  pos (invert):"); pos1.R.print(5,5); pr(pos1.t); pr("");
		
	}
	
	private static void testRodrigues() {
		
		Matrix R = Matrix.identity(3,3);
		double th = 0.3; double c = Math.cos(th); double s = Math.sin(th);
		pr("th = " + th + "\n");
		R.set(1,1,c); R.set(2,2,c); R.set(2,1,s); R.set(1,2,-s);
		
		pr("R:");
		R.print(5,5);
		pr("RRt:");
		R.times(R.transpose()).print(5,5);
		R = Position.rodrigues(R);
		pr("Rodrigues:");
		R.print(5,5);
		pr("-> th = " + R.norm2() + "\n");
		R = Position.rodrigues(R);
		pr("R':");
		R.print(5,5);
		pr("R'R't:");
		R.times(R.transpose()).print(5,5);
						
		// Rodrigues works
	}
	
	public static void main(String[] args) {
//		rot();
		pnp();
		
	}
	
}
