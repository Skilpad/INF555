import java.awt.Point;
import java.util.Stack;

import Jama.Matrix;

import processing.core.PImage;


public class Plan {

	public Stack<Pt3> corners3d;
	public Stack<Pt2> corners2d;
	public PImage     img;
	
	
	public Plan(Stack<Pt3> corners3d, PImage img, Stack<Pt2> ptsInImg) {
		corners2d = new Stack<Pt2>();
		corners2d.push(new Pt2(0,0)); corners2d.push(new Pt2(210*3-1,0)); corners2d.push(new Pt2(210*3-1,297*3-1)); corners2d.push(new Pt2(0,297*3-1));
		this.corners3d = corners3d;
		this.img = Homographie.invert(Homographie.find(corners2d, ptsInImg), img, new Point(210*3,297*3));
	}
	
	
	public PImage toPlot(Position pos, Matrix A, Point dstDimensions) {
		Stack <Pt2> resCorners = new Stack<Pt2>();
		for (Pt3 c : corners3d) resCorners.add(c.toPt2(pos.R, pos.t, A));
		return Homographie.invert(Homographie.find(resCorners, corners2d), img, dstDimensions);
	}
	
}
