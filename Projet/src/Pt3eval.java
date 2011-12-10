import java.util.Stack;

import Jama.Matrix;


public class Pt3eval {

	public Pt3     p;
	public boolean known;

	public Stack<Drt3> drt;
	private Matrix      A;
	private Pt3         Ms;
	
	public Pt3eval() {
		this.p = null; 
		this.drt = new Stack<Drt3>();
		this.A = new Matrix(3,3); this.Ms = new Pt3();
		this.known = false;
	}
	
	public Pt3eval(Stack<Drt3> drt) {
		this.known = false;
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
	
	public Pt3eval(Pt3 p) {
		this.p = p;
		this.drt = new Stack<Drt3>();		
		this.A = null; this.Ms = null;
		this.known = true;
	}
	
	public void add(Pt2 imgP, Position pos) {
		Drt3 d = new Drt3(imgP,pos);
		drt.add(d);
		if (known) return;
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
	
	public int nbDrt() {
		return drt.size();
	}
	
}
