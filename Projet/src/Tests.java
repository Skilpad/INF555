import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;


public class Tests {

	private static void pr(Object s) { System.out.println(s); }
	
	private static void rot() {
		
		Point windowDim = new Point(1024,768);
		Matrix A = new Matrix(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);

		Stack<Pt3> pts = new Stack<Pt3>();
		pts.push(new Pt3(0,0,0));
		pts.push(new Pt3(1,0,0));
		pts.push(new Pt3(1,1,0));
		pts.push(new Pt3(0,1,0));
		
		Matrix R = Matrix.identity(3,3);
		Pt3    t = new Pt3(0,0,-1);
		Position pos0 = new Position(R, t);
				
		Stack<Pt2> seen = new Stack<Pt2>();
		for (Pt3 p : pts) { seen.push(p.apply(pos0.R).plus(pos0.t).apply(A).toPt2()); }
		
		pr("3D points: " + pts); 
		pr("Seen:      " + seen);
		
	}
	
	private static void pnp() {
		
		Point windowDim = new Point(1024,768);
		Matrix A = new Matrix(3,3);
		A.set(0,0, 750); A.set(1,1, 750); A.set(0,2, windowDim.x/2); A.set(1,2, windowDim.y/2);
		
		Stack<Pt3> pts = new Stack<Pt3>();
		pts.push(new Pt3( 0, 0,0));
		pts.push(new Pt3(15,16,0)); 
		pts.push(new Pt3(78,45,0)); 
		pts.push(new Pt3(80,33,0)); 
		pts.push(new Pt3(17,12,0)); 
		
		Matrix R = Matrix.identity(3,3);
		double th = 0.3; double c = Math.cos(th); double s = Math.sin(th);
		R.set(1,1,c); R.set(2,2,c); R.set(2,1,s); R.set(1,2,-s);
		Pt3    t = new Pt3(-50,-50,-50);
		
		Position pos0 = new Position(R, t);
		
		Stack<Pt2> seen = new Stack<Pt2>();
		for (Pt3 p : pts) { seen.push(p.apply(pos0.R).plus(pos0.t).apply(A).toPt2()); }
		
		Position pos1 = new Position(seen, pts, A, null);
		
		pr("3D points: " + pts); 
		pr("Seen:      " + seen);
		pr("");
		pr("Actual pos:"); pos0.R.print(5,5); pr(t); pr("");
		pr("Calc.  pos:"); pos1.R.print(5,5); pr(t); pr("");
		
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
