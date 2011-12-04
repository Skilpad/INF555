import java.util.Stack;


public class Pt_corresp {

	private Pt3eval          pt;
	private Stack<Pt_in_img> pt2_list;
	
	private double threshold = 0.0000001;
	
	public Pt_corresp() {
		pt = null;
		pt2_list = new Stack<Pt_in_img>();
	}
	
	public Pt3 pt3() {
		if (pt == null) return null;
		return pt.p;
	}
	
	public void add(Pt_in_img p) {
		pt2_list.push(p);
		p.img.pts2.push(p.pt);
		p.img.pts3.push(this);
		if (pt.nbDrt() < pt2_list.size()) this.update();
		double d2 = Double.POSITIVE_INFINITY;
		while (d2 > threshold) {
			Pt3 p_ = pt.p;
			p.img.update();
			this.update();
			d2 = (pt == null || pt.p == null || p.img == null) ? 0 : pt.p.dist2(p_);
		}
	}
	
	public void update() {
		if (pt2_list.size() < 2) { pt = null; return; }
		Stack<Drt3> drts = new Stack<Drt3>();
		for (Pt_in_img p : pt2_list) if (p.img.pos != null) drts.push(new Drt3(p.pt, p.img.pos));
		pt = new Pt3eval(drts);
	}
	
}
