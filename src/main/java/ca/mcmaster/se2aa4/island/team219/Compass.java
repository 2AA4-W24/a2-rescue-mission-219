package ca.mcmaster.se2aa4.island.team219;

public enum Compass {

    N, S, E, W, noDirection;

    public Compass left() {
        switch(this) {
            case N: return Compass.W;
            case S: return Compass.E;
            case E: return Compass.N;
            case W: return Compass.S;
            default: return Compass.noDirection;
        }
    }

    public Compass right() {
        switch(this) {
            case N: return Compass.E;
            case S: return Compass.W;
            case E: return Compass.S;
            case W: return Compass.N;
            default: return Compass.noDirection;
        }
    }

    @Override
    public String toString() {
        switch(this) {
            case N: return "N";
            case S: return "S";
            case E: return "E";
            case W: return "W";
            default: return "noDirection";
        }
    }
    
}
