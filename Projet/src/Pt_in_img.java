import processing.core.PImage;


public class Pt_in_img {

	public Pt2   pt;
	public Image img;
	
	public Pt_in_img(Pt2 pt, Image img) {
		this.pt = new Pt2(pt); this.img = img;
	}
	
	public void changePt(Pt2 pt) {
		this.pt.x = pt.x; this.pt.y = pt.y;
	}
	
}
