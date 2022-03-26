import matrix.DoubleMatrix;
import matrix.MatrixMultiplicationException;
import matrix.NumberAdditionException;
import matrix.NumberMultiplicationException;

public class MainTest {

	public static void main(String args[]) throws MatrixMultiplicationException, NumberMultiplicationException, NumberAdditionException, InterruptedException {
		Double values[][] = new Double[1000][1000];
		for(int i = 0; i < 1000; i++) {
			Double row[] = new Double[1000];
			for(int j = 0; j < 1000; j++) {
				row[j] = i + j / 1000.0;
			}
			values[i] = row;
		}
		
		DoubleMatrix A = new DoubleMatrix(values, 10, 10);
//		System.out.println("A = \n" + A);
		
		double startTime = System.currentTimeMillis();
		DoubleMatrix C = DoubleMatrix.multiply(A, A);
		double endTime = System.currentTimeMillis();
		System.out.println("C = AB = \n" + C);
		System.out.println("time spent: " + (endTime - startTime) / 1000 + " s");
		
	}

}
