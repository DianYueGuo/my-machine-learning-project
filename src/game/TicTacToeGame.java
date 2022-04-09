package game;

import matrix.Matrix;

public class TicTacToeGame {

	public static enum GAME_STATE {
		PLAYER1_TURN, PLAYER2_TURN, PLAYER1_WIN, PLAYER2_WIN, DRAW
	}

	private static enum SPACE_STATE {
		EMPITY, MARKED_X, MARKED_O
	}

	private final Matrix<SPACE_STATE> board;
	private GAME_STATE gameState;

	public TicTacToeGame() throws InterruptedException {
		this.board = new Matrix<SPACE_STATE>(3, 3, ((a, b) -> SPACE_STATE.EMPITY), null);
		this.gameState = GAME_STATE.PLAYER1_TURN;
	}

	public GAME_STATE getGameState() {
		return gameState;
	}

	public void mark(int i, int j) {
		if (gameState != GAME_STATE.PLAYER1_TURN && gameState != GAME_STATE.PLAYER2_TURN) { // 檢查遊戲是否已結束，若已結束則跳出
			return;
		}

		if (board.get(i, j) != SPACE_STATE.EMPITY) { // 如果畫到以畫過的格子則輸
			if (gameState == GAME_STATE.PLAYER1_TURN) {
				gameState = GAME_STATE.PLAYER2_WIN;
			} else if (gameState == GAME_STATE.PLAYER2_TURN) {
				gameState = GAME_STATE.PLAYER1_WIN;
			}

			return;
		}

		if (gameState == GAME_STATE.PLAYER1_TURN) { // 畫記
			board.set(i, j, SPACE_STATE.MARKED_X);
		} else if (gameState == GAME_STATE.PLAYER2_TURN) {
			board.set(i, j, SPACE_STATE.MARKED_O);
		}

		// 判斷畫記結果
		boolean isLined = false;

		if (board.get(0, 0) != SPACE_STATE.EMPITY && board.get(0, 0) == board.get(0, 1)
				&& board.get(0, 1) == board.get(0, 2)) {
			isLined = true;
		}
		if (board.get(1, 0) != SPACE_STATE.EMPITY && board.get(1, 0) == board.get(1, 1)
				&& board.get(1, 1) == board.get(1, 2)) {
			isLined = true;
		}
		if (board.get(2, 0) != SPACE_STATE.EMPITY && board.get(2, 0) == board.get(2, 1)
				&& board.get(2, 1) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 0) != SPACE_STATE.EMPITY && board.get(0, 0) == board.get(1, 0)
				&& board.get(1, 0) == board.get(2, 0)) {
			isLined = true;
		}
		if (board.get(0, 1) != SPACE_STATE.EMPITY && board.get(0, 1) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 1)) {
			isLined = true;
		}
		if (board.get(0, 2) != SPACE_STATE.EMPITY && board.get(0, 2) == board.get(1, 2)
				&& board.get(1, 2) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 0) != SPACE_STATE.EMPITY && board.get(0, 0) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 2)) {
			isLined = true;
		}
		if (board.get(0, 2) != SPACE_STATE.EMPITY && board.get(0, 2) == board.get(1, 1)
				&& board.get(1, 1) == board.get(2, 0)) {
			isLined = true;
		}

		if (isLined) {
			// 如果有形成一條線就贏
			if (gameState == GAME_STATE.PLAYER1_TURN) {
				gameState = GAME_STATE.PLAYER1_WIN;
			} else if (gameState == GAME_STATE.PLAYER2_TURN) {
				gameState = GAME_STATE.PLAYER2_WIN;
			}
		} else {
			if (board.get(0, 0) != SPACE_STATE.EMPITY && board.get(0, 1) != SPACE_STATE.EMPITY
					&& board.get(0, 2) != SPACE_STATE.EMPITY && board.get(1, 0) != SPACE_STATE.EMPITY
					&& board.get(1, 1) != SPACE_STATE.EMPITY && board.get(1, 2) != SPACE_STATE.EMPITY
					&& board.get(2, 0) != SPACE_STATE.EMPITY && board.get(2, 1) != SPACE_STATE.EMPITY
					&& board.get(2, 2) != SPACE_STATE.EMPITY) {

				gameState = GAME_STATE.DRAW;

				return;
			}
			
			if (gameState == GAME_STATE.PLAYER1_TURN) {
				gameState = GAME_STATE.PLAYER2_TURN;
			} else if (gameState == GAME_STATE.PLAYER2_TURN) {
				gameState = GAME_STATE.PLAYER1_TURN;
			}
		}

		System.out.println(board);
	}
}
