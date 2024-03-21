package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HeadingTranslatorTest {

    @Test
    void testTranslate() {
        Translator translator = new Translator();
        JSONObject response = new JSONObject()
                .put("cost", 5)
                .put("extras", new JSONObject().put("key", "value"));
        Information info = translator.translate(response);

        assertEquals(5, info.getCost(), "The cost should be correctly translated.");
        assertEquals("value", info.getExtrasJson().getString("key"), "The extras should be correctly translated.");
    }

    @Test
    void testTranslateDirectionE() {
        Translator translator = new Translator();
        Compass direction = translator.translateDirection("E");
        assertEquals(Compass.E, direction, "The direction should be East.");
    }

    @Test
    void testTranslateDirectionW() {
        Translator translator = new Translator();
        Compass direction = translator.translateDirection("W");
        assertEquals(Compass.W, direction, "The direction should be West.");
    }

    @Test
    void testTranslateDirectionN() {
        Translator translator = new Translator();
        Compass direction = translator.translateDirection("N");
        assertEquals(Compass.N, direction, "The direction should be North.");
    }

    @Test
    void testTranslateDirectionS() {
        Translator translator = new Translator();
        Compass direction = translator.translateDirection("S");
        assertEquals(Compass.S, direction, "The direction should be South.");
    }
    
    @Test
    void testTranslateDirectionInvalid() {
        Translator translator = new Translator();
        Compass direction = translator.translateDirection("Invalid");
        assertNull(direction, "Invalid directions should result in null.");
    }

}
