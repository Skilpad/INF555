import java.awt.Point;
import java.util.LinkedList;
import java.util.Stack;

// INF555 2010. Frank Nielsen



// Let us fight Exception in thread "main" java.lang.StackOverflowError !!!

public class AreaFloodFilling
{
	static int nbrec=0;

	public static void flood(PPM img, int posx, int posy, Color background, Color fill)
	{
		recFlood(img, posx, posy, background, fill);
//		stackFlood(img, posx, posy, background, fill);
//	queueFlood(img, posx, posy, background, fill);
	}
	
	
	public static void recFlood(PPM img, int posx, int posy, Color background, Color fill)
	{
		if (fill.equals(background)) return;  // Avoid infinite loop
		nbrec++;

		if ((posx<0) || (posx>=img.width))  return;
		if ((posy<0) || (posy>=img.height)) return;	

		// System.out.println("x"+posx+" y"+posy);	

		Color pixel = img.getColor(posx, posy);	

		// **Response**
		if (pixel.equals(background)) {
			// Color current pixel
			img.setColor(posx, posy, fill);
			// Treat next pixels
			recFlood(img, posx+1, posy,   background, fill);
			recFlood(img, posx-1, posy,   background, fill);
			recFlood(img, posx,   posy+1, background, fill);
			recFlood(img, posx,   posy-1, background, fill);
		}
		
		// ************

	}	

	public static void stackFlood(PPM img, int posx, int posy, Color background, Color fill)
	{
		if (fill.equals(background)) return;  // Avoid infinite loop
		nbrec++;

		if ((posx<0) || (posx>=img.width))  return;
		if ((posy<0) || (posy>=img.height)) return;	

		// System.out.println("x"+posx+" y"+posy);	

		// **Response**

		Stack<Point> todo  = new Stack<Point>();
		todo.push(new Point(posx, posy));
		
		while (!todo.isEmpty()) {
			Point px = todo.pop();
			Color pixel = img.getColor(px.x, px.y);	
			if (pixel.equals(background)) {
				// Color current pixel
				img.setColor(px.x, px.y, fill);
				// Treat next pixels
				todo.push(new Point(px.x+1,px.y  ));
				todo.push(new Point(px.x-1,px.y  ));
				todo.push(new Point(px.x  ,px.y+1));
				todo.push(new Point(px.x  ,px.y-1));
			}
		}
		
		// ************

	}	

	public static void queueFlood(PPM img, int posx, int posy, Color background, Color fill)
	{
		if (fill.equals(background)) return;  // Avoid infinite loop
		nbrec++;

		if ((posx<0) || (posx>=img.width))  return;
		if ((posy<0) || (posy>=img.height)) return;	

		// System.out.println("x"+posx+" y"+posy);	

		// **Response**

		LinkedList<Point> todo  = new LinkedList<Point>();
		todo.add(new Point(posx, posy));
		
		while (!todo.isEmpty()) {
			Point px = todo.pop();
			Color pixel = img.getColor(px.x, px.y);	
			if (pixel.equals(background)) {
				// Color current pixel
				img.setColor(px.x, px.y, fill);
				// Treat next pixels
				todo.add(new Point(px.x+1,px.y  ));
				todo.add(new Point(px.x-1,px.y  ));
				todo.add(new Point(px.x  ,px.y+1));
				todo.add(new Point(px.x  ,px.y-1));
			}
		}
		
		// ************

	}	

	
	
/* ***** */
/* Tests */
/* ***** */	
	
	public static void main(String [] a)
	{
		PPM img = new PPM("forfloodfilling.ppm");
		int x = 212;
		int y = 132;
		Color fill       = new Color(255,0,0);     // red
		Color background = new Color(255,255,255); // white

		flood(img, x, y, background, fill);

		// **Personnal**
		img.show();
		// *************
		
		// img.save("result.ppm");
	}

}