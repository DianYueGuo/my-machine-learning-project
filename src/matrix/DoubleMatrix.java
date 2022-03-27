package matrix;

class DoubleCalculator implements Calculator {

	public Object multiply(Object A, Object B) throws NumberMultiplicationException {
		if (A instanceof Double && B instanceof Double) {
//			System.out.println("multiply");
			return (Double) A * (Double) B;
		} else {
			throw new NumberMultiplicationException();
		}
	}

	public Object add(Object A, Object B) throws NumberAdditionException {
//		System.out.println("A: " + A.getClass().getName() + ", B: " + B.getClass().getName());
		if (A instanceof Double && B instanceof Double) {
			return (Double) A + (Double) B;
		} else {
			throw new NumberAdditionException();
		}
	}
}

public class DoubleMatrix extends Matrix {

	private DoubleMatrix(Matrix matrix) {
		super(matrix.getNumberOfRows(), matrix.getNumberOfColumns());
		super.values = matrix.values;
	}

	public DoubleMatrix(int numberOfRows, int numberOfColumns) {
		super(numberOfRows, numberOfColumns);

		// TODO Auto-generated constructor stub
	}

	public DoubleMatrix(Double values[][], int numberOfRows, int numberOfColumns) {
		super(values, numberOfRows, numberOfColumns);
	}

	public void set(int rowIndex, int columnIndex, double value) {
		super.set(rowIndex, columnIndex, value);
	}

	public Double get(int rowIndex, int columnIndex) {
		return (Double) super.get(rowIndex, columnIndex);
	}

	public static DoubleMatrix multiply(Matrix A, Matrix B) throws MatrixMultiplicationException,
			NumberMultiplicationException, NumberAdditionException, InterruptedException {
		return new DoubleMatrix(multiply(A, B, new DoubleCalculator()));
	}

}
