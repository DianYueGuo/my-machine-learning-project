package matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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

	public Matrix(int numberOfRows, int numberOfColumns, BiFunction<Integer, Integer, Object> biFunction) throws InterruptedException {
		this(numberOfRows, numberOfColumns);
		
		class DoFunction implements Runnable {
			private final Matrix thisMatrix;
			private final int i;
			private final int j;

			DoFunction(Matrix thisMatrix, int i, int j) {
				this.thisMatrix = thisMatrix;
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final Object result = biFunction.apply(i, j);
				this.thisMatrix.set(this.i, this.j, result);
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
	
	public Matrix(Object values[][], int numberOfRows, int numberOfColumns) throws InterruptedException {
		this(numberOfRows, numberOfColumns, new BiFunction<Integer, Integer, Object>() {

			@Override
			public Object apply(Integer i, Integer j) {
				return values[i][j];
			}
			
		});
	}

	public static Matrix multiply(Matrix a, Matrix b, Calculator calculator) throws MatrixMultiplicationException,
			NumberMultiplicationException, NumberAdditionException, InterruptedException {
		if (a.getNumberOfColumns() != b.getNumberOfRows())
			throw new MatrixMultiplicationException();

		final int length = a.getNumberOfColumns();
		final int numberOfRows = a.getNumberOfRows();
		final int numberOfColumns = b.getNumberOfColumns();

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
					try {
						final Object aObject = a.get(i, k);
						final Object bObject = b.get(k, j);
						
						Object cObject = calculator.multiply(aObject, bObject);
						
						if (k > 0) {
							cObject = calculator.add(matrix.get(i, j), cObject);
						}

						matrix.set(i, j, cObject);
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

	public static Matrix add(Matrix a, Matrix b, Calculator calculator)
			throws MatrixAdditionException, InterruptedException {
		final int a_numberOfRows = a.getNumberOfRows();
		final int a_numberOfColumns = a.getNumberOfColumns();
		final int b_numberOfRows = b.getNumberOfRows();
		final int b_numberOfColumns = b.getNumberOfColumns();
		if (a_numberOfRows != b_numberOfRows || a_numberOfColumns != b_numberOfColumns) {
			throw new MatrixAdditionException();
		}

		Matrix matrix = new Matrix(a_numberOfRows, a_numberOfColumns);

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
					result = calculator.add(a.get(i, j), b.get(i, j));
					matrix.set(i, j, result);
				} catch (NumberAdditionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < a_numberOfRows; i++) {
			for (int j = 0; j < b_numberOfColumns; j++) {
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

	public void map(Function<Object, Object> function) throws InterruptedException {
		class DoFunction implements Runnable {
			private final Matrix thisMatrix;
			private final int i;
			private final int j;

			DoFunction(Matrix thisMatrix, int i, int j) {
				this.thisMatrix = thisMatrix;
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final Object result = function.apply(this.thisMatrix.get(i, j));
				this.thisMatrix.set(this.i, this.j, result);
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

	public void filter(Matrix filterMatrix, BiFunction<Object, Object, Object> biFunction) throws InterruptedException {
		class DoFilter implements Runnable {
			private final Matrix thisMatrix;
			private final int i;
			private final int j;
						
			public DoFilter(Matrix thisMatrix, int i, int j) {
				this.thisMatrix = thisMatrix;
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final Object result = biFunction.apply(this.thisMatrix.get(i, j), filterMatrix.get(i, j));
				this.thisMatrix.set(i, i, result);
			}
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < this.numberOfRows; i++) {
			for (int j = 0; j < this.numberOfColumns; j++) {
				DoFilter doFilter = new DoFilter(this, i, j);
				executorService.execute(doFilter);
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
