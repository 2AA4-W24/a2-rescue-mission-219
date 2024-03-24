package ca.mcmaster.se2aa4.island.team219;

public interface DecisionMaker {
    Commands makeDecision();
    void getInfo(Information info);
    boolean missionToLand();
    String uTurnDirection();
    Compass getCurrentDirection();
    String getClosestCreek();
}