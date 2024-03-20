package ca.mcmaster.se2aa4.island.team219;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMapTest {

    VirtualCoordinateMap map;

    @Test
    void testMoveForwardFacingEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.moveForward();
        assertEquals("(1,0)", map.getCurrentPosition(), "Moving forward facing East should increase x coordinate by 1.");
    }

    @Test
    void testMoveForwardFacingWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.moveForward();
        assertEquals("(-1,0)", map.getCurrentPosition(),"Moving forward facing West should decrease x coordinate by 1.");
    }

    @Test
    void testMoveForwardFacingNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        assertEquals("(0,1)", map.getCurrentPosition(),"Moving forward facing North should increase y coordinate by 1");
    }

    @Test
    void testMoveForwardFacingSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.moveForward();
        assertEquals("(0,-1)", map.getCurrentPosition(),"Moving forward facing South should decrease y coordinate by 1");
    }

    @Test
    void testTurnRightFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnRight();
        assertEquals("(1,-1)", map.getCurrentPosition(), "Turning right from East should increase x coordinate by 1 and decrease y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnLeft();
        assertEquals("(1,1)", map.getCurrentPosition(), "Turning left from East should increase x coordinate by 1 and increase y coordinate by 1.");
    }

    @Test
    void testTurnRightFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnRight();
        assertEquals("(-1,1)", map.getCurrentPosition(), "Turning right from West should decrease x coordinate by 1 and increase y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnLeft();
        assertEquals("(-1,-1)", map.getCurrentPosition(), "Turning left from West should decrease x coordinate by 1 and decrease y coordinate by 1.");
    }

    @Test
    void testTurnRightFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnRight();
        assertEquals("(1,1)", map.getCurrentPosition(), "Turning right from North should increase x coordinate by 1 and increase y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnLeft();
        assertEquals("(-1,1)", map.getCurrentPosition(), "Turning left from North should decrease x coordinate by 1 and increase y coordinate by 1.");
    }

    @Test
    void testTurnRightFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnRight();
        assertEquals("(-1,-1)", map.getCurrentPosition(), "Turning right from South should decrease x coordinate by 1 and decrease y coordinate by 1.");
    }

    @Test
    void testTurnLeftFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnLeft();
    
        assertEquals("(1,-1)", map.getCurrentPosition(), "Turning left from South should increase x coordinate by 1 and decrease y coordinate by 1.");
    }

    @Test
    void testMultipleMovements() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        map.turnRight(); 
        map.moveForward(); 
        assertEquals("(2,2)", map.getCurrentPosition(), "(FACING NORTH!) After moving forward (1,0). After turning right (2,1). After moving forward again, should be at (2,2).");
    }
}


