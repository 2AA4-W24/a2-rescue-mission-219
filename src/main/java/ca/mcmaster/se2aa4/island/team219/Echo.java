package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONArray;
import org.json.JSONObject;

public class Echo {

    public boolean landFound = false;
    public boolean groundFound = false;
    public boolean nothingFound = false;
    public JSONObject extras;

    public void initializeExtras(Information info) {
        if (!(info == null)){
            extras = info.getExtrasJson();
        } else {
            extras = new JSONObject();
        }

    }

    public boolean isFound() {
        if (extras.has("found")){
            String found = extras.getString("found");
            landFound = "GROUND".equals(found);
        } else {
            landFound = false;
        }
        return landFound;
    }
    public boolean outOfBounds() {
        if (extras.has("found")){
            String found = extras.getString("found");
            nothingFound = "OUT_OF_RANGE".equals(found);
        } else {
            nothingFound = false;
        }

        if (nothingFound == true){
            if (this.distance() < 2){
                nothingFound = true;
            } else {
                nothingFound = false;
            }
        }
        return nothingFound;
    }

    public boolean groundIsFound() {
        if (extras.has("biomes")) {
            JSONArray biomesArray = extras.getJSONArray("biomes");
            if (biomesArray.length() == 0) {
                groundFound = false;
            } else {
                for (int i = 0; i < biomesArray.length(); i++) {
                    String biome = biomesArray.getString(i);
                    if (!"OCEAN".equals(biome)) {
                        groundFound = true;
                        break;
                    }
                }
            }
        } else {
            groundFound = false;
        }
        return groundFound;
    }
    
    public boolean creekIsFound() {
        if (extras.has("creeks")) {
            JSONArray creeksArray = extras.getJSONArray("creeks");
            if (creeksArray.length() > 0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean emergencySiteIsFound() {
        if (extras.has("sites")) {
            JSONArray sitesArray = extras.getJSONArray("sites");
            if (sitesArray.length() > 0) {
                return true;
            }
        }
        return false;
    }
    

    public int distance() {
        int range;
        if (extras.has("range")){
            range = extras.getInt("range");
        }else{
            range = 0;
        }

        return range;
    }

}