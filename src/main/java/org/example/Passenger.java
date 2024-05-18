package org.example;

public class Passenger {
    private int startFloor;

    private int finishFloor;

    public Passenger(int startFloor, int finishFloor) {
        this.startFloor = startFloor;
        this.finishFloor = finishFloor;
    }

    public Passenger() {
    }

    public int getStartFloor() {
        return startFloor;
    }

    public void setStartFloor(int startFloor) {
        this.startFloor = startFloor;
    }

    public int getFinishFloor() {
        return finishFloor;
    }

    public void setFinishFloor(int finishFloor) {
        this.finishFloor = finishFloor;
    }
}
