package org.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class App {

    private static final int NUMBER_PASSENGERS = 10;
    private static final int NUMBER_OF_FLOOR = 10;


    public static void main(String[] args) {
        ElevatorCallsGenerator elevatorCallsGenerator = new ElevatorCallsGenerator(NUMBER_PASSENGERS, NUMBER_OF_FLOOR);
        Thread elevatorCallsGeneratorThread = new Thread(elevatorCallsGenerator, "ElevatorCallsGeneratorThread");
        elevatorCallsGeneratorThread.start();
    }
}

//По одному потоку на каждый лифт и один поток под taskGenerator. Лифты постепенно развозят людей, пришедших из генератора
