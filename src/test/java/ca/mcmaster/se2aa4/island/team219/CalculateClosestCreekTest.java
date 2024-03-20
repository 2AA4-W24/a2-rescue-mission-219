package ca.mcmaster.se2aa4.island.team219;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

public class CalculateClosestCreekTest {

    @Test
    public void testNoCreeks() {
        CalculateClosestCreek calculator = new CalculateClosestCreek(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, 0);
        assertEquals("No creek found", calculator.calculateClosestCreek(), "Should return 'No creek found' when no creeks are present.");
    }

    @Test
    public void testSingleCreekIsClosest() {
        ArrayList<Integer> listOfCreeksX = new ArrayList<>(Arrays.asList(10));
        ArrayList<Integer> listOfCreeksY = new ArrayList<>(Arrays.asList(10));
        ArrayList<String> creekIds = new ArrayList<>(Arrays.asList("Creek1"));
        CalculateClosestCreek calculator = new CalculateClosestCreek(listOfCreeksX, listOfCreeksY, creekIds, 0, 0);
        assertEquals("Creek1", calculator.calculateClosestCreek(), "Should return the ID of the only creek when it is the closest.");
    }

    @Test
    public void testMultipleCreeksWithOneClosest() {
        ArrayList<Integer> listOfCreeksX = new ArrayList<>(Arrays.asList(10, 20, 30));
        ArrayList<Integer> listOfCreeksY = new ArrayList<>(Arrays.asList(10, 20, 30));
        ArrayList<String> creekIds = new ArrayList<>(Arrays.asList("Creek1", "Creek2", "Creek3"));
        CalculateClosestCreek calculator = new CalculateClosestCreek(listOfCreeksX, listOfCreeksY, creekIds, 0, 0);
        assertEquals("Creek1", calculator.calculateClosestCreek(), "Should return the ID of the closest creek.");
    }

    @Test
    public void testEqualDistanceCreeks() {
        ArrayList<Integer> listOfCreeksX = new ArrayList<>(Arrays.asList(10, -10));
        ArrayList<Integer> listOfCreeksY = new ArrayList<>(Arrays.asList(10, 10));
        ArrayList<String> creekIds = new ArrayList<>(Arrays.asList("Creek1", "Creek2"));
        CalculateClosestCreek calculator = new CalculateClosestCreek(listOfCreeksX, listOfCreeksY, creekIds, 0, 0);
        assertEquals("Creek1", calculator.calculateClosestCreek(), "Should return the first encountered ID when creeks have the same distance to emergency site."); // returns the first encountered closest creek in case of ties
    }
}