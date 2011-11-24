import java.util.Stack;


public class Miniball {
	
	public Pt     C;
	public double r;
	
	public Miniball(Pt a) {
		C = a;
		r = 0;
	}
	
	public Miniball(Pt a, Pt b) {
		C = a.plus(b).times(0.5);
		r = a.dist(b)/2;
	}
	
	public Miniball(Pt a, Pt b, Pt c) {
		double d = 2 * (-a.y*b.x + a.x*b.y + a.y*c.x - b.y*c.x - a.x*c.y + b.x*c.y);
		C = new Pt(- (a.y*b.x*b.x - a.x*a.x*b.y - a.y*a.y*b.y + a.y*b.y*b.y - a.y*c.x*c.x + b.y*c.x*c.x + a.x*a.x*c.y + a.y*a.y*c.y - b.x*b.x*c.y - b.y*b.y*c.y - a.y*c.y*c.y + b.y*c.y*c.y)/d,
				  ( - a.x*a.x*b.x - a.y*a.y*b.x + a.x*b.x*b.x + a.x*b.y*b.y + a.x*a.x*c.x + a.y*a.y*c.x - b.x*b.x*c.x - b.y*b.y*c.x - a.x*c.x*c.x + b.x*c.x*c.x - a.x*c.y*c.y + b.x*c.y*c.y)/d);
		r = C.dist(a);
	}
	
	public Miniball(Stack<Pt> P) {
		Stack<Pt> P_ = new Stack<Pt>();
		for (Pt p : P) P_.push(p);
		miniball(P_, P_.size(), new Stack<Pt>(), 0, null); 
	}
	
	private void miniball(Stack<Pt> P, int Psize, Stack<Pt> B, int Bsize, Pt last_rm) {
		if (Bsize == 3) { solveBasis(B); if (last_rm != null) P.push(last_rm); return; }
		if (Psize <= 3) { solveBasis(P); if (last_rm != null) P.push(last_rm); return; }
		Pt p = P.remove((int) (Psize*Math.random()));
		miniball(P, Psize-1, B, Bsize, p);
		if (!contains(p)) { B.push(p); miniball(P, Psize-1, B, Bsize+1, p); }
		if (last_rm != null) P.push(last_rm); 
	}
	
	private void copy(Miniball m) {
		r = m.r; C = m.C;
	}
	
	private void solveBasis(Stack<Pt> B) {
		Stack<Pt> B_ = new Stack<Pt>(); for (Pt p : B) B_.push(p);
		if (B_.isEmpty()) { copy(new Miniball(new Pt(0,0))); } else {
		Pt a = B_.pop();
		if (B_.isEmpty()) { copy(new Miniball(a)); } else {
		Pt b = B_.pop();
		if (B_.isEmpty()) { copy(new Miniball(a,b)); } else {
		Pt c = B_.pop();
		copy(new Miniball(a,b,c));
		}}}
	}
	
	public boolean contains(Pt p) {
		return C.dist(p) <= r;
	}
	
}
