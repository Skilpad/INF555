import processing.core.PApplet;


public class Constants {

	public static int s          = 5;
//	public static int destHeight = 128;
//	public static int destWidth  = 128;
	public static int destHeight = 300;
	public static int destWidth  = 300;
	public static int n0         = 25;
	
	public static PApplet pApplet;
	
	public static float inf2() {   // > dist² maximal pour les représentants
		return 7*s*(s+1);
	}
	
	public static float inf() {
		return (float) Math.sqrt(7*s*(s+1));
	}
	
	public void tst() {
		class fi {
			
		}
		
	}
}
