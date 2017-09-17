package br.com.amgsolution.gustavo.jogodavelha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import br.com.amgsolution.gustavo.jogodavelha.game.Player;
import br.com.amgsolution.gustavo.jogodavelha.game.PositionState;
import br.com.amgsolution.gustavo.jogodavelha.game.TicTackToe;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout grid;
    private TicTackToe gameEngine;

    private TextView circleScore;
    private TextView crossScore;

    private Button newGameBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        grid = (RelativeLayout) findViewById(R.id.grid);
        grid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("TOUCH EVENT", "Touch coordinates : " +
                            String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));

                    processTurn(v.getWidth(), v.getHeight(), event.getX(), event.getY());
                }
                return true;
            }
        });

        newGameBt = (Button) findViewById(R.id.new_game_button);
        newGameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameEngine.startNewGame(Player.CIRCLE);
                updateGridView();
            }
        });

        circleScore = (TextView) findViewById(R.id.circle_score_counter);
        crossScore = (TextView) findViewById(R.id.cross_score_counter);

        gameEngine = new TicTackToe();

    }

    private void processTurn(float width, float height, float touchX, float touchY) {
        //determina a linha do click
        int line = (int) (touchX / (width / 3));
        //determina a coluna do click
        int colum = (int) (touchY / (height / 3));

        int result = gameEngine.performTurnActions(line, colum);

        updateGridView();

        if(!checkEndOfGame(result)){
            if (gameEngine.getGameMode() == TicTackToe.Mode.SINGLE_PLAYER && gameEngine.getPlayerTurn() == Player.CROSS) {
                result = gameEngine.makeComputerMove();

                updateGridView();

                checkEndOfGame(result);
            }
        }
    }

    private boolean checkEndOfGame(int result) {
        if (result != 0) {
            //jogada final
            circleScore.setText(String.valueOf(gameEngine.getCircleScore()));
            crossScore.setText(String.valueOf(gameEngine.getCrossScore()));

            if (result != 9) {
                Toast.makeText(this, "Vencedor!", Toast.LENGTH_LONG).show();
                return true;
            } else
                Toast.makeText(this, "Deu Velha!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void updateGridView() {

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

        int positionWidth = grid.getWidth() / 3;
        PositionState[][] currentGrid = gameEngine.getGrid();

        grid.removeAllViews();

        ImageView imageBoard = new ImageView(this);
        RelativeLayout.LayoutParams lpGrid = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lpGrid.setMargins(0, 0, 0, 0);
        imageBoard.setLayoutParams(lpGrid);
        imageBoard.setImageResource(R.drawable.grid);
        grid.addView(imageBoard);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentGrid[i][j] != PositionState.EMPTY) {
                    ImageView image = new ImageView(this);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(px, px);
                    lp.setMargins(marginPx + (positionWidth * j), marginPx + (positionWidth * i), 0, 0);
                    image.setLayoutParams(lp);
                    if (currentGrid[i][j].player == Player.CIRCLE)
                        image.setImageResource(R.drawable.circle);
                    else
                        image.setImageResource(R.drawable.cross);

                    grid.addView(image);

                }
            }
        }
        grid.invalidate();
    }
}
