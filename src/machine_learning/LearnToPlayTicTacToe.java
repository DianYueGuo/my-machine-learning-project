package machine_learning;

import game.tic_tac_toe_game.TicTacToeGame;
import game.tic_tac_toe_game.TicTacToePlayer;
import game.tic_tac_toe_game.TicTacToePlayer.Brain;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public class LearnToPlayTicTacToe extends EvolutionaryLearning {

	public LearnToPlayTicTacToe(double mutationRate, int numberOfVariantsToCreate, TicTacToePlayer.Brain parent) {
		super(mutationRate, numberOfVariantsToCreate, a -> {
			if (Math.random() < mutationRate) {
				return Math.random() * 2 - 1;
			}
			return a;
		}, new TicTacToePlayer.Brain[] { parent });
	}

	public Brain getParent() {
		return (TicTacToePlayer.Brain) (super.getParents()[0]);
	}

	@Override
	protected DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException {
		final DeepNeuralNetwork[] variants = new DeepNeuralNetwork[numberOfVariantsToCreate];

		for (int i = 0; i < variants.length; i++) {
			if (i == 0) {
				variants[i] = parents[0].clone();
			} else {
				variants[i] = parents[0].clone().map(mutationFunction);
			}
		}

		return variants;
	}

	@Override
	protected DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		for (int i = variants.length; i > 1; i /= 2) {
			for (int j = 0; j < i / 2; j++) {
				final int index1 = j;
				final int index2 = i - 1 - j;

				if (Math.random() < 0.5) {
					DeepNeuralNetwork temp = variants[index1];
					variants[index1] = variants[index2];
					variants[index2] = temp;
				}

				TicTacToePlayer player1 = new TicTacToePlayer(variants[index1]);
				TicTacToePlayer player2 = new TicTacToePlayer(variants[index2]);

				TicTacToeGame game = new TicTacToeGame();

				switch (game.match(player1, player2)) {
				case PLAYER1_WIN: {
					break;
				}
				case PLAYER2_WIN: {
					variants[index1] = variants[index2];
					break;
				}
				case DRAW: {
					if (Math.random() < 0.5) {
						variants[index1] = variants[index2];
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + game.match(player1, player2));
				}
			}
		}

		return new DeepNeuralNetwork[] { variants[0] };
	}

	private static void test(Brain brain)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		System.out.println("test robot player: ");

		TicTacToePlayer botPlayer = new TicTacToePlayer(brain);
		TicTacToePlayer manPlayer = new TicTacToePlayer(null, true);

		TicTacToeGame game = new TicTacToeGame();

		System.out.println("botPlayer play first: ");
		System.out.println("result: " + game.match(botPlayer, manPlayer));

		game = new TicTacToeGame();

		System.out.println("manPlayer play first: ");
		System.out.println("result: " + game.match(manPlayer, botPlayer));
	}

	public static void main(String[] args)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		Brain brain = new TicTacToePlayer.Brain();
		LearnToPlayTicTacToe learning = new LearnToPlayTicTacToe(0.3, 128, brain);

		for (int i = 1; i <= 1; i++) {
			learning.update();
			System.out.println("update " + i);
			System.out.println(learning.getParent());
		}

		while (true) {
			test(learning.getParent());
		}
	}

}
