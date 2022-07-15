package game.tic_tac_toe_game;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class TicTacToeGame {

	public static enum GameState {
		PLAYER1_TURN, PLAYER2_TURN, PLAYER1_WIN, PLAYER2_WIN, DRAW
	}

	static enum SpaceState {
		EMPTY, MARKED_X, MARKED_O
	}

	private final Matrix<SpaceState> board;
	private GameState gameState;
	private int numberOfMarks;

	public TicTacToeGame() throws InterruptedException {
		this.board = new Matrix<SpaceState>(3, 3, ((a, b) -> SpaceState.EMPTY), null);
		this.gameState = GameState.PLAYER1_TURN;
		this.numberOfMarks = 0;
	}
	
	public TicTacToeGame(TicTacToeGame ticTacToeGame) {
		this.board = ticTacToeGame.board.clone();
		this.gameState = ticTacToeGame.gameState;
		this.numberOfMarks = ticTacToeGame.numberOfMarks;
	}
	
	public TicTacToeGame tryMark(int i, int j) {
		TicTacToeGame game = this.clone();
		game.mark(i, j);
		return game;
	}

	public int getNumberOfMarks() {
		return numberOfMarks;
	}

	public Matrix<SpaceState> getBoard() {
		return board.clone();
	}

	public SpaceState getSpaceState(int r, int c) {
		return board.get(r, c);
	}

	public boolean isLegalToMark(int i, int j) {
		if (gameState != GameState.PLAYER1_TURN && gameState != GameState.PLAYER2_TURN) { // 檢查遊戲是否已結束，若已結束則跳出
			return false;
		}

		if (i < 0 || 2 < i || j < 0 || 2 < j || board.get(i, j) != SpaceState.EMPTY) { // 如果超出範圍或畫到以畫過的格子則輸
			return false;
		}

		return true;
	}

	public void mark(int i, int j) {
		numberOfMarks++;
		
		if (gameState != GameState.PLAYER1_TURN && gameState != GameState.PLAYER2_TURN) { // 檢查遊戲是否已結束，若已結束則跳出
			return;
		}

		if (i < 0 || 2 < i || j < 0 || 2 < j || board.get(i, j) != SpaceState.EMPTY) { // 如果超出範圍或畫到以畫過的格子則輸
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
	}

	public GameState match(TicTacToePlayer player1, TicTacToePlayer player2)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		while (true) {
			switch (getGameState()) {
			case PLAYER1_TURN:
				player1.play(this);
				break;
			case PLAYER2_TURN:
				player2.play(this);
				break;
			default:
				return getGameState();
			}
		}
	}
	
	@Override
	public TicTacToeGame clone() {
		TicTacToeGame game;
		game = new TicTacToeGame(this);
		
		return game;
	}

	private GameState getGameState() {
		return gameState;
	}
}
