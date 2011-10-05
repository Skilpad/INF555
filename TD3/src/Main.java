
public class Main {

	public static void main(String[] args) {
    	
		PPM3 view = new PPM3(500, 500);
		Pt a = new Pt();
		Pt b = new Pt();
		Pt c = new Pt();
		Pt d = new Pt();
		Vect l1, l2;
		if (((b.y-a.y)*(d.x-a.x)-(b.x-a.x)*(d.y-a.y))*((b.y-a.y)*(c.x-a.x)-(b.x-a.x)*(c.y-a.y)) > 0) {
			// c et d sont du même côté de ab
	    	l1 = new Vect(a,d);
	    	l2 = new Vect(c,b);
		} else {
	    	l1 = new Vect(a,b);
	    	l2 = new Vect(c,d);
		}
		Pt i = l1.intersect(l2);
		view.plot(l1);
		view.plot(l2);
		
		System.out.println(l1.c);
		System.out.println(l2.c);
		
		view.plot(a);
		view.plot(b);
		view.plot(c);
		view.plot(d);
		view.plot(i);
		view.show();
    }

}
