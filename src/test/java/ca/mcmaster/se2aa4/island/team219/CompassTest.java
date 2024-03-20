package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompassTest {
    
    private Compass direction;

    @Test
    void testTurnLeftFacingNorth() {
        direction = Compass.N;
        assertEquals(Compass.W, direction.left(), "Turning left from North should face West.");
    }

    @Test
    void testTurnLeftFacingSouth() {
        direction = Compass.S;
        assertEquals(Compass.E, direction.left(), "Turning left from South should face East.");
    }

    @Test
    void testTurnLeftFacingEast() {
        direction = Compass.E;
        assertEquals(Compass.N, direction.left(), "Turning left from East should face North.");
    }

    @Test
    void testTurnLeftFacingWest() {
        direction = Compass.W;
        assertEquals(Compass.S, direction.left(), "Turning left from West should face South.");
    }

    @Test
    void testTurnRightFacingNorth() {
        direction = Compass.N;
        assertEquals(Compass.E, direction.right(), "Turning right from North should face East.");
    }

    @Test
    void testTurnRightFacingSouth() {
        direction = Compass.S;
        assertEquals(Compass.W, direction.right(), "Turning right from South should face West.");
    }

    @Test
    void testTurnRightFacingEast() {
        direction = Compass.E;
        assertEquals(Compass.S, direction.right(), "Turning right from East should face South.");
    }

    @Test
    void testTurnRightFacingWest() {
        direction = Compass.W;
        assertEquals(Compass.N, direction.right(), "Turning right from West should face North.");
    }

    @Test
    void testDirectionToStringN() {
        direction = Compass.N;
        assertEquals("N", direction.toString(), "The string representation of North should be 'N'.");
    }

    @Test
    void testDirectionToStringS() {
        direction = Compass.S;
        assertEquals("S", direction.toString(), "The string representation of South should be 'S'.");
    }

    @Test
    void testDirectionToStringE() {
        direction = Compass.E;
        assertEquals("E", direction.toString(), "The string representation of East should be 'E'.");
    }

    @Test
    void testDirectionToStringW() {
        direction = Compass.W;
        assertEquals("W", direction.toString(), "The string representation of West should be 'W'.");
    }
    
}