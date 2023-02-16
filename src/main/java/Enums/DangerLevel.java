package Enums;

public enum DangerLevel {
    LOW(1),
    MODERATE(2),
    HIGH(3),
    VERY_HIGH(4),
    EXTREME(5);

    public final Integer value;

    private DangerLevel(Integer value) {
        this.value = value;
    }

    public static DangerLevel valueOf(Integer value) {
        for (DangerLevel dangerLevel : DangerLevel.values()) {
            if (dangerLevel.value == value)
                return dangerLevel;
        }

        throw new IllegalArgumentException("Value not found: " + value);
    }

}
