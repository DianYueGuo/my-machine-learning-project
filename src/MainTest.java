import matrix.DoubleMatrix;
import matrix.MatrixMultiplicationException;
import matrix.NumberAdditionException;
import matrix.NumberMultiplicationException;

public class MainTest {

	public static void main(String args[]) throws MatrixMultiplicationException, NumberMultiplicationException, NumberAdditionException {
		Double values[][] = new Double[1000][1000];
		for(int i = 0; i < 1000; i++) {
			Double row[] = new Double[1000];
			for(int j = 0; j < 1000; j++) {
				row[j] = i + j / 1000.0;
			}
			values[i] = row;
		}
		
		Double values2[][] = new Double[1000][1000];
		for(int i = 0; i < 1000; i++) {
			Double row[] = new Double[1000];
			for(int j = 0; j < 1000; j++) {
				row[j] = i + j / 1000.0;
			}
			values2[i] = row;
		}
		
		DoubleMatrix A = new DoubleMatrix(values, 1000, 1000);
		System.out.println("A = \n" + A);
		DoubleMatrix B = new DoubleMatrix(values2, 1000, 1000);
		System.out.println("B = \n" + B);
		
		double startTime = System.currentTimeMillis();
		DoubleMatrix C = DoubleMatrix.multiply(A, B);
		double endTime = System.currentTimeMillis();
		System.out.println("C = AB = \n" + C);
		System.out.println("time spent: " + (endTime - startTime) / 1000 + " s");
		
	}

}
