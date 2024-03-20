package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public interface State {

    public JSONObject stateChange(GridSearch drone, Information currentInformation);

}
