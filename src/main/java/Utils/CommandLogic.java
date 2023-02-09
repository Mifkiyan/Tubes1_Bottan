package Utils;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;

public interface CommandLogic {
    public void execute(PlayerAction playerAction, GameObject bot, GameState gameState);
}