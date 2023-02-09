package Enums;

import java.util.Comparator;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Utils.CommandLogic;
import Utils.Util;

public enum Command {
    EatNearestFood((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var nearestFood = gameState.getGameObjects()
                    .stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.Food)
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .get();

            playerAction.setAction(PlayerActions.Forward);
            playerAction.setHeading(Util.getHeadingBetween(bot, nearestFood));
        }
    }),

    AttackNearestOpponent((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var nearestOpponent = gameState.getPlayerGameObjects()
                    .stream()
                    .filter(item -> !item.getId().equals(bot.getId()))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .get();

            playerAction.setAction(PlayerActions.Forward);
            playerAction.setHeading(Util.getHeadingBetween(bot, nearestOpponent));
        }
    });

    private final CommandLogic logic;

    private Command(final CommandLogic logic) {
        this.logic = logic;
    }

    public void executeLogic(PlayerAction playerAction, GameObject bot, GameState gameState) {
        this.logic.execute(playerAction, bot, gameState);
    }
}
