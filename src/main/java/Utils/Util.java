package Utils;

import Models.GameObject;
import Models.Position;

public class Util {
    public static double getDistanceBetween(GameObject object1, GameObject object2) {
        return euclideanDistance(object1.position, object2.position);
    }

    public static int getHeadingBetween(GameObject bot, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    public static double euclideanDistance(Position pos1, Position pos2) {
        var deltaX = pos1.x - pos2.x;
        var deltaY = pos1.y - pos2.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public static int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    public static double normalize(double value, double max, double min) {
        return (value - min) / (max - min);
    }
}
