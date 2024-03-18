package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompassTest {
    
    private Compass direction;

    @Test
    void testTurnLeftFacingNorth() {
        direction = Compass.N;
        assertEquals(Compass.W, direction.left());
    }

    @Test
    void testTurnLeftFacingSouth() {
        direction = Compass.S;
        assertEquals(Compass.E, direction.left());
    }

    @Test
    void testTurnLeftFacingEast() {
        direction = Compass.E;
        assertEquals(Compass.N, direction.left());
    }

    @Test
    void testTurnLeftFacingWest() {
        direction = Compass.W;
        assertEquals(Compass.S, direction.left());
    }

    @Test
    void testTurnRightFacingNorth() {
        direction = Compass.N;
        assertEquals(Compass.E, direction.right());
    }

    @Test
    void testTurnRightFacingSouth() {
        direction = Compass.S;
        assertEquals(Compass.W, direction.right());
    }

    @Test
    void testTurnRightFacingEast() {
        direction = Compass.E;
        assertEquals(Compass.S, direction.right());
    }

    @Test
    void testTurnRightFacingWest() {
        direction = Compass.W;
        assertEquals(Compass.N, direction.right());
    }

    @Test
    void testDirectionToStringN() {
        direction = Compass.N;
        assertEquals("N", direction.toString());
    }

    @Test
    void testDirectionToStringS() {
        direction = Compass.S;
        assertEquals("S", direction.toString());
    }

    @Test
    void testDirectionToStringE() {
        direction = Compass.E;
        assertEquals("E", direction.toString());
    }

    @Test
    void testDirectionToStringW() {
        direction = Compass.W;
        assertEquals("W", direction.toString());
    }

}

