package Enums;

import java.util.ArrayList;
import java.util.List;

public enum Effects {
    AFTERBURNER(1),
    ASTEROID_FIELD(2),
    GAS_CLOUD(4),
    SUPERFOOD(8),
    SHIELD(16);

    public final Integer value;

    private Effects(Integer value) {
        this.value = value;
    }

    public static List<Effects> parse(Integer value) {
        List<Effects> activeEffects = new ArrayList<>();
        for (int i = 0; i < Effects.values().length; i++) {
            if (((value >> i) & 1) == 1) {
                activeEffects.add(Effects.values()[i]);
            }
        }
        return activeEffects;
    }
}
