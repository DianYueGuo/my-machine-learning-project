package matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.json.JSONObject;
import org.json.JSONString;

public class Matrix<T> implements JSONString {

	public static interface Calculator<T> {

		T multiply(T a, T b);

		T add(T a, T b);

	}

	public static final Calculator<Double> DOUBLE_CALCULATOR = new Calculator<Double>() {

		@Override
		public Double multiply(Double a, Double b) {
			return a * b;
		}

		@Override
		public Double add(Double a, Double b) {
			return a + b;
		}
	};

	private int numberOfRows;
	private int numberOfColumns;
	private T values[][];
	private Calculator<T> calculator;

	@SuppressWarnings("unchecked")
	public Matrix(int numberOfRows, int numberOfColumns, Calculator<T> calculator) {
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		this.values = (T[][]) new Object[numberOfRows][numberOfColumns];
		this.calculator = calculator;
	}

	public Matrix(int numberOfRows, int numberOfColumns, BiFunction<Integer, Integer, T> initializationFunction,
			Calculator<T> calculator) throws InterruptedException {
		this(numberOfRows, numberOfColumns, calculator);

		class DoFunction implements Runnable {
			private final int i;

			DoFunction(Matrix<T> thisMatrix, int i) {
				this.i = i;
			}

			@Override
			public void run() {
				for (int j = 0; j < numberOfColumns; j++) {
					final T result = initializationFunction.apply(i, j);
					Matrix.this.set(this.i, j, result);
				}
			}
		}

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < this.numberOfRows; i++) {
			DoFunction dofunction = new DoFunction(this, i);
			executorService.execute(dofunction);
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
		}
	}

	public Matrix(T values[][], int numberOfRows, int numberOfColumns, Calculator<T> calculator)
			throws InterruptedException {
		this(numberOfRows, numberOfColumns, new BiFunction<Integer, Integer, T>() {

			@Override
			public T apply(Integer i, Integer j) {
				return values[i][j];
			}

		}, calculator);
	}

//	@SuppressWarnings("unchecked")
//	public Matrix(JSONObject jo) throws Exception {
//		this.numberOfRows = jo.getInt("numberOfRows");
//		this.numberOfColumns = jo.getInt("numberOfColumns");
//
//		this.values = (T[][]) new Object[numberOfRows][numberOfColumns];
//		for (int i = 0; i < numberOfRows; i++) {
//			for (int j = 0; j < numberOfColumns; j++) {
//				values[i][j] = 
//				// I don't know how to do
//			}
//		}
//
//		if (jo.getString("calculator").equals("DOUBLE_CALCULATOR")) {
//			this.calculator = (Calculator<T>) DOUBLE_CALCULATOR;
//		} else {
//			throw new Exception();
//		}
//	}

	public void set(int rowIndex, int columnIndex, T value) {
		this.values[rowIndex][columnIndex] = value;
	}

	private void set(Matrix<T> matrix) {
		this.numberOfRows = matrix.numberOfRows;
		this.numberOfColumns = matrix.numberOfColumns;
		this.values = matrix.values;
		this.calculator = matrix.calculator;
	}

	public T get(int rowIndex, int columnIndex) {
		return this.values[rowIndex][columnIndex];
	}

	public int getNumberOfRows() {
		return this.numberOfRows;
	}

	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	public Matrix<T> multiply(Matrix<T> b) throws MatrixMultiplicationException, InterruptedException {
		final Matrix<T> a = this;

		if (a.getNumberOfColumns() != b.getNumberOfRows()) {
			System.out.println(a);
			System.out.println(b);
			throw new MatrixMultiplicationException();
		}

		final int length = a.getNumberOfColumns();
		final int numberOfRows = a.getNumberOfRows();
		final int numberOfColumns = b.getNumberOfColumns();

		Matrix<T> matrix = new Matrix<T>(numberOfRows, numberOfColumns, calculator);

		class DoDotProduct implements Runnable {
			private final int i;
			private final int j;

			DoDotProduct(int i, int j) {
				this.i = i;
				this.j = j;
			}

			public void run() {
				for (int k = 0; k < length; k++) {
					final T aT = a.get(i, k);
					final T bT = b.get(k, j);

					T cT = calculator.multiply(aT, bT);

					if (k > 0) {
						cT = calculator.add(matrix.get(i, j), cT);
					}

					matrix.set(i, j, cT);
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

	public Matrix<T> add(Matrix<T> b) throws MatrixAdditionException, InterruptedException {
		final Matrix<T> a = this;

		final int a_numberOfRows = a.getNumberOfRows();
		final int a_numberOfColumns = a.getNumberOfColumns();
		final int b_numberOfRows = b.getNumberOfRows();
		final int b_numberOfColumns = b.getNumberOfColumns();

		if (a_numberOfRows != b_numberOfRows || a_numberOfColumns != b_numberOfColumns) {
			throw new MatrixAdditionException();
		}

		Matrix<T> matrix = new Matrix<T>(a_numberOfRows, a_numberOfColumns, calculator);

		class DoAddition implements Runnable {
			private final int i;
			private final int j;

			DoAddition(int i, int j) {
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				T result;
				result = calculator.add(a.get(i, j), b.get(i, j));
				matrix.set(i, j, result);
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

		this.set(matrix);

		return this;
	}

	public Matrix<T> map(Function<T, T> function) throws InterruptedException {
		class DoFunction implements Runnable {
			private final int i;
			private final int j;

			DoFunction(int i, int j) {
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final T result = function.apply(Matrix.this.get(i, j));
				Matrix.this.set(this.i, this.j, result);
			}
		}

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < this.numberOfRows; i++) {
			for (int j = 0; j < this.numberOfColumns; j++) {
				DoFunction dofunction = new DoFunction(i, j);
				executorService.execute(dofunction);
			}
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
		}

		return this;
	}

	public Matrix<T> filter(Matrix<T> filterMatrix, BiFunction<T, T, T> biFunction) throws InterruptedException {
		class DoFilter implements Runnable {
			private final int i;
			private final int j;

			public DoFilter(Matrix<T> thisMatrix, int i, int j) {
				this.i = i;
				this.j = j;
			}

			@Override
			public void run() {
				final T result = biFunction.apply(Matrix.this.get(i, j), filterMatrix.get(i, j));
				Matrix.this.set(i, i, result);
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

		return this;
	}

	@Override
	public Matrix<T> clone() {
		Matrix<T> newMatrix = null;

		try {
			newMatrix = new Matrix<T>(numberOfRows, numberOfColumns, new BiFunction<Integer, Integer, T>() {

				@Override
				public T apply(Integer t, Integer u) {
					return Matrix.this.get(t, u);
				}

			}, calculator);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newMatrix;
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
//		return toJSONString();
	}

	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();

		obj.put("numberOfRows", this.numberOfRows);
		obj.put("numberOfColumns", this.numberOfColumns);
		obj.put("values", this.values);

		if (this.calculator == DOUBLE_CALCULATOR) {
			obj.put("calculator", "DOUBLE_CALCULATOR");
		}

		return obj.toString();
	}

}
