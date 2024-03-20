package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FlyState implements State {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {

        JSONObject decision = new JSONObject();

        this.info = currentInformation;
        data.initializeExtras(info);

        if (drone.getRange() > 0) {
            drone.switchState(new FlyState());
            decision = decision.put("action", "fly");
            drone.range--;
        } else {
            drone.switchState(new ScanState());
            decision = drone.scanGridSearch();
            drone.checkedForSite = true;
        }
        
        return decision;
    } 
}
