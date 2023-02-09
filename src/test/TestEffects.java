import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Enums.Effects;

public class TestEffects {

    @Test
    public void testParseEffects() {
        Integer value = Effects.AFTERBURNER.value;
        List<Effects> actual = Effects.parse(value);
        List<Effects> expected = Arrays.asList(
            Effects.AFTERBURNER
        );
        assertEquals(expected, actual);

        value = Effects.ASTEROID_FIELD.value | Effects.SUPERFOOD.value;
        actual = Effects.parse(value);
        expected = Arrays.asList(
            Effects.ASTEROID_FIELD,
            Effects.SUPERFOOD
        );

        assertEquals(expected, actual);
    }
}
