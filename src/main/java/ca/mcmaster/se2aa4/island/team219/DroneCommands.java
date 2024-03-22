package ca.mcmaster.se2aa4.island.team219;

public class DroneCommands {

    public DroneCommands() {
    }

    public Commands turnLeft(Compass currentDirection) {
        return new Commands("heading", currentDirection.left());
    }

    public Commands turnRight(Compass currentDirection) {
        return new Commands("heading", currentDirection.right());
    }

    public Commands scan() {
        return new Commands("scan");
    }

    public Commands fly() {
        return new Commands("fly");
    }

    public Commands stop() {
        return new Commands("stop");
    }

    public Commands echoTowards(Compass currentDirection) {
        return new Commands("echo", currentDirection);
    }
}
