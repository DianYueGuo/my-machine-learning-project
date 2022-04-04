package matrix;

class DoubleCalculator implements Calculator {

	public Object multiply(Object a, Object b) throws NumberMultiplicationException {
		if (a instanceof Double && b instanceof Double) {
//			System.out.println("multiply");
			return (Double) a * (Double) b;
		} else {
			throw new NumberMultiplicationException();
		}
	}

	public Object add(Object a, Object b) throws NumberAdditionException {
//		System.out.println("A: " + A.getClass().getName() + ", B: " + B.getClass().getName());
		if (a instanceof Double && b instanceof Double) {
			return (Double) a + (Double) b;
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

	public DoubleMatrix(Double values[][], int numberOfRows, int numberOfColumns) throws InterruptedException {
		super(values, numberOfRows, numberOfColumns);
	}

	@Override
	public void set(int rowIndex, int columnIndex, Object value) {
		super.set(rowIndex, columnIndex, (Double) value);
	}

	@Override
	public Double get(int rowIndex, int columnIndex) {
		return (Double) super.get(rowIndex, columnIndex);
	}

	public static DoubleMatrix multiply(Matrix a, Matrix b) throws MatrixMultiplicationException,
			NumberMultiplicationException, NumberAdditionException, InterruptedException {
		return new DoubleMatrix(multiply(a, b, new DoubleCalculator()));
	}
	
	public static DoubleMatrix add(Matrix a, Matrix b) throws MatrixAdditionException, InterruptedException {
		return new DoubleMatrix(add(a, b, new DoubleCalculator()));
	}

}
