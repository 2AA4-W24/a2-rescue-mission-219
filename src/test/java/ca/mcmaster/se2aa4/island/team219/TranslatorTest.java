package ca.mcmaster.se2aa4.island.team219;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class TranslatorTest {
    
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
}

