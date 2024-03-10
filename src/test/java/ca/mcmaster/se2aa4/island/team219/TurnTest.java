package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TurnTest {
    
    private Turn direction;

    @Test
    void testTurnLeftFacingNorth() {
        direction = Turn.N;
        assertEquals(Turn.W, direction.left());
    }

    @Test
    void testTurnLeftFacingSouth() {
        direction = Turn.S;
        assertEquals(Turn.E, direction.left());
    }

    @Test
    void testTurnLeftFacingEast() {
        direction = Turn.E;
        assertEquals(Turn.N, direction.left());
    }

    @Test
    void testTurnLeftFacingWest() {
        direction = Turn.W;
        assertEquals(Turn.S, direction.left());
    }

    @Test
    void testTurnRightFacingNorth() {
        direction = Turn.N;
        assertEquals(Turn.E, direction.right());
    }

    @Test
    void testTurnRightFacingSouth() {
        direction = Turn.S;
        assertEquals(Turn.W, direction.right());
    }

    @Test
    void testTurnRightFacingEast() {
        direction = Turn.E;
        assertEquals(Turn.S, direction.right());
    }

    @Test
    void testTurnRightFacingWest() {
        direction = Turn.W;
        assertEquals(Turn.N, direction.right());
    }

    @Test
    void testDirectionToStringN() {
        direction = Turn.N;
        assertEquals("N", direction.toString());
    }

    @Test
    void testDirectionToStringS() {
        direction = Turn.S;
        assertEquals("S", direction.toString());
    }

    @Test
    void testDirectionToStringE() {
        direction = Turn.E;
        assertEquals("E", direction.toString());
    }

    @Test
    void testDirectionToStringW() {
        direction = Turn.W;
        assertEquals("W", direction.toString());
    }

}