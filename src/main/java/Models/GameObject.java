package Models;

import Enums.*;
import java.util.*;

public class GameObject {
  public UUID id;
  public Integer size;
  public Integer speed;
  public Integer currentHeading;
  public Position position;
  public ObjectTypes gameObjectType;
  public List<Effects> activEffects;
  public Integer torpedoSalvoCount;
  public Boolean supernovaAvailable;
  public Integer teleporterCount;
  public Integer shieldCount;

  public GameObject(UUID id, Integer size, Integer speed, Integer currentHeading, Position position,
      ObjectTypes gameObjectType, List<Effects> activeEffects) {
    this(id, size, speed, currentHeading, position, gameObjectType, activeEffects, 0, false, 0, 0);
  }

  public GameObject(UUID id, Integer size, Integer speed, Integer currentHeading, Position position,
      ObjectTypes gameObjectType, List<Effects> activeEffects, Integer torpedoSalvoCount, Boolean supernovaAvailable,
      Integer teleporterCount, Integer shieldCount) {
    this.id = id;
    this.size = size;
    this.speed = speed;
    this.currentHeading = currentHeading;
    this.position = position;
    this.gameObjectType = gameObjectType;
    this.activEffects = new ArrayList<>(activeEffects);
    this.torpedoSalvoCount = torpedoSalvoCount;
    this.supernovaAvailable = supernovaAvailable;
    this.teleporterCount = teleporterCount;
    this.shieldCount = shieldCount;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public ObjectTypes getGameObjectType() {
    return gameObjectType;
  }

  public void setGameObjectType(ObjectTypes gameObjectType) {
    this.gameObjectType = gameObjectType;
  }

  public static GameObject FromStateList(UUID id, List<Integer> stateList) {
    Integer size = stateList.get(0);
    Integer speed = stateList.get(1);
    Integer currentHeading = stateList.get(2);
    ObjectTypes gameObjectType = ObjectTypes.valueOf(stateList.get(3));
    Position position = new Position(stateList.get(4), stateList.get(5));
    List<Effects> activeEffects = Effects.parse(stateList.get(6));

    if (stateList.size() != 11) {
      return new GameObject(id, size, speed, currentHeading, position, gameObjectType, activeEffects);
    }

    Integer torpedoSalvoCount = stateList.get(7);
    Boolean supernovaAvailable = stateList.get(8) != 0;
    Integer teleporterCount = stateList.get(9);
    Integer shieldCount = stateList.get(10);
    return new GameObject(id, size, speed, currentHeading, position, gameObjectType, activeEffects, torpedoSalvoCount,
        supernovaAvailable, teleporterCount, shieldCount);
  }
}
