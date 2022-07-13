package game.tic_tac_toe_game;

import java.util.Scanner;
import java.util.function.Supplier;

import org.json.JSONObject;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public class TicTacToePlayer {

	public static class Brain extends DeepNeuralNetwork {

		public Brain(int[] hidden_layer_depths, String name) throws InterruptedException {
			super(DeepNeuralNetwork.Initializar.ZERO, DeepNeuralNetwork.ActivationFunction.SIGMOID,
					DeepNeuralNetwork.ActivationFunction.SIGMOID, (new Supplier<int[]>() {

						@Override
						public int[] get() {
							int[] layer_depths = new int[hidden_layer_depths.length + 2];

							layer_depths[0] = 11;
							layer_depths[layer_depths.length - 1] = 1;

							for (int i = 0; i < hidden_layer_depths.length; i++) {
								layer_depths[i + 1] = hidden_layer_depths[i];
							}

							return layer_depths;
						}

					}).get(), name);
		}

		public Brain(JSONObject obj) throws Exception {
			super(obj);
		}

		private Brain(Initializar initializar, ActivationFunction activationFunction,
				ActivationFunction outputLayerActivationFunction, int[] widths, String name)
				throws InterruptedException {
			super(initializar, activationFunction, outputLayerActivationFunction, widths, name);
		}

	}

	private DeepNeuralNetwork brain;

	public TicTacToePlayer(DeepNeuralNetwork brain) {
		this.brain = brain;
	}

	public void play(TicTacToeGame game)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		int markIndex_i = 0, markIndex_j = 0;

		if (brain == null) {
			System.out.println(game.getBoard());

			Scanner scanner = new Scanner(System.in);

			markIndex_i = scanner.nextInt();
			markIndex_j = scanner.nextInt();
		} else {
			double maxPossibility = 0;
			markIndex_i = 0;
			markIndex_j = 0;
			
			for (int k = 0; k < 3; k++) {
				for (int l = 0; l < 3; l++) {
					
					Matrix<Double> inputValue = new Matrix<Double>(11, 1, Matrix.DOUBLE_CALCULATOR);
					
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
					
					inputValue.set(9, 0, (double) k);
					inputValue.set(10, 0, (double) l);

					Matrix<Double> result = brain.getOutput(inputValue);
					
					if(result.get(0, 0) > maxPossibility) {
						maxPossibility = result.get(0, 0);
						
						markIndex_i = k;
						markIndex_j = l;
					}
				}
			}
		}

		game.mark(markIndex_i, markIndex_j);
	}

}
