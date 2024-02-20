package ca.mcmaster;

import ca.mcmaster.se2aa4.island.team219.Information;

import org.json.JSONObject;

public class Echo {

    public boolean landFound = false;
    public JSONObject extras;

    public void initializeExtras(Information info) {
        extras = info.getExtrasJson();
    }

    public boolean isFound() {
        String found = extras.getString("found");
        landFound = "GROUND".equals(found);
        return landFound;
    }

    public int distance() {
        int range = extras.getInt("range");
        return range;
    }

}
