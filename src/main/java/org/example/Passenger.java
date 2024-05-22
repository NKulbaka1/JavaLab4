package org.example;

public class Passenger {

    // начало пути
    private int startFloor;

    // конец пути
    private int finishFloor;

    public Passenger(int startFloor, int finishFloor) {
        this.startFloor = startFloor;
        this.finishFloor = finishFloor;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getFinishFloor() {
        return finishFloor;
    }
}
