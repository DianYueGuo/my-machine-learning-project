package matrix;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Matrix {

	private final int numberOfRows;
	private final int numberOfColumns;
	protected final Object values[][];

	public Matrix(int numberOfRows, int numberOfColumns) {
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		this.values = new Object[numberOfRows][numberOfColumns];
	}

	public Matrix(Object values[][], int numberOfRows, int numberOfColumns) {
		this(numberOfRows, numberOfColumns);

		for (int i = 0; i < numberOfRows; i++) {
			for (int j = 0; j < numberOfColumns; j++) {
				this.set(i, j, values[i][j]);
			}
		}
	}

	public void set(int rowIndex, int columnIndex, Object value) {
		this.values[rowIndex][columnIndex] = value;
	}

	public Object get(int rowIndex, int columnIndex) {
		return this.values[rowIndex][columnIndex];
	}

	public int getNumberOfRows() {
		return this.numberOfRows;
	}

	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	public static Matrix multiply(Matrix A, Matrix B, Calculator calculator) throws MatrixMultiplicationException,
			NumberMultiplicationException, NumberAdditionException, InterruptedException {
		if (A.getNumberOfColumns() != B.getNumberOfRows())
			throw new MatrixMultiplicationException();

		final int length = A.getNumberOfColumns();
		final int numberOfRows = A.getNumberOfRows();
		final int numberOfColumns = B.getNumberOfColumns();

		Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

		class DoDotProduct implements Runnable {
			private final int i;
			private final int j;

			DoDotProduct(int i, int j) {
				this.i = i;
				this.j = j;
			}

			public void run() {
				for (int k = 0; k < length; k++) {
//					System.out.println("i: " + i);
					Object a = A.get(i, k);
					Object b = B.get(k, j);

					try {
						Object c = calculator.multiply(a, b);
//					System.out.println(c);
						if (k == 0) {
							matrix.set(i, j, c);
						} else {
							c = calculator.add(matrix.get(i, j), c);
						}

						matrix.set(i, j, c);
					} catch (NumberMultiplicationException | NumberAdditionException e) {
					}
				}
			}
		}

		ExecutorService executorService = Executors.newFixedThreadPool(16);
		for (int i = 0; i < numberOfRows; i++) {
			for (int j = 0; j < numberOfColumns; j++) {
				DoDotProduct doDotProduct = new DoDotProduct(i, j);
				executorService.execute(doDotProduct);
			}
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
		}

		return matrix;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("[");
		for (int i = 0; i < numberOfRows; i++) {
			if (i > 0)
				str.append(" ");
			for (int j = 0; j < numberOfColumns; j++) {
				if (j == 0)
					str.append("[");

				str.append(this.get(i, j));

				if (j < numberOfColumns - 1)
					str.append(", ");
				else if (j == numberOfColumns - 1)
					str.append("]");
			}
			if (i < numberOfRows - 1)
				str.append(",\n");
		}
		str.append("]");

		return str.toString();
	}

}
