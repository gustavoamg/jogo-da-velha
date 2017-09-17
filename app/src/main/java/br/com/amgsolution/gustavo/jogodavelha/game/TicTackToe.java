package br.com.amgsolution.gustavo.jogodavelha.game;

import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by gustavoamg on 13/09/17.
 */

public class TicTackToe {
    public static final int LINE_1 = 1;
    public static final int LINE_2 = 2;
    public static final int LINE_3 = 3;
    public static final int COLUMN_1 = 4;
    public static final int COLUMN_2 = 5;
    public static final int COLUMN_3 = 6;
    public static final int DIAGONAL_1 = 7;
    public static final int DIAGONAL_2 = 8;
    public static final int VELHA = 9;

    public enum Mode {
        SINGLE_PLAYER,
        TWO_PLAYERS;
    }

    private PositionState[][] gridMatrix = new PositionState[3][3];
    private Mode gameMode = Mode.SINGLE_PLAYER;

    private Player playerTurn, playerSimbol;

    private int circleScore;
    private int crossScore;

    private Bot bot;

    public TicTackToe() {
        this(0, 0);
    }

    public TicTackToe(int circleScore, int crossScore) {
        this.circleScore = circleScore;
        this.crossScore = crossScore;
        this.playerSimbol = Player.CIRCLE;
        this.playerTurn = Player.CIRCLE;
        this.bot = new Bot();
        init();
    }

    private void init() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                gridMatrix[i][j] = PositionState.EMPTY;
        }
    }

    public void startNewGame(Player playerSimbol) {
        this.playerSimbol = playerSimbol;
        this.playerTurn = playerSimbol;
        init();
    }

    public int performTurnActions(int positionX, int positionY) {
        if (selectPosition(positionX, positionY)) {
            int result = checkResult(gridMatrix);
            if (result != 0) {
                if (result != 9) {
                    if (playerTurn == Player.CIRCLE)
                        circleScore++;
                    else
                        crossScore++;
                }
                return result;
            }

            switchPlayers();
        }

        return 0;

    }

    private boolean selectPosition(int positionX, int positionY) {
        if (positionX >= 3 || positionY >= 3 || positionX < 0 || positionY < 0)
            return false;

        if (gridMatrix[positionY][positionX] != PositionState.EMPTY)
            return false;

        if (playerTurn == Player.CIRCLE)
            gridMatrix[positionY][positionX] = PositionState.CIRCLE;
        else
            gridMatrix[positionY][positionX] = PositionState.CROSS;

        return true;
    }

    private void switchPlayers() {
        if (this.playerTurn == Player.CIRCLE) {
            this.playerTurn = Player.CROSS;
            this.playerSimbol = Player.CROSS;

        } else {
            this.playerTurn = Player.CIRCLE;
            this.playerSimbol = Player.CIRCLE;
        }

    }

    private int checkResult(PositionState[][] matrixState) {

        if (checkLine(0, matrixState))
            return LINE_1;
        if (checkLine(1, matrixState))
            return LINE_2;
        if (checkLine(2, matrixState))
            return LINE_3;
        if (checkColumn(0, matrixState))
            return COLUMN_1;
        if (checkColumn(1, matrixState))
            return COLUMN_2;
        if (checkColumn(2, matrixState))
            return COLUMN_3;

        int diagonals = checkDiagonal(matrixState);

        if (diagonals != 0)
            return 6 + diagonals; //7 ou 8

        if (checkVelha(matrixState))
            return VELHA;

        return 0;

    }

    private boolean checkLine(int line, PositionState[][] matrixState) {
        PositionState currentPosition = matrixState[0][line];
        for (int i = 0; i < 3; i++) {
            if (currentPosition == PositionState.EMPTY || matrixState[i][line] != currentPosition) {
                return false;
            }
            currentPosition = matrixState[i][line];
        }
        return true;
    }

    private boolean checkColumn(int column, PositionState[][] matrixState ) {
        PositionState currentPosition = matrixState[column][0];
        for (int i = 0; i < 3; i++) {
            if (currentPosition == PositionState.EMPTY || matrixState[column][i] != currentPosition) {
                return false;
            }
            currentPosition = matrixState[column][i];
        }
        return true;
    }

    private int checkDiagonal(PositionState[][] matrixState) {

        PositionState currentPosition = matrixState[0][0];

        int i;
        for (i = 0; i < 3; i++) {
            if (currentPosition == PositionState.EMPTY || matrixState[i][i] != currentPosition)
                break;
            currentPosition = matrixState[i][i];
        }

        if (i == 3)
            return 1;

        currentPosition = matrixState[2][0];

        int j = 2;
        for (i = 0; i < 3 && j >= 0; i++) {
            if (currentPosition == PositionState.EMPTY || matrixState[j][i] != currentPosition)
                break;
            currentPosition = matrixState[j][i];
            j--;
        }

        if (j < 0)
            return 2;

        return 0;
    }

    private boolean checkVelha(PositionState[][] matrixState) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (matrixState[i][j] == PositionState.EMPTY)
                    return false;
        return true;
    }

    public PositionState[][] getGrid() {
        final PositionState[][] currentGrid = this.gridMatrix;
        return currentGrid;
    }

    public Player getPlayerTurn() {
        return playerTurn;
    }

    public int getCircleScore() {
        return circleScore;
    }

    public int getCrossScore() {
        return crossScore;
    }

    public Mode getGameMode() {
        return gameMode;
    }

    public int makeComputerMove() {
        int move = bot.makeNextMove(new WeakReference<>(playerTurn));
        return performTurnActions(bot.getCoodX(move), bot.getCoordY(move));
    }

    class Bot {

        private WeakReference<Player> playerWeakReference;


        int makeNextMove(WeakReference<Player> playerWeakReference) {
            this.playerWeakReference = playerWeakReference;

            return minmax(this.playerWeakReference.get());
        }

        int minmax(Player player) {
            int move = 1;
            int widraw = -1;
            for (int i = 1; i < 10; i++){
                PositionState[][] currentState = resetState(TicTackToe.this.gridMatrix);

                logState(currentState);

                int result = 0;

                if(TicTackToe.this.gridMatrix[getCoordY(i)][getCoodX(i)] == PositionState.EMPTY) {
                    if (player == Player.CIRCLE) {
                        currentState[getCoordY(i)][getCoodX(i)] = PositionState.CIRCLE;
                        result = valorMin(currentState , Player.CIRCLE);
                    } else {
                        currentState[getCoordY(i)][getCoodX(i)] = PositionState.CROSS;
                        result = valorMax(currentState , Player.CROSS);
                    }
                    move = i;

                    if(result > 0)
                        return move;

                    if(result == 0 &&  widraw < 0)
                        widraw = move;
                }
            }

            if(widraw > 0)
                return widraw;

            return move;
        }

        PositionState[][] resetState(final PositionState[][] originalState) {
            PositionState[][] currentState = new PositionState[3][3];
            for (int i = 0; i < 3; i ++)
                for (int j = 0; j < 3; j++)
                    currentState[i][j] =  originalState[i][j];

            return currentState;
        }

        int valorMax(final PositionState[][] state, Player player) {
            logState(state);
            int result = checkResult(state);
            if(player == Player.CIRCLE) {
                if (result != 0) {
                    return checkUtility(result, Player.CIRCLE);
                }
            }
            else {
                if (result != 0) {
                    return checkUtility(result, Player.CROSS);
                }
            }

            int v = 9;
            PositionState[][] nextState = sucessor(state, player);

            //while (nextState != null) {
                if(playerWeakReference.get() == Player.CIRCLE) {
                     v = valorMin(nextState, Player.CROSS);
                }
                else {
                    v = valorMin(nextState, Player.CIRCLE);
                }
            //}

            return v;

        }

        int valorMin(final PositionState[][] state, Player player) {
            logState(state);
            int result = checkResult(state);

            if(player == Player.CIRCLE) {
                if (result != 0) {
                    return checkUtility(result, Player.CIRCLE);
                }
            }
            else {
                if (result != 0) {
                    return checkUtility(result, Player.CROSS);
                }
            }

            int v = 1;
            PositionState[][] nextState = sucessor(state, player);
            //while (nextState != null) {
                if(playerWeakReference.get() == Player.CIRCLE) {
                    v = valorMax(nextState, Player.CIRCLE);
                    //nextState = sucessor(nextState, Player.CIRCLE);
                }
                else {
                    v = valorMax(nextState, Player.CROSS);
                    //nextState = sucessor(nextState, Player.CROSS);
                }
            //}

            return v;

        }

        int checkUtility(int result, Player player) {
            if (result == 9) {
                Log.i ("__ FINAL STATE __", "----------------------------> DRAW");
                return 0;
            }
            if (result != 0 && player == TicTackToe.this.playerTurn) {
                Log.i ("__ FINAL STATE __", "----------------------------> VICTORY");
                return 1;
            }
            Log.i ("__ FINAL STATE __", "----------------------------> DEFEAT");
            return -1;
        }

        PositionState[][] sucessor(final PositionState[][] state, Player player) {
            PositionState[][] nextState = resetState(state);

            for (int i = 1; i < 10; i++) {
                if (state[getCoordY(i)][getCoodX(i)] == PositionState.EMPTY) {
                        if (player == Player.CIRCLE) {
                            nextState[getCoordY(i)][getCoodX(i)] = PositionState.CROSS;
                        } else {
                            nextState[getCoordY(i)][getCoodX(i)] = PositionState.CIRCLE;
                        }

                    return nextState;
                }
            }

            return null;
        }

        int getCoodX(int square) {
            return (square - 1) % 3;
        }

        int getCoordY(int square) {
            return (square -1) / 3;
        }




    }

    private void logState(PositionState[][] state) {
        Log.d("__BOARD STATE__", "########################");
        Log.d("__BOARD STATE__", String.format(" %1$s | %2$s | %3$s ", state[0][0], state[0][1], state[0][2]));
        Log.d("__BOARD STATE__", " ---------- ");
        Log.d("__BOARD STATE__", String.format(" %1$s | %2$s | %3$s ", state[1][0], state[1][1], state[1][2]));
        Log.d("__BOARD STATE__", " ---------- ");
        Log.d("__BOARD STATE__", String.format(" %1$s | %2$s | %3$s ", state[2][0], state[2][1], state[2][2]));
        Log.d("__BOARD STATE__", "########################");
    }
}
