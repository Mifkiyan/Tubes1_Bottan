package Services;

import Enums.*;
import Models.*;
import Utils.Util;

import java.util.*;
import java.util.stream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }

    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        var rand = new Random();
        var command = Command.EAT_NEAREST_FOOD;
        playerAction.setAction(PlayerActions.FORWARD);
        playerAction.setHeading(rand.nextInt(360));

        if (!gameState.getPlayerGameObjects().isEmpty() && !gameState.getGameObjects().isEmpty()) {
            var opponentList = gameState.getPlayerGameObjects()
                    .stream()
                    .filter(item -> !item.getId().equals(bot.getId()))
                    .sorted(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var foodList = gameState.getGameObjects()
                    .stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD
                            || item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestOpponent = opponentList.get(0);
            var nearestFood = foodList.get(0);

            Command.ATTACK_NEAREST_OPPONENT.setProfit(nearestOpponent.getSize() / 2);
            Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.HIGH);

            Command.EAT_NEAREST_FOOD.setProfit((nearestFood.gameObjectType == ObjectTypes.SUPERFOOD ? 6 : 3) * bot.speed);
            Command.EAT_NEAREST_FOOD.setDangerLevel(DangerLevel.LOW);

            if (bot.activEffects.contains(Effects.SUPERFOOD)) {
                Command.EAT_NEAREST_FOOD.setProfit(Command.EAT_NEAREST_FOOD.getProfit() * 2);
            }

            if (nearestOpponent.getSize() < bot.getSize()) {
                Command.ATTACK_NEAREST_OPPONENT.setProfit(nearestOpponent.getSize() / 2);
                Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.valueOf(
                        Math.min(DangerLevel.EXTREME.value,
                                (int) (Math.ceil((double) nearestOpponent.size / (double) bot.size * 5)))));
            } else {
                Command.EAT_NEAREST_FOOD.setProfit((int) (Command.EAT_NEAREST_FOOD.getProfit() * 1.5));
                Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.EXTREME);
            }

            if (getDistanceBetween(bot, nearestFood) < getDistanceBetween(nearestOpponent, nearestFood)) {
                Command.EAT_NEAREST_FOOD.setDangerLevel(DangerLevel.HIGH);
            }
        }

        var commands = Arrays.asList(Command.values())
                .stream()
                .sorted(Comparator.comparing(Command::getDensity, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        commands.forEach(
                item -> System.out.println(String.format("{Command: %s, Profit: %d, DangerLevel: %s, Density: %f}",
                        item.toString(), item.getProfit(), item.getDangerLevel().toString(), item.getDensity())));

        int idx = 0;
        do {
            command = commands.get(idx);
            idx++;
        } while (command.getDangerLevel() == DangerLevel.EXTREME && idx < Command.values().length);

        // if (!gameState.getGameObjects().isEmpty()) {
        // var foodList = gameState.getGameObjects()
        // .stream().filter(item -> item.getGameObjectType() == ObjectTypes.Food)
        // .sorted(Comparator
        // .comparing(item -> getDistanceBetween(bot, item)))
        // .collect(Collectors.toList());

        // playerAction.heading = getHeadingBetween(foodList.get(0));
        // }

        command.execute(playerAction, bot, gameState);
        logger.info("Execute command: " + command.toString());
        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream()
                .filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

}
