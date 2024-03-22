package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class AcknowledgeResults {

    private boolean groundFound = false;
    private boolean emergencySiteFound = false;
    private ArrayList<Integer> listOfCreeksX = new ArrayList<>();
    private ArrayList<Integer> listOfCreeksY = new ArrayList<>();
    private int emergencyX = 0;
    private int emergencyY = 0;
    private JSONObject extras;
    private String foundString = "found";
    public ArrayList<String> creekIds = new ArrayList<>(); // make private and add getter for test

    public void initializeExtras(Information info) {
        if (info != null){
            extras = info.getExtrasJson();
        } else {
            extras = new JSONObject();
        }
    }

    public boolean isFound() {
        boolean landFound;
        if (extras.has(foundString)){
            String found = extras.getString(foundString);
            landFound = "GROUND".equals(found);
        } else {
            landFound = false;
        }
        return landFound;
    }

    public boolean outOfBounds() {
        boolean nothingFound;
        if (extras.has(foundString)){
            String found = extras.getString(foundString);
            nothingFound = "OUT_OF_RANGE".equals(found);
        } else {
            nothingFound = false;
        }
        if (nothingFound){
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
        boolean creekFound;
        if (extras.has("creeks")) {
            JSONArray creeksArray = extras.getJSONArray("creeks");
            if (creeksArray.length() == 0) {
                creekFound = false;
            } else {
                for (int i = 0; i < creeksArray.length(); i++) {
                    String creek = creeksArray.getString(i);
                    creekIds.add(creek);
                }
                creekFound = true;
            }
        } else {
            creekFound = false;  
        }
        return creekFound;
    }

    public int distance() {
        return extras.optInt("range", 0);
    }
    
    public boolean emergencySiteIsFound() {
        if (extras.has("sites")) {
            JSONArray sitesArray = extras.getJSONArray("sites");
            if (sitesArray.length() > 0) {
                emergencySiteFound = true; 
            } else {
                emergencySiteFound = false; 
            } 
        } 
        return emergencySiteFound;
    }
    
    public void storeCoordinates(int x, int y) {
        listOfCreeksX.add(x);
        listOfCreeksY.add(y);
    }

    public void storeCoordinatesEmergency(int x, int y) {
        emergencyX = x;
        emergencyY = y;
    }

    public String getClosestCreek() { 
        CalculateClosestCreek closestCreekID;
        closestCreekID = new CalculateClosestCreek(listOfCreeksX, listOfCreeksY, creekIds, emergencyX, emergencyY);   
        return closestCreekID.calculateClosestCreek();
    }

}