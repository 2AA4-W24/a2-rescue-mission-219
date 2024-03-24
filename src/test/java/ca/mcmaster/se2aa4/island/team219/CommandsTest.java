package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    @Test
    void testCommandWithoutDirection() {
        Commands command = new Commands("stop");
        assertEquals("stop", command.getValue(), "The command value should be 'stop'.");
        assertEquals(Compass.NONE, command.getDirection(), "The command should have 'noDirection'.");

        JSONObject translatedCommand = command.commandTranslator();
        assertEquals("stop", translatedCommand.getString("action"), "The action should be 'stop'.");
        assertFalse(translatedCommand.has("parameters"), "The command should not have parameters.");
    }

    @Test
    void testCommandWithDirectionHeading() {
        Commands command = new Commands("heading", Compass.N);
        assertEquals("heading", command.getValue(), "The command value should be 'heading'.");
        assertEquals(Compass.N, command.getDirection(), "The command direction should be North.");

        JSONObject translatedCommand = command.commandTranslator();
        assertEquals("heading", translatedCommand.getString("action"), "The action should be 'heading'.");
        assertTrue(translatedCommand.has("parameters"), "The command should have parameters.");
        assertEquals("N", translatedCommand.getJSONObject("parameters").getString("direction"), "The direction parameter should be 'N'.");
    }

    @Test
    void testCommandWithDirectionEcho() {
        Commands command = new Commands("echo", Compass.E);
        assertEquals("echo", command.getValue(), "The command value should be 'echo'.");
        assertEquals(Compass.E, command.getDirection(), "The command direction should be East.");

        JSONObject translatedCommand = command.commandTranslator();
        assertEquals("echo", translatedCommand.getString("action"), "The action should be 'echo'.");
        assertTrue(translatedCommand.has("parameters"), "The command should have parameters.");
        assertEquals("E", translatedCommand.getJSONObject("parameters").getString("direction"), "The direction parameter should be 'E'.");
    }

    @Test
    void testCommandNoDirectionEnum() {
        Commands command = new Commands("scan");
        assertEquals("scan", command.getValue(), "The command value should be 'scan'.");
        assertEquals(Compass.NONE, command.getDirection(), "The command should have 'noDirection' as direction.");

        JSONObject translatedCommand = command.commandTranslator();
        assertEquals("scan", translatedCommand.getString("action"), "The action should be 'scan'.");
        assertFalse(translatedCommand.has("parameters"), "The 'scan' command should not have parameters.");
    }
}
