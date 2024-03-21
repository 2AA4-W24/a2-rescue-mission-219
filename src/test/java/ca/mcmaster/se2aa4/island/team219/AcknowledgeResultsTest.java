package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AcknowledgeResultsTest {

    private AcknowledgeResults results;
    private JSONObject extras;

    @BeforeEach
    void setUp() {
        extras = new JSONObject();  
        results = new AcknowledgeResults();
        Information info = new Information(0, extras);
        results.initializeExtras(info); 
    }


    public void initializeExtras(Information info) {
        this.extras = (info != null && info.getExtrasJson() != null) ? info.getExtrasJson() : new JSONObject();
    }

    @Test
    void testInitializeExtrasWithGroundFound() {
        extras.put("found", "GROUND");
        assertTrue(results.isFound(), "Ground should be found when 'found' key equals 'GROUND'");
    }

    @Test
    void testOutOfBounds() {
        extras.put("found", "OUT_OF_RANGE");
        assertTrue(results.outOfBounds(), "Should recognize when out of bounds");
    }

    @Test
    void testCreekIsFound() {
        JSONArray creeks = new JSONArray();
        creeks.put("creek-id-1"); // Add creek ID to JSONArray
        extras.put("creeks", creeks); 
        assertTrue(results.creekIsFound(), "Should recognize when a creek is found");
    }

    @Test
    void testEmergencySiteIsFound() {
        JSONArray sites = new JSONArray();
        sites.put("site-id-1");
        extras.put("sites", sites);
        assertTrue(results.emergencySiteIsFound(), "Should recognize when an emergency site is found");
    }

    @Test
    void testGroundIsFoundWithBiomes() {
        JSONArray biomes = new JSONArray();
        biomes.put("MANGROVE");
        extras.put("biomes", biomes);
        assertTrue(results.groundIsFound(), "Should recognize ground when biomes other than OCEAN are found");
    }

    /*@Test
    void testGetClosestCreek() {
        results.storeCoordinates(0, 2); // Creek 1 at (0,2)
        results.storeCoordinates(5, 5); // Creek 2 at (5,5)
        results.storeCoordinatesEmergency(1, 1); // Emergency site at (1,1)
        results.creekIds.add("creek1");
        results.creekIds.add("creek2");
        String closestCreek = results.getClosestCreek();
        assertEquals("creek1", closestCreek, "Should calculate and return the ID of the closest creek to the emergency site");
    }*/
    
}