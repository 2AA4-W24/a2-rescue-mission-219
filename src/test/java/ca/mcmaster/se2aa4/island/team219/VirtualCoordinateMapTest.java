package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VirtualCoordinateMapTest {

    @Test
    public void testMoveForwardFacingEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.moveForward();
        assertEquals("(1,0)", map.getCurrentPosition());
    }

    @Test
    public void testMoveForwardFacingWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.moveForward();
        assertEquals("(-1,0)", map.getCurrentPosition());
    }

    @Test
    public void testMoveForwardFacingNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.moveForward();
        assertEquals("(0,1)", map.getCurrentPosition());
    }

    @Test
    public void testMoveForwardFacingSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.moveForward();
        assertEquals("(0,-1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnRightFromEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.turnRight();
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnLeftFromEast() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        map.turnLeft();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnRightFromWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.turnRight();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnLeftFromWest() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.W);
        map.turnLeft();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnRightFromNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.turnRight();
        assertEquals("(1,1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnLeftFromNorth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.turnLeft();
        assertEquals("(-1,1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnRightFromSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.turnRight();
        assertEquals("(-1,-1)", map.getCurrentPosition());
    }

    @Test
    public void testTurnLeftFromSouth() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.S);
        map.turnLeft();
    
        assertEquals("(1,-1)", map.getCurrentPosition());
    }

    @Test
    public void testMultipleMovements() {
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.N);
        map.moveForward(); // Move north
        map.turnRight(); // Face east
        map.moveForward(); // Move east
        assertEquals("(2,2)", map.getCurrentPosition());
    }
}
