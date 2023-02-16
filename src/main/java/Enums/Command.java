package Enums;

import java.util.Comparator;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Utils.CommandLogic;
import Utils.Util;

public enum Command {
    EAT_NEAREST_FOOD((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var nearestFood = gameState.getGameObjects()
                    .stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD
                            || item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .orElse(null);

            playerAction.setAction(PlayerActions.FORWARD);
            if (nearestFood != null) {
                playerAction.setHeading(Util.getHeadingBetween(bot, nearestFood));
            } else {
                playerAction.setHeading(Util.getHeadingToCenter(bot));
            }
        }
    }),

    ATTACK_NEAREST_OPPONENT((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var nearestOpponent = gameState.getPlayerGameObjects()
                    .stream()
                    .filter(item -> !item.getId().equals(bot.getId()))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .get();

            playerAction.setAction(PlayerActions.FORWARD);
            playerAction.setHeading(Util.getHeadingBetween(bot, nearestOpponent));
        }
    }),

    ESCAPE_FROM_ATTACKER((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty() && !gameState.getPlayerGameObjects().isEmpty()) {
            var nearestOpponent = gameState.getPlayerGameObjects()
                    .stream()
                    .filter(item -> !item.getId().equals(bot.getId()))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .get();
            var targetFood = gameState.getGameObjects()
                    .stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD
                            || item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                            && Util.valueBetween(Math.abs(
                                    nearestOpponent.currentHeading - Util.getHeadingBetween(item, nearestOpponent)), 0,
                                    180))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .orElse(null);
            var nearestFood = gameState.getGameObjects()
                    .stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD
                            || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .orElse(null);

            playerAction.setAction(PlayerActions.FORWARD);
            if (targetFood != null && nearestFood != null) {
                if (Util.getDistanceBetween(bot, nearestOpponent) < gameState.world.radius) {
                    playerAction.setHeading(Util.getHeadingBetween(bot, nearestFood));
                } else {
                    playerAction.setHeading(Util.getHeadingBetween(bot, targetFood));
                }
            } else {
                playerAction.setHeading(Util.getHeadingToCenter(bot));
            }
        }
    }),

    FIRE_TORPEDO((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var nearestOpponent = gameState.getPlayerGameObjects()
                    .stream()
                    .filter(item -> !item.getId().equals(bot.getId()))
                    .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .get();

            playerAction.setAction(PlayerActions.FIRETORPEDOES);
            playerAction.setHeading(Util.getHeadingBetween(bot, nearestOpponent));
        }
    }),

    ACTIVATE_SHIELD((playerAction, bot, gameState) -> {
        playerAction.setAction(PlayerActions.ACTIVATESHIELD);
    });

    private final CommandLogic logic;

    private Integer profit;
    private DangerLevel dangerLevel;

    private Command(final CommandLogic logic) {
        this.logic = logic;
        profit = 0;
        dangerLevel = DangerLevel.LOW;
    }

    public Integer getProfit() {
        return profit;
    }

    public DangerLevel getDangerLevel() {
        return dangerLevel;
    }

    public void setProfit(Integer profit) {
        this.profit = profit;
    }

    public void setDangerLevel(DangerLevel dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public double getDensity() {
        return (double) profit / (double) dangerLevel.value;
    }

    public void execute(PlayerAction playerAction, GameObject bot, GameState gameState) {
        logic.execute(playerAction, bot, gameState);
    }
}
