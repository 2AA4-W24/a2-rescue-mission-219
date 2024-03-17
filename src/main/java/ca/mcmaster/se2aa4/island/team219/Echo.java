package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

public class Echo {

    private boolean landFound = false;
    private boolean groundFound = false;
    private boolean nothingFound = false;
    private boolean creekFound = false;
    private boolean emergencySiteFound = false;
    public JSONObject extras;
    

    private ArrayList<String> creekIds = new ArrayList<>();
    private ArrayList<Integer> listOfCreeksX = new ArrayList<>();
    private ArrayList<Integer> listOfCreeksY = new ArrayList<>();
    public String closestCreek = "";
    public int emergencyX = 0;
    public int emergencyY = 0;
    public int indexOfClosestCreek = 0;
    public int closestIndex = -1;
    double minDistance = Double.MAX_VALUE;
    double distance = 0;

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

    public ArrayList<String> getCreekIds() {
        return creekIds;
    } 
    public ArrayList<Integer> getCreekx() {
        return listOfCreeksX;
    } 
    public ArrayList<Integer> getCreeky() {
        return listOfCreeksY;
    } 

    public int emergencyXss() {
        return emergencyX;
    } 
    
    public int emergencyYss() {
        return emergencyY;
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


    public String calculateClosestCreek() {
        Set<String> visitedCoordinates = new HashSet<>();
        closestIndex = -1;
        minDistance = Double.MAX_VALUE;
    
        for (int i = 0; i < listOfCreeksX.size(); i++) {
            int x = listOfCreeksX.get(i);
            int y = listOfCreeksY.get(i);
            String coordinate = x + "," + y;
    
            if (visitedCoordinates.contains(coordinate)) {
                continue; 
            }
            visitedCoordinates.add(coordinate);
    
            double distance = Math.sqrt(Math.pow(x - emergencyX, 2) + Math.pow(y - emergencyY, 2));
    
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
    
        if (closestIndex != -1) {
            return creekIds.get(closestIndex);
        } else {
            return "No creek found";
        }
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