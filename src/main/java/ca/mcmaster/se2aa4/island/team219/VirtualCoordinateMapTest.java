package ca.mcmaster.se2aa4.island.team219;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMapTest {

    VirtualCoordinateMap map;

    @Test
    void testMoveForwardFacingEast() {
        map = new VirtualCoordinateMap(Turn.E);
        map.moveForward();
        assertEquals("(1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingWest() {
        map = new VirtualCoordinateMap(Turn.W);
        map.moveForward();
        assertEquals("(-1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingNorth() {
        map = new VirtualCoordinateMap(Turn.N);
        map.moveForward();
        assertEquals("(0,1)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingSouth() {
        map = new VirtualCoordinateMap(Turn.S);
        map.moveForward();
        assertEquals("(0,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromEast() {
        map = new VirtualCoordinateMap(Turn.E);
        map.turnRight();
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromEast() {
        map = new VirtualCoordinateMap(Turn.E);
        map.turnLeft();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromWest() {
        map = new VirtualCoordinateMap(Turn.W);
        map.turnRight();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromWest() {
        map = new VirtualCoordinateMap(Turn.W);
        map.turnLeft();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromNorth() {
        map = new VirtualCoordinateMap(Turn.N);
        map.turnRight();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromNorth() {
        map = new VirtualCoordinateMap(Turn.N);
        map.turnLeft();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromSouth() {
        map = new VirtualCoordinateMap(Turn.S);
        map.turnRight();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromSouth() {
        map = new VirtualCoordinateMap(Turn.S);
        map.turnLeft();
    
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testMultipleMovements() {
        map = new VirtualCoordinateMap(Turn.N);
        map.moveForward();
        map.turnRight(); 
        map.moveForward(); 
        assertEquals("(2,2)", map.getCurrentPosition());
    }
}
