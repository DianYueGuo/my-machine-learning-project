import java.util.Scanner;

import game.TicTacToeGame;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.Network;

public class MainTest {

	public static void main(String args[])
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
//		TicTacToeGame game = new TicTacToeGame();
//		Scanner scanner = new Scanner(System.in);
//
//		while (game.getGameState() == TicTacToeGame.GAME_STATE.PLAYER1_TURN
//				|| game.getGameState() == TicTacToeGame.GAME_STATE.PLAYER2_TURN) {
//			
//			System.out.println(game.getGameState());
//			
//			final int i = scanner.nextInt();
//			final int j = scanner.nextInt();
//			
//			game.mark(i, j);
//		}
//		
//		System.out.println(game.getGameState());
		
		Network network = new Network(Network.INITIALIZAR., null, null)
	}

}
