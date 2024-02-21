package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualCoordinateMaptest {
    
    @Test
    void movingEastShouldIncrementX() {
        // Assuming Turn.EAST is a valid enum constant for direction east
        VirtualCoordinateMap map = new VirtualCoordinateMap(Turn.E);
        
        // Act
        map.moveForward();
        
        // Assert
        assertEquals("(1,0)", map.getCurrentPosition(), "After moving east, x should be incremented.");
    }
}