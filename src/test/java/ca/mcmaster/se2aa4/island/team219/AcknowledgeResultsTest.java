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
        creeks.put("creek-id-1"); 
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

    @Test
    void testGetClosestCreek() {
        results.storeCoordinates(0, 2); 
        results.storeCoordinates(5, 5); 
        results.storeCoordinatesEmergency(1, 1); 
        results.creekIds.add("creek1");
        results.creekIds.add("creek2");
        String closestCreek = results.getClosestCreek();
        assertEquals("creek1", closestCreek, "Should calculate and return the ID of the closest creek to the emergency site");
    }
    
    @Test
    void testIsFoundWithGround() {
        extras.put("found", "GROUND");
        assertTrue(results.isFound(), "isFound should return true when 'GROUND' is found");
    }

    @Test
    void testIsFoundWithOutOfRange() {
        extras.put("found", "OUT_OF_RANGE");
        assertFalse(results.isFound(), "isFound should return false when 'OUT_OF_RANGE' is found");
    }

    @Test
    void testIsNotFoundWhenKeyAbsent() {
        assertFalse(results.isFound(), "isFound should return false when 'found' key is absent");
    }

    @Test
    void testOutOfBoundsWhenOutOfRange() {
        extras.put("found", "OUT_OF_RANGE");
        assertTrue(results.outOfBounds(), "outOfBounds should return true when 'OUT_OF_RANGE'");
    }

    @Test
    void testOutOfBoundsWhenGround() {
        extras.put("found", "GROUND");
        assertFalse(results.outOfBounds(), "outOfBounds should return false when 'GROUND'");
    }

    @Test
    void testGroundIsNotFoundWithOnlyOcean() {
        JSONArray biomes = new JSONArray();
        biomes.put("OCEAN");
        extras.put("biomes", biomes);
        assertFalse(results.groundIsFound(), "groundIsFound should return false when only 'OCEAN' is present");
    }

    @Test
    void testCreekIsFoundWithCreeks() {
        JSONArray creeks = new JSONArray();
        creeks.put("creek1");
        extras.put("creeks", creeks);
        assertTrue(results.creekIsFound(), "creekIsFound should return true when creeks are present");
    }

    @Test
    void testCreekIsNotFoundWhenEmpty() {
        JSONArray creeks = new JSONArray();
        extras.put("creeks", creeks);
        assertFalse(results.creekIsFound(), "creekIsFound should return false when no creeks are present");
    }

    @Test
    void testEmergencySiteIsFoundWithSites() {
        JSONArray sites = new JSONArray();
        sites.put("site1");
        extras.put("sites", sites);
        assertTrue(results.emergencySiteIsFound(), "emergencySiteIsFound should return true when sites are present");
    }

    @Test
    void testEmergencySiteIsNotFoundWhenEmpty() {
        JSONArray sites = new JSONArray();
        extras.put("sites", sites);
        assertFalse(results.emergencySiteIsFound(), "emergencySiteIsFound should return false when no sites are present");
    }

    @Test
    void testIsFoundWithUnexpectedValue() {
        extras.put("found", "UNKNOWN");
        assertFalse(results.isFound(), "isFound should return false for unexpected 'found' values");
    }

    @Test
    void testIsFoundAbsent() {
        assertFalse(results.isFound(), "isFound should return false when 'found' key is absent");
    }

    @Test
    void testOutOfBoundsAbsent() {
        assertFalse(results.outOfBounds(), "outOfBounds should return false when 'found' key is absent");
    }

    @Test
    void testGroundIsFoundAbsent() {
        assertFalse(results.groundIsFound(), "groundIsFound should return false when 'biomes' key is absent");
    }

    @Test
    void testCreekIsFoundAbsent() {
        assertFalse(results.creekIsFound(), "creekIsFound should return false when 'creeks' key is absent");
    }

    @Test
    void testEmergencySiteIsFoundAbsent() {
        assertFalse(results.emergencySiteIsFound(), "emergencySiteIsFound should return false when 'sites' key is absent");
    }

    @Test
    void testGroundIsFoundWithMixedBiomes() {
        JSONArray biomes = new JSONArray();
        biomes.put("OCEAN").put("FOREST");
        extras.put("biomes", biomes);
        assertTrue(results.groundIsFound(), "groundIsFound should return true when mixed biomes include non-'OCEAN'");
    }

    @Test
    void testCalculateClosestCreekWithDuplicateCoordinates() {
        results.storeCoordinates(0, 2); 
        results.storeCoordinates(0, 2); 
        results.storeCoordinates(5, 5); 
        
        results.creekIds.add("creek1a");
        results.creekIds.add("creek1b"); 
        results.creekIds.add("creek2");
        
        results.storeCoordinatesEmergency(1, 1); 
        
        String closestCreek = results.getClosestCreek();
        assertEquals("creek1a", closestCreek, "Should calculate and return the ID of the closest creek to the emergency site, ignoring duplicate coordinates");
    }
}
