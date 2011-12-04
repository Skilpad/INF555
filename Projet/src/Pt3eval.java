import java.util.Stack;

import Jama.Matrix;


public class Pt3eval {

	public Pt3     p;

	private Stack<Drt3> drt;
	private Matrix A;
	private Pt3 Ms;
	
	public Pt3eval() {
		this.p = null; 
		this.drt = new Stack<Drt3>();
		this.A = new Matrix(3,3); this.Ms = new Pt3();
	}
	
	public Pt3eval(Stack<Drt3> drt) {
		this.drt = new Stack<Drt3>();
		this.A = new Matrix(3,3); this.Ms = new Pt3();
		for (Drt3 d : drt) { 
			this.drt.push(d); 
			Matrix a = new Matrix(3,3);
			a.set(0,0, d.v.y*d.v.y + d.v.z*d.v.z);   a.set(0,1, -d.v.x*d.v.y);                a.set(0,2, -d.v.x*d.v.z);
			a.set(1,0, a.get(0,1));                  a.set(1,1, d.v.x*d.v.x + d.v.z*d.v.z);   a.set(1,2, -d.v.y*d.v.z);
			a.set(2,0, a.get(0,2));                  a.set(2,1, a.get(1,2));                  a.set(2,2, d.v.x*d.v.x + d.v.y*d.v.y);
			A  = A.plus(a);
			Ms.add(d.M.apply(a));
		}
		this.p = (A.det() == 0) ? null : Ms.apply(A.inverse());		
	}
	
	public void add(Pt2 imgP, Position pos) {
		Drt3 d = new Drt3(imgP,pos);
		drt.add(d);
		Matrix a = new Matrix(3,3);
		a.set(0,0, d.v.y*d.v.y + d.v.z*d.v.z);   a.set(0,1, -d.v.x*d.v.y);                a.set(0,2, -d.v.x*d.v.z);
		a.set(1,0, a.get(0,1));                  a.set(1,1, d.v.x*d.v.x + d.v.z*d.v.z);   a.set(1,2, -d.v.y*d.v.z);
		a.set(2,0, a.get(0,2));                  a.set(2,1, a.get(1,2));                  a.set(2,2, d.v.x*d.v.x + d.v.y*d.v.y);
		A  = A.plus(a);
		Ms.add(d.M.apply(a));
		p = (A.det() == 0) ? null : Ms.apply(A.inverse());
	}
	
	
	public String toString() {
		return (drt + " >>  " + p);
	}
	
	public int nbDrt() { return drt.size(); }
	
}
