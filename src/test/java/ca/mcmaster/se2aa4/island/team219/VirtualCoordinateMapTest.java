package ca.mcmaster.se2aa4.island.team219;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMapTest {

    VirtualCoordinateMap map;

    @Test
    void testMoveForwardFacingEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.moveForward();
        assertEquals("(1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.moveForward();
        assertEquals("(-1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        assertEquals("(0,1)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.moveForward();
        assertEquals("(0,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnRight();
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromEast() {
        map = new VirtualCoordinateMap(Compass.E, 0, 0);
        map.turnLeft();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnRight();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromWest() {
        map = new VirtualCoordinateMap(Compass.W, 0, 0);
        map.turnLeft();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnRight();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromNorth() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.turnLeft();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnRight();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromSouth() {
        map = new VirtualCoordinateMap(Compass.S, 0, 0);
        map.turnLeft();
    
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testMultipleMovements() {
        map = new VirtualCoordinateMap(Compass.N, 0, 0);
        map.moveForward();
        map.turnRight(); 
        map.moveForward(); 
        assertEquals("(2,2)", map.getCurrentPosition());
    }
}




