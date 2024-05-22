package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElevatorControllingSystem implements Runnable {

    // мапа с пассажирами на этажах
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Passenger>> passengersOnTheFloors;

    // общая очередь вызовов лифта
    private ConcurrentLinkedQueue<Passenger> callsQueue;

    public ElevatorControllingSystem(ConcurrentHashMap<Integer, CopyOnWriteArrayList<Passenger>> passengersOnTheFloors, ConcurrentLinkedQueue<Passenger> callsQueue) {
        this.passengersOnTheFloors = passengersOnTheFloors;
        this.callsQueue = callsQueue;
    }

    @Override
    public void run() {
        // создаём монитор и потоки с лифтами
        Object monitor = new Object();
        Elevator firstElevator = new Elevator(monitor, passengersOnTheFloors);
        Elevator secondElevator = new Elevator(monitor, passengersOnTheFloors);

        Thread firstElevatorThread = new Thread(firstElevator, "Лифт 1");
        Thread secondElevatorThread = new Thread(secondElevator, "Лифт 2");

        firstElevatorThread.start();
        secondElevatorThread.start();

        while (true) {
            if (!callsQueue.isEmpty()) {
                if (firstElevator.free) {
                    firstElevator.elevatorCallsQueue.add(callsQueue.poll());
                } else if (secondElevator.free) {
                    secondElevator.elevatorCallsQueue.add(callsQueue.poll());
                }
            }
        }
    }
}
