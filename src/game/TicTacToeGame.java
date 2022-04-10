package game;

import matrix.Matrix;

public class TicTacToeGame {

	public static enum GameState {
		PLAYER1_TURN, PLAYER2_TURN, PLAYER1_WIN, PLAYER2_WIN, DRAW
	}

	private static enum SpaceState {
		EMPTY, MARKED_X, MARKED_O
	}

	private final Matrix<SpaceState> board;
	private GameState gameState;

	public TicTacToeGame() throws InterruptedException {
		this.board = new Matrix<SpaceState>(3, 3, ((a, b) -> SpaceState.EMPTY), null);
		this.gameState = GameState.PLAYER1_TURN;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void mark(int i, int j) {
		if (gameState != GameState.PLAYER1_TURN && gameState != GameState.PLAYER2_TURN) { // 檢查遊戲是否已結束，若已結束則跳出
			return;
		}

		if (board.get(i, j) != SpaceState.EMPTY) { // 如果畫到以畫過的格子則輸
			if (gameState == GameState.PLAYER1_TURN) {
				gameState = GameState.PLAYER2_WIN;
			} else if (gameState == GameState.PLAYER2_TURN) {
				gameState = GameState.PLAYER1_WIN;
			}

			return;
		}

		if (gameState == GameState.PLAYER1_TURN) { // 畫記
			board.set(i, j, SpaceState.MARKED_X);
		} else if (gameState == GameState.PLAYER2_TURN) {
			board.set(i, j, SpaceState.MARKED_O);
		}

		// 判斷畫記結果
		boolean isLined = false;

		if (board.get(0, 0) != SpaceState.EMPTY && board.get(0, 0) == board.get(0, 1)
				&& board.get(0, 1) == board.get(0, 2)) {
			isLined = true;
		}
		if (board.get(1, 0) != SpaceState.EMPTY && board.get(1, 0) == board.get(1, 1)
				&& board.get(1, 1) == board.get(1, 2)) {
			isLined = true;
		}
		if (board.get(2, 0) != SpaceState.EMPTY && board.get(2, 0) == board.get(2, 1)
				&& board.get(2, 1) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 0) != SpaceState.EMPTY && board.get(0, 0) == board.get(1, 0)
				&& board.get(1, 0) == board.get(2, 0)) {
			isLined = true;
		}
		if (board.get(0, 1) != SpaceState.EMPTY && board.get(0, 1) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 1)) {
			isLined = true;
		}
		if (board.get(0, 2) != SpaceState.EMPTY && board.get(0, 2) == board.get(1, 2)
				&& board.get(1, 2) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 0) != SpaceState.EMPTY && board.get(0, 0) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 2) != SpaceState.EMPTY && board.get(0, 2) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 0)) {
			isLined = true;
		}

		if (isLined) {
			// 如果有形成一條線就贏
			if (gameState == GameState.PLAYER1_TURN) {
				gameState = GameState.PLAYER1_WIN;
			} else if (gameState == GameState.PLAYER2_TURN) {
				gameState = GameState.PLAYER2_WIN;
			}
		} else {
			if (board.get(0, 0) != SpaceState.EMPTY && board.get(0, 1) != SpaceState.EMPTY
					&& board.get(0, 2) != SpaceState.EMPTY && board.get(1, 0) != SpaceState.EMPTY
					&& board.get(1, 1) != SpaceState.EMPTY && board.get(1, 2) != SpaceState.EMPTY
					&& board.get(2, 0) != SpaceState.EMPTY && board.get(2, 1) != SpaceState.EMPTY
					&& board.get(2, 2) != SpaceState.EMPTY) {

				gameState = GameState.DRAW;

				return;
			}
			
			if (gameState == GameState.PLAYER1_TURN) {
				gameState = GameState.PLAYER2_TURN;
			} else if (gameState == GameState.PLAYER2_TURN) {
				gameState = GameState.PLAYER1_TURN;
			}
		}

		System.out.println(board);
	}
}
