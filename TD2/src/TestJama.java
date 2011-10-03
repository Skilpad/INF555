import Jama.Matrix;

class TestJama
{
public static void main(String args[])
{

	  //double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}}; 
      //Matrix A = new Matrix(array); 
/*      Matrix A = Matrix.random(3,3);
      Matrix b = Matrix.random(3,1); 
      Matrix x = A.solve(b); 
      Matrix Residual = A.times(x).minus(b); 
      double rnorm = Residual.normInf(); 
      
      System.out.println("A:");
      A.print(3,2);
      
      System.out.println("b:");
      b.print(3,2);
      System.out.println("Solution of Ax=b, x:");
      x.print(3,2);
      System.out.println("Residual Ax-b:");
      Residual.print(3,2);
      System.out.println("Norm of residual:"+rnorm); */
      
//	Matrix A = Matrix.random(3,5);
//    
//	System.out.println("A: ");
//	A.print(3,2);
//	System.out.println("A[1,2]:  " + A.get(1,2));
//    System.out.println("Columns: " + A.getColumnDimension());
//    System.out.println("Row:     " + A.getRowDimension());
//      
//    (new Matrix(3,2)).print(3,2);
	
	double[][] da = {{1,2},{3,4}};
	double[][] db = {{2,2},{0,-1}};
	Matrix A = new Matrix(da);
	Matrix B = new Matrix(db);
	A.print(3,2);
	B.print(3,2);
	A.plus(B).print(3,2);
	A.plusEquals(B);
	A.print(3,2);
	
	
	
}

}
