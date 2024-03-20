package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class ScanState implements State {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {
        JSONObject decision = new JSONObject();
        
        this.info = currentInformation;
        data.initializeExtras(info);
        
        if (data.groundIsFound()){
            drone.switchState(new FlyState());
            decision = decision.put("action", "fly");
            drone.outOfRangeCounter = 0;
        } else if (!data.groundIsFound()) {
            drone.switchState(new EchoState());
            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
        }
        return decision;
    } 
}
