package br.com.amgsolution.gustavo.jogodavelha.game;

/**
 * Created by gustavoamg on 13/09/17.
 */

public enum PositionState {

    EMPTY(null),
    CIRCLE(Player.CIRCLE),
    CROSS(Player.CROSS);

    public Player player;

    PositionState(Player player) {
        this.player = player;
    }


}
