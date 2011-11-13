import java.util.Stack;

import Jama.Matrix;


public class Pt3eval {

	public Pt3         p;

	private Stack<Drt3> drt;
	private Matrix A;
	private Pt3 Ms;
	
	public Pt3eval() {
		this.p = new Pt3(); this.drt = new Stack<Drt3>();
	}
	
	public void add(Pt2 imgP, Matrix R, Pt3 t) {
		Drt3 d = new Drt3(imgP,R,t);
		drt.add(d);
		Matrix a = new Matrix(3,3);
		a.set(0,0, d.v.y*d.v.y + d.v.z*d.v.z);   a.set(0,1, -d.v.x*d.v.y);                a.set(0,2, -d.v.x*d.v.z);
		a.set(1,0, a.get(0,1));                  a.set(1,1, d.v.x*d.v.x + d.v.z*d.v.z);   a.set(1,2, -d.v.y*d.v.z);
		a.set(2,0, a.get(0,2));                  a.set(2,1, a.get(1,2));                  a.set(2,2, d.v.x*d.v.x + d.v.y*d.v.y);
		A  = A.plus(a);
		Ms.add(d.M.apply(a));
		p  = Ms.apply(A.inverse());
	}
	
}
