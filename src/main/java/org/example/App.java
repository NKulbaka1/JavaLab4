package org.example;

public class App {

    private static final int NUMBER_PASSENGERS = 10;

    private static final int NUMBER_OF_FLOORS = 10;

    private static final int ELEVATOR_CALLS_FREQUENCY = 500;

    public static void main(String[] args) {
        ElevatorCallsGenerator elevatorCallsGenerator = new ElevatorCallsGenerator(NUMBER_PASSENGERS, NUMBER_OF_FLOORS, ELEVATOR_CALLS_FREQUENCY);
        Thread elevatorCallsGeneratorThread = new Thread(elevatorCallsGenerator, "Генератор вызовов лифтов");
        elevatorCallsGeneratorThread.start();
    }
}