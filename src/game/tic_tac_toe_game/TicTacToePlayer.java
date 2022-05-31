package game.tic_tac_toe_game;

import java.util.ArrayList;
import java.util.Scanner;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public class TicTacToePlayer {

	public static class Brain extends DeepNeuralNetwork {

		public Brain() throws InterruptedException {
			super(DeepNeuralNetwork.Initializar.RANDOM, DeepNeuralNetwork.ActivationFunction.SIGMOID,
					DeepNeuralNetwork.ActivationFunction.SIGMOID, new int[] { 9, 9, 9, 9 });
		}

		private Brain(Initializar initializar, ActivationFunction activationFunction,
				ActivationFunction outputLayerActivationFunction, int[] widths) throws InterruptedException {
			super(initializar, activationFunction, outputLayerActivationFunction, widths);
		}

		@Override
		public Brain clone() {
			Brain returnValue = null;
			try {
				returnValue = new Brain(initializar, activationFunction, outputLayerActivationFunction, widths);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return returnValue;
		}

	}

	private DeepNeuralNetwork brain;
	private boolean isManualInput;

	public TicTacToePlayer(DeepNeuralNetwork brain, boolean isManualInput) {
		this.brain = brain;
		this.isManualInput = isManualInput;
	}

	public TicTacToePlayer(DeepNeuralNetwork brain) {
		this(brain, false);
	}

	void play(TicTacToeGame game) throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		int markIndex_i = 0, markIndex_j = 0;

		if (isManualInput) {
			System.out.println(game.getBoard());

			Scanner scanner = new Scanner(System.in);

			markIndex_i = scanner.nextInt();
			markIndex_j = scanner.nextInt();
		} else {
			Matrix<Double> inputValue = new Matrix<Double>(9, 1, Matrix.DOUBLE_CALCULATOR);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					double inuptNumber = 0.0;

					switch (game.getSpaceState(i, j)) {
					case EMPTY:
						inuptNumber = 0.0;
						break;
					case MARKED_O:
						inuptNumber = -1.0;
						break;
					case MARKED_X:
						inuptNumber = 1.0;
						break;
					default:
						break;
					}

					inputValue.set(i * 3 + j, 0, inuptNumber);
				}
			}

			Matrix<Double> result = brain.getOutput(inputValue);

			double maxNumber = 0;
			ArrayList<Integer> maxNumberIndexs = new ArrayList<Integer>();

			for (int i = 0; i < 9; i++) {
				if (game.isLegalToMark(i / 3, i % 3)) {
					if (result.get(i, 0) > maxNumber) {
						maxNumber = result.get(i, 0);
						maxNumberIndexs = new ArrayList<Integer>();
						maxNumberIndexs.add(i);
					} else if (result.get(i, 0) == maxNumber) {
						maxNumberIndexs.add(i);
					}
				}
			}

			int maxNumberIndex = maxNumberIndexs.get((int) (maxNumberIndexs.size() * Math.random()));

			markIndex_i = maxNumberIndex / 3;
			markIndex_j = maxNumberIndex % 3;
		}

		game.mark(markIndex_i, markIndex_j);
	}

}
