package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;

class GridSearchTest {

    private GridSearch gridSearch;

    @BeforeEach
    void setUp() {
        gridSearch = new GridSearch("left", Compass.N);
        gridSearch.droneCommand = new MockDroneCommands(); 
        AcknowledgeResults mockAcknowledgeResults = new MockAcknowledgeResults();
        gridSearch.setData(mockAcknowledgeResults);
    }

    @Test
    void testMakeDecision() {
        gridSearch.switchState(gridSearch.new ScanState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
    }

    @Test
    void uTurnDirection_ShouldReturnInitialDirection() {
        assertEquals("left", gridSearch.uTurnDirection(), "Initial U-Turn direction should be 'left'.");
    }

    @Test
    void missionToLand_ShouldAlwaysReturnFalse() {
        assertFalse(gridSearch.missionToLand(), "missionToLand should always return false.");
    }

    @Test
    void getCurrentDirection_ShouldReturnCurrentDirection() {
        assertEquals(Compass.N, gridSearch.getCurrentDirection(), "Current direction should be NORTH.");
    }

    @Test
    void testInitialState() {
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(0, gridSearch.islandHalvesExplored);
        assertEquals(0, gridSearch.uTurns);
        assertFalse(gridSearch.checkedForSite);
        assertFalse(gridSearch.turnedLeft);
        assertFalse(gridSearch.turnedRight);
        assertTrue(gridSearch.firstRun);
    }

    @Test
    void testSwitchState() {
        GridSearchState newState = gridSearch.new BigUTurnState();
        gridSearch.switchState(newState);
        assertEquals(newState.getClass().getSimpleName(), gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection() {
        gridSearch.uTurnDirection = "right";
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(1, gridSearch.uTurns);
    }

    @Test
    void testScanState_WhenGroundIsFound() {
        gridSearch.switchState(gridSearch.new ScanState());
        gridSearch.setData(createAcknowledgeResults(true, 5));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testScanState_WhenGroundIsNotFound() {
        gridSearch.switchState(gridSearch.new ScanState());
        gridSearch.setData(createAcknowledgeResults(false, 10));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testFlyState_WhenRangeIsGreaterThanZero() {
        gridSearch.switchState(gridSearch.new FlyState());
        gridSearch.range = 3;
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testFlyState_WhenRangeIsZero() {
        gridSearch.switchState(gridSearch.new FlyState());
        gridSearch.range = 0;
        Commands command = gridSearch.makeDecision();
        assertEquals("scan", command.getValue());
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testEchoState_WhenCreekIsFound() {
        gridSearch.switchState(gridSearch.new EchoState());
        gridSearch.setData(createAcknowledgeResults(true, 5));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("SecondEchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testEchoState_WhenCreekIsNotFound() {
        gridSearch.switchState(gridSearch.new EchoState());
        gridSearch.setData(createAcknowledgeResults(false, 10));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("SecondEchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSecondEchoState_AfterTwoOutOfRangeDetections() {
        gridSearch.switchState(gridSearch.new SecondEchoState());
        gridSearch.outOfRangeCounter = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("FlyToEndState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSecondEchoState_BeforeTwoOutOfRangeDetections() {
        gridSearch.switchState(gridSearch.new SecondEchoState());
        gridSearch.outOfRangeCounter = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("FlyToUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testUTurnState_AfterCompletingRightUTurn() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "right";
        gridSearch.uTurns = 2; 
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testUTurnState_AfterCompletingLeftUTurn() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "left";
        gridSearch.uTurns = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsScanState() {
        gridSearch.switchState(gridSearch.new ScanState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsFlyState() {
        gridSearch.switchState(gridSearch.new FlyState());
        Commands command = gridSearch.makeDecision();
        assertEquals("scan", command.getValue());
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsFlyToEndState() {
        gridSearch.switchState(gridSearch.new FlyToEndState());
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyToEndState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsFlyToUTurnState() {
        gridSearch.switchState(gridSearch.new FlyToUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyToUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsBigUTurnState() {
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsEchoState() {
        gridSearch.switchState(gridSearch.new EchoState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("SecondEchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsSecondEchoState() {
        gridSearch.switchState(gridSearch.new SecondEchoState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("FlyToUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsScanStateAndGroundIsFound() {
        gridSearch.switchState(gridSearch.new ScanState());
        AcknowledgeResults mockAcknowledgeResults = Mockito.mock(AcknowledgeResults.class);
        Mockito.when(mockAcknowledgeResults.groundIsFound()).thenReturn(true);
        gridSearch.setData(mockAcknowledgeResults);
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsScanStateAndGroundIsNotFound() {
        gridSearch.switchState(gridSearch.new ScanState());
        AcknowledgeResults mockAcknowledgeResults = Mockito.mock(AcknowledgeResults.class);
        Mockito.when(mockAcknowledgeResults.groundIsFound()).thenReturn(false);
        gridSearch.setData(mockAcknowledgeResults);
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsSecondEchoState_AndOutOfRangeCounterIsTwo() {
        gridSearch.switchState(gridSearch.new SecondEchoState());
        gridSearch.outOfRangeCounter = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("FlyToEndState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsSecondEchoState_AndOutOfRangeCounterIsNotTwo() {
        gridSearch.switchState(gridSearch.new SecondEchoState());
        gridSearch.outOfRangeCounter = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("FlyToUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsLeft_AndUTurnsIsZero() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "left";
        gridSearch.uTurns = 0;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsLeft_AndUTurnsIsOne() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "left";
        gridSearch.uTurns = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsLeft_AndUTurnsIsTwo() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "left";
        gridSearch.uTurns = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsRight_AndUTurnsIsZero() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "right";
        gridSearch.uTurns = 0;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsRight_AndUTurnsIsOne() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "right";
        gridSearch.uTurns = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState_AndUTurnDirectionIsRight_AndUTurnsIsTwo() {
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "right";
        gridSearch.uTurns = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("EchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testMakeDecision_WhenInitialStateIsUTurnState() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.switchState(gridSearch.new UTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSwitchStateToBigUTurnState() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSwitchStateToScanStateFromBigUTurnState() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        gridSearch.switchState(gridSearch.new ScanState());
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSwitchStateToFlyState() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new FlyState());
        assertEquals("FlyState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testSwitchStateToUTurnState() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new UTurnState());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testEchoState_WhenGroundIsFound() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new EchoState());
        gridSearch.setData(createAcknowledgeResults(true, 5));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("SecondEchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testEchoState_WhenGroundIsNotFound() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new EchoState());
        gridSearch.setData(createAcknowledgeResults(false, 10));
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("SecondEchoState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testFlyToEndState_WhenOutOfRangeCounterIsTwo() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new FlyToEndState());
        gridSearch.outOfRangeCounter = 2;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testFlyToUTurnState_WhenOutOfRangeCounterIsNotTwo() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new FlyToUTurnState());
        gridSearch.outOfRangeCounter = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testBigUTurnState_WhenUTurnDirectionIsRight() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testBigUTurnState_WhenUTurnDirectionIsLeft() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testUTurnState_WhenUTurnDirectionIsLeft_AndUTurnsIsZero() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "left";
        gridSearch.uTurns = 0;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testUTurnState_WhenUTurnDirectionIsRight_AndUTurnsIsOne() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.switchState(gridSearch.new UTurnState());
        gridSearch.uTurnDirection = "right";
        gridSearch.uTurns = 1;
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("UTurnState", gridSearch.getState().getClass().getSimpleName());
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection_WhenUTurnsIsZero() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(1, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection_WhenUTurnsIsOne() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.uTurns = 1;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(2, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection_WhenUTurnsIsTwo() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.uTurns = 2;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(3, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection_WhenUTurnsIsThree() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.uTurns = 3;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("scan", command.getValue());
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(0, gridSearch.uTurns);
        assertEquals("right", gridSearch.uTurnDirection);
    }

    @Test
    void testBigUTurnState_UnderRightUTurnDirection_WhenUTurnsIsGreaterThanThree() {
        GridSearch gridSearch = new GridSearch("right", Compass.N);
        gridSearch.uTurns = 4;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(4, gridSearch.uTurns);
        assertEquals("right", gridSearch.uTurnDirection);
    }

    @Test
    void testBigUTurnState_UnderLeftUTurnDirection_WhenUTurnsIsZero() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(1, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderLeftUTurnDirection_WhenUTurnsIsOne() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.uTurns = 1;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(2, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderLeftUTurnDirection_WhenUTurnsIsTwo() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.uTurns = 2;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("heading", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(3, gridSearch.uTurns);
    }

    @Test
    void testBigUTurnState_UnderLeftUTurnDirection_WhenUTurnsIsThree() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.uTurns = 3;
        gridSearch.switchState(gridSearch.new BigUTurnState());
        Commands command = gridSearch.makeDecision();
        assertEquals("scan", command.getValue());
        assertEquals("ScanState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(0, gridSearch.uTurns);
        assertEquals("left", gridSearch.uTurnDirection);
    }

    @Test
    void testFlyToEndState_WhenDistanceToOOBIsZero() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.distanceToOOB = 0;
        gridSearch.switchState(gridSearch.new FlyToEndState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(0, gridSearch.distanceToOOB);
    }

    @Test
    void testFlyToEndState_WhenDistanceToOOBIsGreaterThanSeven() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.distanceToOOB = 8;
        gridSearch.switchState(gridSearch.new FlyToEndState());
        Commands command = gridSearch.makeDecision();
        assertEquals("fly", command.getValue());
        assertEquals("FlyToEndState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(7, gridSearch.distanceToOOB);
    }

    @Test
    void testFlyToEndState_WhenDistanceToOOBIsLessThanOrEqualToSeven() {
        GridSearch gridSearch = new GridSearch("left", Compass.N);
        gridSearch.distanceToOOB = 7;
        gridSearch.switchState(gridSearch.new FlyToEndState());
        Commands command = gridSearch.makeDecision();
        assertEquals("echo", command.getValue());
        assertEquals("BigUTurnState", gridSearch.getState().getClass().getSimpleName());
        assertEquals(0, gridSearch.distanceToOOB);
    }

    //Helper method to create AcknowledgeResults object
    private AcknowledgeResults createAcknowledgeResults(boolean found, int distance) {
        AcknowledgeResults data = new AcknowledgeResults();
        JSONObject extras = new JSONObject();
        extras.put("found", found ? "GROUND" : "OUT_OF_RANGE");
        extras.put("range", distance);
        data.initializeExtras(new Information(0, extras));
        return data;
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

        @Override
        public Commands scan() {
            return new Commands("scan");
        }
        
        @Override
        public Commands stop() {
            return new Commands("stop");
        }
    }

    private static class MockAcknowledgeResults extends AcknowledgeResults {
        @Override
        public boolean creekIsFound() {
            return true;
        }

        @Override
        public boolean emergencySiteIsFound() {
            return false;
        }

        @Override
        public int distance() {
            return 5;
        }

        @Override
        public boolean groundIsFound() {
            return true;
        }
    }

}
