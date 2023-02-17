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
        setUpAttackingSituation();
        setUpFeedingSituation();
        setUpDefendingSituation();

        var command = determineCommand();

        command.execute(playerAction, bot, gameState);
        logger.info("Execute command: " + command.toString());
        this.playerAction = playerAction;
    }

    private Command determineCommand() {
        Command command;

        var commands = Arrays.asList(Command.values())
                .stream()
                .sorted(Comparator.comparing(Command::getDensity, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        int idx = 0;
        do {
            command = commands.get(idx);
            idx++;
        } while (command.getDangerLevel() == DangerLevel.EXTREME && idx < Command.values().length);

        if (idx == Command.values().length) {
            var rand = new Random();
            playerAction.setAction(PlayerActions.FORWARD);
            playerAction.setHeading(rand.nextInt(360));
            command = Command.NOTHING;
        }

        return command;
    }

    private void setUpFeedingSituation() {
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
                    (int) Math.min(5, 1 + Math.ceil(Util.normalize(
                            Util.euclideanDistance(nearestFood.getPosition(), gameState.getWorld().getCenterPoint()), 0,
                            gameState.getWorld().getRadius()) * 4))));
        }
    }

    private void setUpDefendingSituation() {
        if (gameState.gameObjects.isEmpty()) {
            return;
        }
        var torpedoesCount = gameState.gameObjects
                .stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
                .filter(item -> Util.isValueBetween(
                        Math.abs(item.currentHeading - Util.getHeadingBetween(bot, item)), 179, 181))
                .filter(item -> Util.getDistanceBetween(item, bot) <= 1200)
                .count();
        // batas ukuran untuk penggunaan shield adalah 50
        Command.ACTIVATE_SHIELD.setDangerLevel(DangerLevel
                .valueOf((int) Math.max(1,
                        Math.min(5, 5 - Math.floor(Util.normalize(Math.sqrt(bot.getSize()), 20, 7) * 5)))));
        Command.ACTIVATE_SHIELD
                .setProfit(bot.shieldCount > 0 && !bot.activEffects.contains(Effects.SHIELD)
                        ? (int) torpedoesCount * Command.ATTACK_NEAREST_OPPONENT.getDangerLevel().value * 3
                        : 0);

        Command.AVOID_TORPEDOES
                .setProfit(!bot.activEffects.contains(Effects.SHIELD)
                        ? (int) torpedoesCount * bot.getSpeed()
                        : 0);

        Command.AVOID_GAS_CLOUD.setProfit(bot.activEffects.contains(Effects.GAS_CLOUD) ? bot.size * bot.speed / 2 : 0);

    }

    private void setUpAttackingSituation() {
        Command.FIRE_TORPEDO.setDangerLevel(DangerLevel
                .valueOf((int) Math.max(1,
                        Math.min(5, 5 - Math.floor(Util.normalize(Math.sqrt(bot.getSize()), 15, 5) * 5)))));
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

        final var x = Util.getDistanceBetween(bot, enemy1);

        Command.FIRE_TORPEDO.setProfit(bot.torpedoSalvoCount > 0
                ? (int) (enemy1.getSize()
                        / Util.normalize(
                                x, gameState.getWorld().getRadius() / 4,
                                0))
                : 0);

        Command.ATTACK_NEAREST_OPPONENT.setProfit(enemy1.getSize() / 2);

        if (enemy1.size > bot.size - 6) {
            Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.EXTREME);
            Command.ESCAPE_FROM_ATTACKER.setProfit(enemy1.size - (int) x);
        } else {
            Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.MODERATE);
            if (enemy2 != null) {
                if (Util.getDistanceBetween(enemy1, enemy2) < x) {
                    Command.ATTACK_NEAREST_OPPONENT.setDangerLevel(DangerLevel.VERY_HIGH);
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
