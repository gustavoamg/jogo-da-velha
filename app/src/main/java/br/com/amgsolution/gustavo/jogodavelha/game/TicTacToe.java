package br.com.amgsolution.gustavo.jogodavelha.game;

/**
 * Created by gustavoamg on 13/09/17.
 */

public class TicTacToe {
    public static final int LINE_1 = 1;
    public static final int LINE_2 = 2;
    public static final int LINE_3 = 3;
    public static final int COLUMN_1 = 4;
    public static final int COLUMN_2 = 5;
    public static final int COLUMN_3 = 6;
    public static final int DIAGONAL_1 = 7;
    public static final int DIAGONAL_2 = 8;
    public static final int VELHA = 9;

    private PositionState[][] gridMatrix = new PositionState[3][3];

    private Player playerTurn, playerSimbol;

    private int circleScore;
    private int crossScore;

    public TicTacToe() {
        this(0,0);
    }

    public TicTacToe(int circleScore, int crossScore) {
        this.circleScore = circleScore;
        this.crossScore = crossScore;
        this.playerSimbol = Player.CIRCLE;
        this.playerTurn = Player.CIRCLE;
        init();
    }

    private void init() {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++)
                gridMatrix[i][j] = PositionState.EMPTY;
        }
    }

    public void startNewGame(Player playerSimbol){
        this.playerSimbol = playerSimbol;
        this.playerTurn = playerSimbol;
        init();
    }

    public int performTurnActions(int positionX, int positionY) {
        if(selectPosition(positionX, positionY)){
            int result = checkResult();
            if(result != 0) {
                if(result != 9) {
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
        if(positionX > 3 || positionY > 3 || positionX <= 0 || positionY <= 0)
            return false;

        positionX--;
        positionY--;

        if(gridMatrix[positionY][positionX] != PositionState.EMPTY)
            return false;

        if(playerTurn == Player.CIRCLE)
            gridMatrix[positionY][positionX] = PositionState.CIRCLE;
        else
            gridMatrix[positionY][positionX] = PositionState.CROSS;

        return true;
    }

    private void switchPlayers() {
        if(this.playerTurn == Player.CIRCLE) {
            this.playerTurn = Player.CROSS;
            this.playerSimbol = Player.CROSS;
        }
        else {
            this.playerTurn = Player.CIRCLE;
            this.playerSimbol = Player.CIRCLE;
        }
    }

    private int checkResult() {

        if(checkLine(0))
            return LINE_1;
        if(checkLine(1))
            return LINE_2;
        if(checkLine(2))
            return LINE_3;
        if(checkColumn(0))
            return COLUMN_1;
        if(checkColumn(1))
            return COLUMN_2;
        if(checkColumn(2))
            return COLUMN_3;

        int diagonals = checkDiagonal();

        if(diagonals != 0)
            return 6 + diagonals; //7 ou 8

        if(checkVelha())
            return VELHA;

        return 0;

    }

    private boolean checkLine(int line){
        for(int i = 0; i < 3; i++) {
            if (gridMatrix[i][line].player != playerTurn)
                return false;
        }
        return true;
    }

    private boolean checkColumn(int column){
        for(int i = 0; i < 3; i++) {
            if (gridMatrix[column][i].player != playerTurn)
                return false;
        }
        return true;
    }

    private int checkDiagonal() {

        int i = 0;
        for(; i < 3; i++) {
            if (gridMatrix[i][i].player != playerTurn)
                break;
        }

        if(i == 3)
            return 1;

        int j = 2;
        for(i = 0; i < 3; i++) {
            for(; j >= 0; j--)
                if (gridMatrix[j][i].player != playerTurn)
                    break;
        }

        if(j == 0)
            return -1;

        return 0;
    }

    private boolean checkVelha(){
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(gridMatrix[i][j] == PositionState.EMPTY)
                    return false;
        return true;
    }

    public PositionState[][] getGrid(){
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
}
