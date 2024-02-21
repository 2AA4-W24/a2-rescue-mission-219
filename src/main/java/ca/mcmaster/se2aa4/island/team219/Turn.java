package ca.mcmaster.se2aa4.island.team219;

public enum Turn {

    N, S, E, W;

    public Turn left() {
        switch(this) {
            case N: return Turn.W;
            case S: return Turn.E;
            case E: return Turn.N;
            case W: return Turn.S;
            default: return this;
        }
    }

    public Turn right() {
        switch(this) {
            case N: return Turn.E;
            case S: return Turn.W;
            case E: return Turn.S;
            case W: return Turn.N;
            default: return this;
        }
    }

    @Override
    public String toString() {
        switch(this) {
            case N: return "N";
            case S: return "S";
            case E: return "E";
            case W: return "W";
            default: return "Unknown";
        }
    }
}
