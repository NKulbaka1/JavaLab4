package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElevatorCallsGenerator implements Runnable {

    // количество этажей
    private int numberOfFloors;

    // общее число пассажиров
    private int numberOfPassengers;

    // частота вызовов лифта
    private int callsFrequency;

    public ElevatorCallsGenerator(int numberOfPassengers, int numberOfFloors, int callsFrequency) {
        this.numberOfPassengers = numberOfPassengers;
        this.numberOfFloors = numberOfFloors;
        this.callsFrequency = callsFrequency;
    }

    @Override
    public void run() {
        // создаём мапу пассажиров на всех этажах и общую очередь вызовов лифта
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Passenger>> passengersOnTheFloors = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<Passenger> callsQueue = new ConcurrentLinkedQueue<>();

        // создаём поток, распределяющий вызовы между лифтами
        ElevatorControllingSystem elevatorControllingSystem = new ElevatorControllingSystem(passengersOnTheFloors, callsQueue);
        Thread elevatorControllingSystemThread = new Thread(elevatorControllingSystem, "Система контроля лифтов");
        elevatorControllingSystemThread.start();

        while (numberOfPassengers > 0) {
            // создаём нового пассажира с осмысленным маршрутом
            int startFloor = (int) (Math.random() * numberOfFloors + 1);
            int finishFloor = (int) (Math.random() * numberOfFloors + 1);
            while (finishFloor == startFloor) {
                finishFloor = (int) (Math.random() * numberOfFloors + 1);
            }

            Passenger passenger = new Passenger(startFloor, finishFloor);
            System.out.println("Вызов с " + startFloor + " этажа на " + finishFloor);

            // добавляем пассажира в мапу и в очередь
            passengersOnTheFloors.putIfAbsent(startFloor, new CopyOnWriteArrayList<>());
            CopyOnWriteArrayList<Passenger> passengers = passengersOnTheFloors.get(startFloor);
            passengers.add(passenger);
            passengersOnTheFloors.put(startFloor, passengers);
            callsQueue.add(passenger);

            numberOfPassengers--;

            try {
                Thread.sleep(callsFrequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
