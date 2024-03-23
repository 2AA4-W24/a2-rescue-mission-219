package ca.mcmaster.se2aa4.island.team219;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DroneTest {

    @Mock
    private Battery mockBattery;
    @Mock
    private Compass mockCompass;
    @Mock
    private DecisionMaker mockFindLandDecisionMaker;
    @Mock
    private DecisionMaker mockGridSearchDecisionMaker;
    @Mock
    private DroneCommands mockDroneCommands;

    private Drone drone;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockCompass.toString()).thenReturn("NORTH");
        when(mockFindLandDecisionMaker.getCurrentDirection()).thenReturn(mockCompass);
        when(mockCompass.toString()).thenReturn("NORTH");
        when(mockFindLandDecisionMaker.missionToLand()).thenReturn(true);
        when(mockGridSearchDecisionMaker.getClosestCreek()).thenReturn("CREEK_123");

        drone = new Drone(100, mockCompass);
        drone.findLandDecisionMaker = mockFindLandDecisionMaker;
        drone.gridSearchDecisionMaker = mockGridSearchDecisionMaker;
        drone.droneCommand = mockDroneCommands;
    }

    @Test
    public void testInitializationAndBatteryLevel() {
        assertEquals(100, drone.getBatteryLevelDrone());
        assertTrue(drone.firstRun);
        assertTrue(drone.secondRun);
    }

    @Test
    public void testBatteryLevelWarning() {
        drone.getInfo(new Information(61, new JSONObject()));
        assertTrue(drone.batteryLevelWarning());

        drone.getInfo(new Information(20, new JSONObject())); 
        assertTrue(drone.batteryLevelWarning());
    }

    @Test
    public void testGetClosestCreek() {
        String creek = drone.getClosestCreek();
        assertEquals("CREEK_123", creek);
    }

    @Test
    public void testMakeDecisionFirstRun() {
        Commands command = drone.makeDecision();
        verify(mockDroneCommands).echoTowards(mockCompass);
        assertFalse(drone.firstRun);
    }

    @Test
    public void testMakeDecisionFindLand() {
        drone.firstRun = false;
        when(mockFindLandDecisionMaker.missionToLand()).thenReturn(false);
        drone.makeDecision();
        verify(mockFindLandDecisionMaker).makeDecision();
    }

    @Test
    public void whenFirstRun_thenEchoTowardsIsCalled() {
        drone.makeDecision();
        verify(mockDroneCommands).echoTowards(mockCompass);
        assertFalse(drone.firstRun);
    }

    @Test
    public void whenFindLandMissionIncomplete_thenMakeDecisionCalledOnFindLand() {
        drone.firstRun = false;
        when(mockFindLandDecisionMaker.missionToLand()).thenReturn(false);
        drone.makeDecision();
        verify(mockFindLandDecisionMaker).makeDecision();
    }

    @Test
    public void whenFindLandMissionCompleteAndSecondRun_thenGridSearchInitialized() {
        drone.firstRun = false;
        when(mockFindLandDecisionMaker.missionToLand()).thenReturn(true);
        drone.makeDecision();
        assertNotNull(drone.gridSearchDecisionMaker);
        assertFalse(drone.secondRun);
    }

    @Test
    public void whenBatteryLevelWarning_thenStopCommandIssued() {
        when(mockBattery.getBatteryLevel()).thenReturn(30);
        drone.getInfo(new Information(0, new JSONObject())); 
        Commands result = drone.makeDecision();
        assertEquals(result, drone.droneCommand.stop());
    }


    @Test
    public void testInitializeGridSearch() {
        drone.secondRun = true; 
        drone.initializeGridSearch();
        assertFalse(drone.secondRun);
    }

    @Test
    public void whenBatteryLevelIsLow_thenStopCommandIssued() {
        when(mockBattery.getBatteryLevel()).thenReturn(30);
        drone.batteryLevel = mockBattery;
        
        Commands stopCommand = new Commands("stop");
        when(mockDroneCommands.stop()).thenReturn(stopCommand);
        
        drone.getInfo(new Information(0, new JSONObject())); 
        Commands commandIssued = drone.makeDecision();
        assertNotNull(commandIssued, "Command issued should not be null when battery level is low.");
        assertEquals("stop", commandIssued.getValue(), "Command value should be 'stop' when battery level is low.");
        verify(mockDroneCommands, times(1)).stop();
    }
    
}
