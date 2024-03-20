package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class bigUTurnState implements State {

    private AcknowledgeResults data = new AcknowledgeResults();

    private Information info = new Information(0, new JSONObject());

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {
        JSONObject decision = new JSONObject();

        this.info = currentInformation;

        data.initializeExtras(info);
        
        if (drone.uTurnDirection.equals("right")) {
            if (drone.uTurns == 0) {
                decision = drone.turnLeftGridSearch();
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
                drone.islandHalvesExplored++;
            } else if (drone.uTurns == 1) {
                decision = decision.put("action", "fly");
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
            } else if (drone.uTurns == 2) {
                decision = drone.turnLeftGridSearch();
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
            } else if (drone.uTurns == 3) {
                decision = drone.scanGridSearch();
                drone.switchState(new ScanState());
                drone.checkedForSite = true;
                drone.uTurns = 0;
                drone.uTurnDirection = "right";
                drone.islandHalvesExplored++;
            }
        } else if (drone.uTurnDirection.equals("left")) {
            if (drone.uTurns == 0) {
                decision = drone.turnRightGridSearch();
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
                drone.islandHalvesExplored++;
            } else if (drone.uTurns == 1) {
                decision = decision.put("action", "fly");
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
            } else if (drone.uTurns == 2) {
                decision = drone.turnRightGridSearch();
                drone.uTurns++;
                drone.switchState(new bigUTurnState());
            } else if (drone.uTurns == 3) {
                decision = drone.scanGridSearch();
                drone.checkedForSite = true;
                drone.switchState(new ScanState());
                drone.uTurns = 0;
                drone.uTurnDirection = "left";
            }
        }

        
        return decision;
    } 
}
