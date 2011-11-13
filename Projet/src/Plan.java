import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;

import processing.core.PImage;


public class Plan {

	Stack<Pt3> corners3d;
	Stack<Pt2> corners2d;
	PImage     img;
	
	PImage toPlot(Matrix R, Pt3 t, Matrix A, Point dstDimensions) {
		Stack <Pt2> resCorners = new Stack<Pt2>();
		for (Pt3 c : corners3d) resCorners.add(c.toPt2(R, t, A));
		return Homographie.invert(Homographie.find(resCorners, corners2d), img, dstDimensions);
	}
	
}
