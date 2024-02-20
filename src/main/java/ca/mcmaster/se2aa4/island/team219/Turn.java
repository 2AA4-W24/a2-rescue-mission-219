package ca.mcmaster.se2aa4.island.team219;

public enum Turn {
    
    north("west","east"), 
    south("east","west"), 
    east("north","south"), 
    west("south","north");

    private final Turn left;
    private final Turn right;

    Turn(String left, String right) {
        this.left = Turn.valueOf(left);
        this.right = Turn.valueOf(right);
    }

    public Turn left(Turn heading) {
        return this.left;   
    }

    public Turn right(Turn heading) {
        return this.right;
    }

}
