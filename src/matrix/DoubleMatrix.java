package matrix;

class DoubleCalculator implements Calculator {
	
	public Object multiply(Object A, Object B) throws NumberMultiplicationException {
		if(A instanceof Double && B instanceof Double) {
//			System.out.println("multiply");
			return (Double)A * (Double)B;
		} else {
			throw new NumberMultiplicationException();
		}
	}
	
	public Object add(Object A, Object B) throws NumberAdditionException {
//		System.out.println("A: " + A.getClass().getName() + ", B: " + B.getClass().getName());
		if(A instanceof Double && B instanceof Double) {
			return (Double)A + (Double)B;
		} else {
			throw new NumberAdditionException();
		}
	}
}

public class DoubleMatrix extends Matrix {
	
	public DoubleMatrix(Matrix matrix) {
		this(matrix.getNumberOfRows(), matrix.getNumberOfColumns());
		
		final int numberOfRows = matrix.getNumberOfRows();
		final int numberOfColumns = matrix.getNumberOfColumns();
		
		for(int i = 0; i < numberOfRows; i++) {
			for(int j = 0; j < numberOfColumns; j++) {
				this.set(i, j, matrix.get(i, j));
			}
		}
	}

	public DoubleMatrix(int numberOfRows, int numberOfColumns) {
		super(numberOfRows, numberOfColumns);
		// TODO Auto-generated constructor stub
	}
	
	public DoubleMatrix(Double values[][], int numberOfRows, int numberOfColumns) {
		super(values, numberOfRows, numberOfColumns);
	}
	
	public void set(int rowIndex, int columnIndex, double value) {
		super.values[rowIndex][columnIndex] = value;
	}
	
	public Double get(int rowIndex, int columnIndex) {
		return (Double) super.values[rowIndex][columnIndex];
	}
	
	public static DoubleMatrix multiply(Matrix A, Matrix B) throws MatrixMultiplicationException, NumberMultiplicationException, NumberAdditionException {
		return new DoubleMatrix(multiply(A, B, new DoubleCalculator()));
	}
	
}
