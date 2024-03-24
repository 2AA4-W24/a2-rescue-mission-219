package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DroneCommandsTest {

    private final DroneCommands droneCommands = new DroneCommands();

    @Test
    void whenTurnLeftCommandIssued_ShouldReturnTurnLeftCommand() {
        Compass initialDirection = Compass.N;
        Commands command = droneCommands.turnLeft(initialDirection);
        assertEquals("heading", command.getValue(), "Turn left command should have 'heading' as its value.");
        assertEquals(Compass.W, command.getDirection(), "After turning left from North, the drone should be facing West.");
    }

    @Test
    void whenTurnRightCommandIssued_ShouldReturnTurnRightCommand() {
        Compass initialDirection = Compass.N;
        Commands command = droneCommands.turnRight(initialDirection);
        assertEquals("heading", command.getValue(), "Turn right command should have 'heading' as its value.");
        assertEquals(Compass.E, command.getDirection(), "After turning right from North, the drone should be facing East.");
    }

    @Test
    void whenScanCommandIssued_ShouldReturnScanCommand() {
        Commands command = droneCommands.scan();
        assertEquals("scan", command.getValue(), "Issuing a scan command should return a command with 'scan' as its value.");
    }

    @Test
    void whenFlyCommandIssued_ShouldReturnFlyCommand() {
        Commands command = droneCommands.fly();
        assertEquals("fly", command.getValue(), "Issuing a fly command should return a command with 'fly' as its value.");
    }

    @Test
    void whenStopCommandIssued_ShouldReturnStopCommand() {
        Commands command = droneCommands.stop();
        assertEquals("stop", command.getValue(), "Issuing a stop command should return a command with 'stop' as its value.");
    }

    @Test
    void whenEchoCommandIssued_ShouldReturnEchoCommand() {
        Compass currentDirection = Compass.N;
        Commands command = droneCommands.echoTowards(currentDirection);
        assertEquals("echo", command.getValue(), "Issuing an echo command should return a command with 'echo' as its value.");
        assertEquals(Compass.N, command.getDirection(), "The echo command should maintain the drone's current facing direction, North.");
    }
}
