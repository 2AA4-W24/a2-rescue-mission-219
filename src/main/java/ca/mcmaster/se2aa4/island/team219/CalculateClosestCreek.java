package ca.mcmaster.se2aa4.island.team219;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CalculateClosestCreek {

    private ArrayList<String> creekIds;
    private ArrayList<Integer> listOfCreeksX;
    private ArrayList<Integer> listOfCreeksY;
    private int emergencyX;
    private int emergencyY;
    private int closestIndex = -1;
    private double minDistance = Double.MAX_VALUE;
    private double distance = 0;
    
    public CalculateClosestCreek(ArrayList<Integer> listOfCreeksX, ArrayList<Integer> listOfCreeksY, ArrayList<String> creekIds, int emergencyX, int emergencyY) {
        this.listOfCreeksX = listOfCreeksX;
        this.listOfCreeksY = listOfCreeksY;
        this.creekIds = creekIds;
        this.emergencyX = emergencyX;
        this.emergencyY = emergencyY;
    }

    public String calculateClosestCreek() {

        Set<String> visitedCoordinates = new HashSet<>();
    
        for (int i = 0; i < listOfCreeksX.size(); i++) {

            int x = listOfCreeksX.get(i);
            int y = listOfCreeksY.get(i);
            String coordinate = x + "," + y;
    
            if (!visitedCoordinates.contains(coordinate)) {
                visitedCoordinates.add(coordinate);
                distance = Math.sqrt(Math.pow(x - emergencyX, 2) + Math.pow(y - emergencyY, 2));
    
                if (distance < minDistance) {
                    minDistance = distance;
                    closestIndex = i;
                }
            }

        }
    
        if (closestIndex != -1) {
            return creekIds.get(closestIndex);
        } else {
            return "No creek found";
        }

    }
    
}
