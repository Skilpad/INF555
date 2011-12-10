import java.util.Stack;


public class Pt_corresp {

	public Pt3eval          pt;
	public Stack<Pt_in_img> pt2_list;
	
	private double threshold = 0.0000001;
	
	public Pt_corresp() {
		pt = new Pt3eval();
		pt2_list = new Stack<Pt_in_img>();
	}
	
	public Pt_corresp(Pt3 p) {
		this.pt = new Pt3eval(p);
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
//		if (pt.nbDrt() < pt2_list.size()) 
			this.update();
		if (pt.p == null) return;
		double d2 = Double.POSITIVE_INFINITY;
		int cnt = 0;
		while (d2 > threshold) {
			Pt3 p_ = pt.p;
			p.img.update();
			this.update();
			d2 = (pt == null || pt.p == null || p.img == null) ? 0 : pt.p.dist2(p_);
			if (cnt++ == 1000) break;
		}
	}
	
	public void delete_from_img(Image img) {
		Stack<Pt_in_img> new_list = new Stack<Pt_in_img>();
		for (Pt_in_img p : pt2_list)
			if (p.img == img)   img.forget_pt(this);
			else                new_list.push(p);
		pt2_list = new_list;
	}
	
	public void update() {
		if (pt.known) return;
		if (pt2_list.size() < 2) { pt = new Pt3eval(); return; }
		Stack<Drt3> drts = new Stack<Drt3>();
		for (Pt_in_img p : pt2_list) if (p.img.pos != null) drts.push(new Drt3(p.pt, p.img.pos, p.img.A));
		pt = new Pt3eval(drts);
	}
	
	public Pt_in_img pt_in_img(Image img) {
		for (Pt_in_img p : pt2_list) if (p.img == img) return p;
		return null;
	}
	
	public Pt2 pt2_in_img(Image img) {
		for (Pt_in_img p : pt2_list) if (p.img == img) return p.pt;
		return null;
	}
	
	public int nbPts() {
		return pt2_list.size();
	}
	
	public boolean isEmpty() {
		return pt2_list.isEmpty();
	}
	
	public boolean known() {
		return pt.known;
	}
	
}
