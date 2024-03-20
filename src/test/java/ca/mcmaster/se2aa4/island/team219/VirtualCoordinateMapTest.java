package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMapTest {

    VirtualCoordinateMap map;

    @Test
    void testMoveForwardFacingEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.moveForward();
        assertEquals(1, map.getCurrentX(), "Moving forward facing East should increase x coordinate by 1.");
        assertEquals(0, map.getCurrentY(), "Moving forward facing East, y coordinate should remain unchanged.");
    }

    @Test
        void testMoveForwardFacingWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.moveForward();
        assertEquals(-1, map.getCurrentX(), "Moving forward facing West should decrease x coordinate by 1.");
        assertEquals(0, map.getCurrentY(), "Moving forward facing West, y coordinate should remain unchanged.");
    }

    @Test
    void testMoveForwardFacingNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        assertEquals(0, map.getCurrentX(), "Moving forward facing North, x coordinate should remain unchanged.");
        assertEquals(1, map.getCurrentY(), "Moving forward facing North should increase y coordinate by 1.");
    }

    @Test
    void testMoveForwardFacingSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.moveForward();
        assertEquals(0, map.getCurrentX(), "Moving forward facing South, x coordinate should remain unchanged.");
        assertEquals(-1, map.getCurrentY(), "Moving forward facing South should decrease y coordinate by 1.");
    }

    @Test
    void testTurnRightFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnRight();
        assertEquals(1, map.getCurrentX(), "Turning right from East should increase x coordinate by 1.");
        assertEquals(-1, map.getCurrentY(), "Turning right from East should decrease y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnLeft();
        assertEquals(1, map.getCurrentX(), "Turning left from East should increase x coordinate by 1.");
        assertEquals(1, map.getCurrentY(), "Turning left from East should increase y coordinate by 1.");
    }

    @Test
    void testTurnRightFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnRight();
        assertEquals(-1, map.getCurrentX(), "Turning right from West should decrease x coordinate by 1.");
        assertEquals(1, map.getCurrentY(), "Turning right from West should increase y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnLeft();
        assertEquals(-1, map.getCurrentX(), "Turning left from West should decrease x coordinate by 1.");
        assertEquals(-1, map.getCurrentY(), "Turning left from West should decrease y coordinate by 1.");
    }

    @Test
    void testTurnRightFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnRight();
        assertEquals(1, map.getCurrentX(), "Turning right from North should increase x coordinate by 1.");
        assertEquals(1, map.getCurrentY(), "Turning right from North should increase y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnLeft();
        assertEquals(-1, map.getCurrentX(), "Turning left from North should decrease x coordinate by 1.");
        assertEquals(1, map.getCurrentY(), "Turning left from North should increase y coordinate by 1.");
    }

    @Test
    void testTurnRightFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnRight();
        assertEquals(-1, map.getCurrentX(), "Turning right from South should decrease x coordinate by 1.");
        assertEquals(-1, map.getCurrentY(), "Turning right from South should decrease y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnLeft();
        assertEquals(1, map.getCurrentX(), "Turning left from South should increase x coordinate by 1.");
        assertEquals(-1, map.getCurrentY(), "Turning left from South should decrease y coordinate by 1.");
    }

    @Test
    void testMultipleMovements() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        map.turnRight();
        map.moveForward();
        assertEquals(2, map.getCurrentX(), "After moving forward, turning right, and moving forward again, x coordinate should be 2.");
        assertEquals(2, map.getCurrentY(), "After moving forward, turning right, and moving forward again, y coordinate should be 2.");
    }

}


