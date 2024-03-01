package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMapTest {

    @Test
    void testMoveForwardFacingEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.moveForward();
        assertEquals("(1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.moveForward();
        assertEquals("(-1,0)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.moveForward();
        assertEquals("(0,1)", map.getCurrentPosition());
    }

    @Test
    void testMoveForwardFacingSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.moveForward();
        assertEquals("(0,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.turnRight();
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.turnLeft();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.turnRight();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.turnLeft();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.turnRight();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.turnLeft();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    void testTurnRightFromSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.turnRight();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    void testTurnLeftFromSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.turnLeft();
    
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    void testMultipleMovements() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.moveForward(); // Move north
        map.turnRight(); // Face east
        map.moveForward(); // Move east
        assertEquals("(2,2)", map.getCurrentPosition());
    }
}
