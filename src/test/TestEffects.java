import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Enums.Effects;

public class TestEffects {

    @Test
    public void testParseEffects() {
        Integer value = Effects.Afterburner.value;
        List<Effects> actual = Effects.parse(value);
        List<Effects> expected = Arrays.asList(
            Effects.Afterburner
        );
        assertEquals(expected, actual);

        value = Effects.AsteroidField.value | Effects.Superfood.value;
        actual = Effects.parse(value);
        expected = Arrays.asList(
            Effects.AsteroidField,
            Effects.Superfood
        );

        assertEquals(expected, actual);
    }
}
