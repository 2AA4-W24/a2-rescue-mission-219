package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.json.JSONObject;

class FindLandTest {

    private FindLand findLand;

    @BeforeEach
    void setUp() {
        findLand = new FindLand(Compass.N);
        findLand.droneCommand = new MockDroneCommands();
        AcknowledgeResults mockAcknowledgeResults = new MockAcknowledgeResults();
        findLand.setData(mockAcknowledgeResults);
    }

    @Test
    void getClosestCreek_ShouldReturnExpectedCreekId() {
        AcknowledgeResults mockAcknowledgeResults = mock(AcknowledgeResults.class);
        String expectedCreekId = "creek-123";
        when(mockAcknowledgeResults.getClosestCreek()).thenReturn(expectedCreekId);
        findLand.setData(mockAcknowledgeResults);
        String actualCreekId = findLand.getClosestCreek();
        assertEquals(expectedCreekId, actualCreekId, "The method should return the expected creek ID.");
    }

    @Test
    void uTurnDirection_ShouldReturnInitialDirection() {
        assertEquals("left", findLand.uTurnDirection(), "Initial U-Turn direction should be 'left'.");
    }

    @Test
    void missionToLand_ShouldReflectMissionStatus() {
        assertFalse(findLand.missionToLand(), "Initially, missionToLand should be false.");
    }

    @Test
    void getCurrentDirection_ShouldReturnCurrentDirection() {
        assertEquals(Compass.N, findLand.getCurrentDirection(), "Current direction should be NORTH.");
    }

    @Test
    public void testInitialState() {
        assertEquals("StartingState", findLand.getStateName());
        assertFalse(findLand.missionToLand());
        assertEquals("left", findLand.uTurnDirection());
        assertEquals("No creek found", findLand.getClosestCreek());
    }

    @Test
    void testInvalidInitialState() {
        assertNotEquals("FlyForwardState", findLand.getStateName(), "Initial state should not be FlyForwardState");
        assertNotEquals("TurnToLandState", findLand.getStateName(), "Initial state should not be TurnToLandState");
    }

    @Test
    void makeDecision_StateChangeCalled() {
        FindLandState mockState = mock(FindLandState.class);
        when(mockState.stateChange(any())).thenReturn(new Commands("dummy"));
        FindLand findLand = new FindLand(Compass.N);
        findLand.switchState(mockState);
        findLand.makeDecision();
        verify(mockState, times(1)).stateChange(findLand);
    }

    private AcknowledgeResults createAcknowledgeResults(boolean found, int distance) {
        AcknowledgeResults data = new AcknowledgeResults();
        JSONObject extras = new JSONObject();
        extras.put("found", found ? "GROUND" : "OUT_OF_RANGE");
        extras.put("range", distance);
        data.initializeExtras(new Information(0, extras));
        return data;
    }

    @Test
    void testEchoRightState() {
        findLand.switchState(findLand.new EchoRightState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoLeftState", findLand.getStateName());
        findLand.setData(createAcknowledgeResults(true, 5));
        command = findLand.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyForwardState", findLand.getStateName());
    }

    @Test
    void testEchoLeftState() {
        findLand.switchState(findLand.new EchoLeftState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyForwardState", findLand.getStateName());
        findLand.setData(createAcknowledgeResults(true, 5));
        command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoRightState", findLand.getStateName());
    }

    @Test
    void testFlyForwardState() {
        findLand.switchState(findLand.new FlyForwardState());
        findLand.dontEchoRight = true;
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoLeftState", findLand.getStateName());
        findLand.dontEchoRight = false;
        command = findLand.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyForwardState", findLand.getStateName());
    }

    @Test
    public void testMakeDecisionInitialState() {
        Commands command = findLand.makeDecision();
        assertNotNull(command);
        assertEquals("echo", command.getValue());
        assertEquals("EchoForwardState", findLand.getStateName());
    }

    @Test
    public void testSwitchState() {
        FindLandState newState = findLand.new EchoForwardState();
        findLand.switchState(newState);
        assertEquals(newState.getClass().getSimpleName(), findLand.getStateName());
    }

    @Test
    public void testEchoForwardState() {
        findLand.switchState(findLand.new EchoForwardState());
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoRightState", findLand.getStateName());
    }

    @Test
    void makeDecision_UpdateUTurnDirection_LeftTurn() {
        FindLand findLand = new FindLand(Compass.N);
        findLand.turnedLeft = true;
        findLand.makeDecision();
        assertEquals("left", findLand.uTurnDirection());
    }

    @Test
    void makeDecision_UpdateUTurnDirection_RightTurn() {
        FindLand findLand = new FindLand(Compass.N);
        findLand.turnedRight = true;
        findLand.makeDecision();
        assertEquals("right", findLand.uTurnDirection());
    }

    @Test
    void testTurnToLandState() {
        findLand.switchState(findLand.new TurnToLandState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("scan", command.getValue());
        assertTrue(findLand.missionToLand());
        assertEquals("TurnToLandState", findLand.getStateName());
        findLand.setData(createAcknowledgeResults(true, 5));
        command = findLand.makeDecision();
        assertEquals("scan", command.getValue());
        assertTrue(findLand.missionToLand());
        assertEquals("TurnToLandState", findLand.getStateName());
    }

    @Test
    void testEchoForwardState_NoCreekFound() {
        findLand.switchState(findLand.new EchoForwardState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoRightState", findLand.getStateName());
    }

    @Test
    void testTurnLeftToLandState_WhenLandIsNotFound() {
        findLand.switchState(findLand.new EchoLeftState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyForwardState", findLand.getStateName());
    }

    @Test
    void testEchoRightState_WhenLandIsFound() {
        findLand.switchState(findLand.new EchoRightState());
        findLand.setData(createAcknowledgeResults(true, 5));
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoLeftState", findLand.getStateName());
    }

    @Test
    void testEchoRightState_WhenLandIsNotFound() {
        findLand.switchState(findLand.new EchoRightState());
        findLand.setData(createAcknowledgeResults(false, 10));
        Commands command = findLand.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoLeftState", findLand.getStateName());
        findLand.setData(createAcknowledgeResults(false, 2));
        command = findLand.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyForwardState", findLand.getStateName());
    }

    private static class MockDroneCommands extends DroneCommands {
        @Override
        public Commands fly() {
            return new Commands("fly");
        }

        @Override
        public Commands echoTowards(Compass currentDirection) {
            return new Commands("echo");
        }

        @Override
        public Commands turnLeft(Compass currentDirection) {
            return new Commands("heading", Compass.W);
        }

        @Override
        public Commands turnRight(Compass currentDirection) {
            return new Commands("heading", Compass.E);
        }
        
    }

    private static class MockAcknowledgeResults extends AcknowledgeResults {
        @Override
        public boolean isFound() {
            return false;
        }

        @Override
        public int distance() {
            return 3;
        }

    }

}
