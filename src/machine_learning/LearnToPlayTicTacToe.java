package machine_learning;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

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

	public static void main(String[] args) throws Exception {
		if (args.length == 5 && args[0].equals("train")) {
			Brain brain;

			String name = args[1];
			double learningRate = Double.parseDouble(args[2]);
			int variationWidth = Integer.parseInt(args[3]);
			int updateTimes = Integer.parseInt(args[4]);

			JSONObject obj = new JSONObject(Files.readString(Path.of(name)));
			brain = new TicTacToePlayer.Brain(obj);

			LearnToPlayTicTacToe learning = new LearnToPlayTicTacToe(learningRate, (int) Math.pow(2, variationWidth),
					brain);

			for (int i = 1; i <= updateTimes; i++) {
				learning.update();
				System.out.println("update " + i);

				String filename;
				if (name.lastIndexOf('.') != -1) {
					filename = name.substring(0, name.lastIndexOf('.')) + "." + i + ".json";
				} else {
					filename = name + "." + i + ".json";
				}

				FileWriter fw = new FileWriter(filename);
				fw.write(learning.getParent().toJSONString());
				fw.close();

				System.out.println("the brain is saved as \"" + filename + "\"");
			}
		} else if (args.length == 2 && args[0].equals("test")) {
			test(new TicTacToePlayer.Brain(new JSONObject(Files.readString(Path.of(args[1])))));
		} else if (args.length >= 2 && args[0].equals("create")) {
			int[] hiddenLayerDepths = new int[args.length - 2];

			for (int i = 0; i < hiddenLayerDepths.length; i++) {
				hiddenLayerDepths[i] = Integer.valueOf(args[i + 2]);
			}

			Brain brain = new TicTacToePlayer.Brain(hiddenLayerDepths);
			String name = args[1];

			String filename;
			if (name.lastIndexOf('.') != -1) {
				filename = name.substring(0, name.lastIndexOf('.')) + ".json";
			} else {
				filename = name + ".json";
			}

			FileWriter fw = new FileWriter(filename);
			fw.write(brain.toJSONString());
			fw.close();

			System.out.println("new Brain \"" + filename + "\" is created!");
		} else {
			System.out.println("Syntax error");
			System.out.println("The syntax should be: ");
			System.out.println("\"train <name> <learningRate> <variationWidth> <updateTimes>\"");
			System.out.println("or");
			System.out.println("\"test <name>\"");
			System.out.println("or");
			System.out.println("\"create <name> <d1> <d2> <d3>... ('d' represents the depth of a hidden layer)\"");
		}
	}

}
