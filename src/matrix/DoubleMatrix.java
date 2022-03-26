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

	private final boolean isWrapping;
	private final Matrix wrappedMatrix;

	public DoubleMatrix(Matrix matrix) {
		super(matrix.getNumberOfRows(), matrix.getNumberOfColumns());
		isWrapping = true;
		wrappedMatrix = matrix;
	}

	public DoubleMatrix(int numberOfRows, int numberOfColumns) {
		super(numberOfRows, numberOfColumns);
		isWrapping = false;
		wrappedMatrix = null;

		// TODO Auto-generated constructor stub
	}

	public DoubleMatrix(Double values[][], int numberOfRows, int numberOfColumns) {
		super(values, numberOfRows, numberOfColumns);
		isWrapping = false;
		wrappedMatrix = null;
	}

	public void set(int rowIndex, int columnIndex, double value) {
		if (isWrapping) {
			wrappedMatrix.set(rowIndex, columnIndex, value);
		} else {
			super.values[rowIndex][columnIndex] = value;
		}
	}

	public Double get(int rowIndex, int columnIndex) {
		if (isWrapping) {
			return (Double) wrappedMatrix.get(rowIndex, columnIndex);
		} else {
			return (Double) super.values[rowIndex][columnIndex];
		}
	}

	public static DoubleMatrix multiply(Matrix A, Matrix B) throws MatrixMultiplicationException,
			NumberMultiplicationException, NumberAdditionException, InterruptedException {
		return new DoubleMatrix(multiply(A, B, new DoubleCalculator()));
	}

}
