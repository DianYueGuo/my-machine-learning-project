package matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Matrix {

	private final int numberOfRows;
	private final int numberOfColumns;
	protected Object values[][];

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

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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

	public static Matrix add(Matrix A, Matrix B, Calculator calculator) throws MatrixAdditionException, InterruptedException {
		final int A_numberOfRows = A.getNumberOfRows();
		final int A_numberOfColumns = A.getNumberOfColumns();
		final int B_numberOfRows = B.getNumberOfRows();
		final int B_numberOfColumns = B.getNumberOfColumns();
		if(A_numberOfRows != B_numberOfRows || A_numberOfColumns != B_numberOfColumns) {
			throw new MatrixAdditionException();
		}
		
		Matrix matrix = new Matrix(A_numberOfRows, A_numberOfColumns);
		
		class DoAddition implements Runnable {
			private final int i;
			private final int j;

			DoAddition(int i, int j) {
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				try {
					Object result;
					result = calculator.add(A.get(i, j), B.get(i, j));
					matrix.set(i, j, result);
				} catch (NumberAdditionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < A_numberOfRows; i++) {
			for (int j = 0; j < B_numberOfColumns; j++) {
				DoAddition doAddition = new DoAddition(i, j);
				executorService.execute(doAddition);
			}
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
		}
		
		return matrix;
	}

	public void map(Function<Object, Object> function) throws InterruptedException {
		class DoFunction implements Runnable {
			private final Matrix matrix;
			private final int i;
			private final int j;

			DoFunction(Matrix matrix, int i, int j) {
				this.matrix = matrix;
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final Object result = function.apply(this.matrix.get(i, j));
				this.matrix.set(this.i, this.j, result);
			}
		}

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < this.numberOfRows; i++) {
			for (int j = 0; j < this.numberOfColumns; j++) {
				DoFunction dofunction = new DoFunction(this, i, j);
				executorService.execute(dofunction);
			}
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
		}

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
