package org.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ElevatorCallsGenerator implements Runnable{

    private final int numberOfFloors;

    private int numberOfPassengers;

    public ElevatorCallsGenerator(int numberOfPassengers, int numberOfFloors) {
        this.numberOfPassengers = numberOfPassengers;
        this.numberOfFloors = numberOfFloors;
    }

    public void generateCalls(Map<Integer, Integer> passengersOnTheFloors){
        Map<Integer, List<Passenger>> passengersOnTheFloors = new ConcurrentHashMap<Integer, new Concurrent>();

        while(numberOfPassengers > 0) {

            int starFloor = (int) (Math.random() * numberOfFloors + 1);
            int finishFloor = (int) (Math.random() * numberOfFloors + 1);
            while (finishFloor == starFloor) {
                finishFloor = (int) (Math.random() * numberOfFloors + 1);
            }

            Passenger passenger = new Passenger(starFloor, finishFloor);

            passengersOnTheFloors.get(s)

            numberOfPassengers--;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        generateCalls();
    }
}
