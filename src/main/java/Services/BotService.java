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

        setUpAttackingSituation();
        setUpFeedingSituation();

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

        command.execute(playerAction, bot, gameState);
        logger.info("Execute command: " + command.toString());
        this.playerAction = playerAction;
    }

    void setUpFeedingSituation() {
        if (gameState.gameObjects.isEmpty()) {
            return;
        }
        var nearestFood = gameState.getGameObjects()
                .stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD
                        || item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                .min(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                .orElse(null);
        Command.EAT_NEAREST_FOOD.setProfit(0);
        Command.EAT_NEAREST_FOOD.setDangerLevel(DangerLevel.LOW);
        if (nearestFood != null) {
            Command.EAT_NEAREST_FOOD.setProfit(bot.speed);
            if (bot.activEffects.contains(Effects.SUPERFOOD)) {
                Command.EAT_NEAREST_FOOD.setProfit(bot.speed * 2);
            }
            Command.EAT_NEAREST_FOOD.setDangerLevel(DangerLevel.valueOf(
                    (int) Math.ceil(Util.normalize(
                            Util.euclideanDistance(nearestFood.getPosition(), gameState.getWorld().getCenterPoint()), 0,
                            gameState.getWorld().getRadius()) * 5)));
        }
    }

    void setUpAttackingSituation() {
        if (gameState.playerGameObjects.isEmpty()) {
            return;
        }
        var opponentList = gameState.getPlayerGameObjects()
                .stream()
                .filter(item -> !item.getId().equals(bot.getId()))
                .sorted(Comparator.comparing(item -> Util.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        var enemy1 = opponentList.get(0); // nearest from bot
        var enemy2 = opponentList // nearest from enemy1
                .stream()
                .filter(item -> !item.getId().equals(enemy1.getId()))
                .min(Comparator.comparing(item -> Util.getDistanceBetween(item, enemy1)))
                .orElse(null);

        Command.ATTACK_NEAREST_OPPONENT.setProfit(enemy1.getSize() / 2);

        if (enemy1.size > bot.size) {
            Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.EXTREME);
        } else {
            Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.MODERATE);
            if (enemy2 != null) {
                if (enemy2.size > enemy1.size
                        && Util.getDistanceBetween(enemy1, enemy2) < Util.getDistanceBetween(enemy1, bot)) {
                    Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.EXTREME);
                } else {
                    Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.HIGH);
                }
            }
        }
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

}
