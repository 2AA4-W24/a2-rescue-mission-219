package ca.mcmaster.se2aa4.island.team219;

public class VirtualCoordinateMap {
    
    private int x = 0;
    private int y = 0;
    private Turn currentDirection;

    public VirtualCoordinateMap(Turn direction) {
        this.currentDirection = direction;
    }

    public void moveForward() {
        switch (currentDirection) {
            case E:
                x += 1;
                break;
            case W:
                x -= 1;
                break;
            case N:
                y += 1;
                break;
            case S:
                y -= 1;
                break;
            default:
                break;
        }
    }

    public void turnLeft() {
        switch (currentDirection) {
            case N:
                x -= 1; 
                y += 1;
                break;
            case S:
                x += 1; 
                y -= 1;
                break;
            case E:
                x += 1; 
                y += 1;
                break;
            case W:
                x -= 1; 
                y -= 1;
                break;
            default:
                break;
        }
        currentDirection = currentDirection.left();
    }

    public void turnRight() {
        switch (currentDirection) {
            case N:
                x += 1; 
                y += 1;
                break;
            case S:
                x -= 1; 
                y -= 1;
                break;
            case E:
                x += 1; 
                y -= 1;
                break;
            case W:
                x -= 1; 
                y += 1;
                break;
            default:
                break;
        }
        currentDirection = currentDirection.right();
    }

    public String getCurrentPosition() {
        return "("  +x+  ","  +y+  ")";
    }

    void storeCoordinates() { // will use later
        //store important coordinates (creeks, emergency site) 
    }

    void calculateDistance() { // will use later
        //calculate distance between creeks and emergency site
    }
    
}