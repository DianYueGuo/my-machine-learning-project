import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import matrix.NumberAdditionException;
import matrix.NumberMultiplicationException;

public class MainTest {

	public static void main(String args[]) throws MatrixMultiplicationException, NumberMultiplicationException,
			NumberAdditionException, InterruptedException, MatrixAdditionException {

		Double values[][] = new Double[1000][1000];
		for (int i = 0; i < 1000; i++) {
			Double row[] = new Double[1000];
			for (int j = 0; j < 1000; j++) {
				row[j] = i + j / 1000.0;
			}
			values[i] = row;
		}

		Matrix<Double> a = new Matrix<>(values, 10, 10, Matrix.DOUBLE_CALCULATOR);
		System.out.println("a = \n" + a);

		double startTime = System.currentTimeMillis();
		Matrix<Double> b = a.multiply(a);
		double endTime = System.currentTimeMillis();
		System.out.println("b = a^2 = \n" + b);
		System.out.println("time spent: " + (endTime - startTime) / 1000 + " s");

		startTime = System.currentTimeMillis();
		Matrix<Double> c = a.add(a);
		endTime = System.currentTimeMillis();
		System.out.println("c = a + a = \n" + c);
		System.out.println("time spent: " + (endTime - startTime) / 1000 + " s");
	}

}
