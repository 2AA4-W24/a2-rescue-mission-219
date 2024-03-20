package ca.mcmaster.se2aa4.island.team219;

public class HeadingTranslator {

    private Compass currentDirection; 

    public Compass translateDirection(String direction) {

        if (direction.equals("E")){
            currentDirection = Compass.E;
        } else if (direction.equals("W")){
            currentDirection = Compass.W;
        } else if (direction.equals("N")){
            currentDirection = Compass.N;
        } else if (direction.equals("S")){
            currentDirection = Compass.S;
        }

        return currentDirection;
    }
    
}