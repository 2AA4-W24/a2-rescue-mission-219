package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HeadingTranslatorTest {

    @Test
    void testTranslateDirectionE() {
        HeadingTranslator headingTranslator = new HeadingTranslator();
        Compass direction = headingTranslator.translateDirection("E");
        assertEquals(Compass.E, direction, "The direction should be East.");
    }

    @Test
    void testTranslateDirectionW() {
        HeadingTranslator headingTranslator = new HeadingTranslator();
        Compass direction = headingTranslator.translateDirection("W");
        assertEquals(Compass.W, direction, "The direction should be West.");
    }

    @Test
    void testTranslateDirectionN() {
        HeadingTranslator headingTranslator = new HeadingTranslator();
        Compass direction = headingTranslator.translateDirection("N");
        assertEquals(Compass.N, direction, "The direction should be North.");
    }

    @Test
    void testTranslateDirectionS() {
        HeadingTranslator headingTranslator = new HeadingTranslator();
        Compass direction = headingTranslator.translateDirection("S");
        assertEquals(Compass.S, direction, "The direction should be South.");
    }
    
    @Test
    void testTranslateDirectionInvalid() {
        HeadingTranslator headingTranslator = new HeadingTranslator();
        Compass direction = headingTranslator.translateDirection("Invalid");
        assertNull(direction, "Invalid directions should result in null.");
    }

}
