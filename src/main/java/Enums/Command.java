package Enums;

import java.util.Comparator;
import java.util.stream.Collectors;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Utils.CommandLogic;
import Utils.Util;

public enum Command {
    NOTHING((PlayerAction, bot, gameState) -> {
        return;
    }),

    EAT_NEAREST_FOOD((playerAction, bot, gameState) -> {
        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD
                            || item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestFood = foodList.isEmpty() ? null : foodList.get(0);

            var centerHeading = Util.getHeadingToCenter(bot);

            var foodHeadingToCenter = foodList.isEmpty() ? null
                    : foodList
                            .stream()
                            .filter(item -> Util
                                    .isValueBetween(Math.abs(centerHeading - Util.getHeadingBetween(bot, item)), 0, 70))
                            .findFirst()
                            .orElse(null);

            playerAction.setAction(PlayerActions.FORWARD);
            if (nearestFood != null) {
                playerAction.setHeading(Util.getHeadingBetween(bot, nearestFood));
            }
            if (Util.euclideanDistance(bot.position, gameState.world.centerPoint) - bot.size > gameState.world.radius
                    - 50) {
                if (foodHeadingToCenter != null) {
                    playerAction.setHeading(Util.getHeadingBetween(bot, foodHeadingToCenter));
                } else {
                    playerAction.setHeading(Util.getHeadingToCenter(bot) + 45);
                }
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
                            && Util.isValueBetween(Math.abs(
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
            if (Util.euclideanDistance(bot.position, gameState.world.centerPoint) - bot.size > gameState.world.radius
                    - 50) {
                playerAction.setHeading(Util.getHeadingToCenter(bot) + 45);
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
    }),

    AVOID_GAS_CLOUD((playerAction, bot, gameState) -> {
        var gas = gameState.getGameObjects()
                .stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                .orElse(null);

        if (gas != null) {
            playerAction.setAction(PlayerActions.FORWARD);
            playerAction.setHeading(Util.getHeadingBetween(bot, gas) + 90);
        }
    }),

    AVOID_TORPEDOES((playerAction, bot, gameState) -> {
        var torpedo = gameState.getGameObjects()
                .stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
                .filter(item -> Util.isValueBetween(Math.abs(item.currentHeading - Util.getHeadingBetween(bot, item)), 179, 181))
                .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                .orElse(null);

        if (torpedo != null) {
            playerAction.setAction(PlayerActions.FORWARD);
            playerAction.setHeading(Util.getHeadingBetween(bot, torpedo) + 15);
        }
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
