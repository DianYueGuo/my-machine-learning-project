package machine_learning;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.json.JSONObject;

import game.tic_tac_toe_game.TicTacToeGame;
import game.tic_tac_toe_game.TicTacToePlayer;
import game.tic_tac_toe_game.TicTacToePlayer.Brain;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public class LearnToPlayTicTacToe extends EvolutionaryLearning {

	public LearnToPlayTicTacToe(double mutationRate, int numberOfVariantsToCreate, int selectionWidth,
			Brain[] parents) {
		super(mutationRate, numberOfVariantsToCreate, selectionWidth, a -> {
			if (Math.random() < mutationRate) {
				return Math.random() * 2 - 1;
			}
			return a;
		}, parents);
	}

	@Override
	protected DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException {
		final DeepNeuralNetwork[] variants = new DeepNeuralNetwork[numberOfVariantsToCreate];

		for (int i = 0; i < variants.length; i++) {
			if (i < selectionWidth) {
				variants[i] = parents[i].clone();
			} else {
				final int index = (int) Math.floor(Math.random() * selectionWidth);
				variants[i] = parents[index].clone().map(mutationFunction);
			}
		}

		return variants;
	}

	@Override
	protected DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {

		for (int i = variants.length; i > selectionWidth; i /= 2) {
			shuffleArray(0, i, variants);

			for (int j = 0; j < i / 2; j++) {
				final int index1 = j;
				final int index2 = i - 1 - j;

				TicTacToePlayer player1 = new TicTacToePlayer(variants[index1]);
				TicTacToePlayer player2 = new TicTacToePlayer(variants[index2]);

				TicTacToeGame game = new TicTacToeGame();

				switch (game.match(player1, player2)) {
				case PLAYER1_WIN: {
					break;
				}
				case PLAYER2_WIN: {
					swap(index1, index2, variants);
					break;
				}
				case DRAW: {
					if (Math.random() < 0.5) {
						swap(index1, index2, variants);
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + game.match(player1, player2));
				}
			}
		}

		return Arrays.copyOfRange(variants, 0, selectionWidth);
	}

	private void shuffleArray(int begin, int end, Object array[]) {
		for (int i = begin; i < end; i++) {
			int index = (int) Math.floor(Math.random() * (end - begin - i));

			swap(i, i + index, array);
		}
	}

	private void swap(int a, int b, Object array[]) {
		Object temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	private static void test(Brain brain)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		System.out.println("test robot player: ");

		TicTacToePlayer botPlayer = new TicTacToePlayer(brain);
		TicTacToePlayer manPlayer = new TicTacToePlayer(null);

		TicTacToeGame game = new TicTacToeGame();

		System.out.println("botPlayer play first: ");
		System.out.println("result: " + game.match(botPlayer, manPlayer));

		game = new TicTacToeGame();

		System.out.println("manPlayer play first: ");
		System.out.println("result: " + game.match(manPlayer, botPlayer));
	}

	public static void main(String[] args) throws Exception {

		if (args.length >= 7 && args[0].equals("train")) {

			String name = args[1];

			final int selectionWidth = Integer.parseInt(args[args.length - 2]);

			Brain brains[] = new Brain[selectionWidth];
			for (int i = 0; i < selectionWidth; i++) {
				JSONObject brainJO = new JSONObject(Files.readString(Path.of(args[2 + i])));
				Brain brain = new TicTacToePlayer.Brain(brainJO);
				brains[i] = brain;
			}

			final double learningRate = Double.parseDouble(args[args.length - 4]);
			final int variationWidth = Integer.parseInt(args[args.length - 3]);
			final int updateTimes = Integer.parseInt(args[args.length - 1]);

			LearnToPlayTicTacToe learning = new LearnToPlayTicTacToe(learningRate, variationWidth, selectionWidth,
					brains);

			long previousTime = System.currentTimeMillis();
			long currentTime = previousTime;
			for (int i = 1; i <= updateTimes; i++) {

				// update
				learning.update();

				// print estimated time
				currentTime = System.currentTimeMillis();
				System.out.println("update " + i + " (progress: "
						+ Math.round(((double) i / updateTimes * 100) * 10) / 10.0 + "%, "
						+ Math.round(((currentTime - previousTime) * (updateTimes - i) / 60000.0) * 10) / 10.0
						+ " minutes left)");
				previousTime = currentTime;

				// make filename
				String filenames[] = new String[selectionWidth];
				for (int j = 0; j < selectionWidth; j++) {
					filenames[j] = name + "_" + (j + 1) + "." + i + ".json";
				}

				// save file
				for (int j = 0; j < selectionWidth; j++) {
					FileWriter fw = new FileWriter(filenames[j]);
					fw.write(learning.getParents()[j].toJSONString());
					fw.close();
				}

				// print save message
				System.out.println("the brains are saved as " + Arrays.toString(filenames));

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

			String filename = name + ".json";

			FileWriter fw = new FileWriter(filename);
			fw.write(brain.toJSONString());
			fw.close();

			System.out.println("new brain \"" + filename + "\" is created!");

		} else {
			System.out.println("Syntax error");
			System.out.println("The syntax should be: ");
			System.out.println(
					"\"train <name> <brain1> <brain2>...<brainN> <learningRate> <variationWidth> <selectionWidth> <updateTimes>\" (selectionWidth should be equivalent to the number of <brain> and be less than or equal to variationWidth)");
			System.out.println("or");
			System.out.println("\"test <name>\"");
			System.out.println("or");
			System.out.println("\"create <name> <d1> <d2> <d3>...\" ('d' represents the depth of a hidden layer)");
		}

	}

}
