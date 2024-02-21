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
        }
    }

    public void turnLeft() {
        if (currentDirection == Turn.N) {
            x -= 1; 
            y += 1; 
        } else if (currentDirection == Turn.S) {
            x += 1; 
            y -= 1; 
        } else if (currentDirection == Turn.E) {
            x += 1; 
            y += 1; 
        } else if (currentDirection == Turn.W) {
            x -= 1; 
            y -= 1; 
        }

        currentDirection = currentDirection.left();

    }

    public void turnRight() {
        if (currentDirection == Turn.N) {
            x += 1; 
            y += 1;
        } else if (currentDirection == Turn.S) {
            x -= 1; 
            y -= 1; 
        } else if (currentDirection == Turn.E) {
            x += 1; 
            y -= 1; 
        } else if (currentDirection == Turn.W) {
            x -= 1; 
            y += 1; 
        }

        currentDirection = currentDirection.right();

    }

    public String getCurrentPosition() {
        return "("  +x+  ","  +y+  ")";
    }
    
}