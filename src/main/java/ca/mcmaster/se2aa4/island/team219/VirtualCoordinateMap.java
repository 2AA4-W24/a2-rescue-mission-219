package ca.mcmaster.se2aa4.island.team219;

public class VirtualCoordinateMap {
    
    private int x;
    private int y;
    private Compass currentDirection;

    public VirtualCoordinateMap(Compass direction, int x, int y) {
        this.currentDirection = direction;
        this.x = x;
        this.y = y;
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

    public int getCurrentX() {
        return x;
    }

    public int getCurrentY() {
        return y;
    }

    public Compass getDirection() {
        return currentDirection;
    }

    
}