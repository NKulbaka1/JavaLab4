package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Elevator implements Runnable {

    // время прохождения лифтом одного этажа
    private final int timeToPassOneFloor = 500;

    // время остановки на этаже
    private final int floorStopTime = 1000;

    // текущий этаж
    private int currentfloor = 1;

    // флаг занятости лифта
    public boolean free = true;

    // флаг движения вниз
    private boolean movingDown = false;

    // флаг движения вверх
    private boolean movingUp = false;

    // объект синхронизации
    private final Object monitor;

    // внутренняя очередь вызовов
    public ConcurrentLinkedQueue<Passenger> elevatorCallsQueue = new ConcurrentLinkedQueue<>();

    // мапа пассажиров на этажах
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Passenger>> passengersOnTheFloors;

    // пассажиры в лифте
    private List<Passenger> passengers = new ArrayList<>();

    public Elevator(Object monitor, ConcurrentHashMap<Integer, CopyOnWriteArrayList<Passenger>> passengersOnTheFloors) {
        this.monitor = monitor;
        this.passengersOnTheFloors = passengersOnTheFloors;
    }

    @Override
    public void run() {
        while (true) {
            if (!elevatorCallsQueue.isEmpty()) {
                // проверяем, не забрал ли этого пассажира один из лифтов в качестве попутчика
                Passenger passengerToCheck = elevatorCallsQueue.peek();
                CopyOnWriteArrayList<Passenger> passengersOnTheCheckingFloor = passengersOnTheFloors.get(passengerToCheck.getStartFloor());

                // если человека нет на этаже, то удаляем его из очереди
                if (!passengersOnTheCheckingFloor.contains(passengerToCheck)) {
                    elevatorCallsQueue.poll();
                // если он всё ещё ждёт лифт, отправляем лифт к нему
                } else {
                    Passenger passenger = elevatorCallsQueue.poll();
                    startTransferringPassengers(passenger);
                }
            }
        }
    }

    private void startTransferringPassengers(Passenger passenger) {
        free = false;

        // едем на вызов
        moveToTheFloor(passenger.getStartFloor());

        // если в лифте кто-то есть, отвозим его
        while (!passengers.isEmpty()) {
            Passenger elevatorPassenger = passengers.get(0);
            moveToTheFloor(elevatorPassenger.getFinishFloor());
        }

        free = true;
    }

    private void moveToTheFloor(int targetFloor) {
        if (currentfloor == targetFloor) {
            boolean elevatorStopped = true;

            // проверяем, нужно ли кого впустить в лифт или выпустить из него
            pickUpPassengersOnTheFloor(elevatorStopped);
            dropPassengersOnTheFloor(elevatorStopped);

            return;
        }

        System.out.println(Thread.currentThread().getName() + " поехал на " + targetFloor + " этаж");
        if (targetFloor < currentfloor) {
            movingDown = true;
        } else {
            movingUp = true;
        }

        while (currentfloor != targetFloor) {
            boolean elevatorStopped = false;

            if (movingUp) {
                moveUp();
            } else if (movingDown) {
                moveDown();
            }

            if (currentfloor == targetFloor) {
                movingDown = false;
                movingUp = false;
            }

            // проверяем, нужно ли кого-нибудь впустить в лифт или выпустить из него
            pickUpPassengersOnTheFloor(elevatorStopped);
            dropPassengersOnTheFloor(elevatorStopped);
        }

        movingDown = false;
        movingUp = false;
    }

    private void moveDown() {
        currentfloor--;
        try {
            Thread.sleep(timeToPassOneFloor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveUp() {
        currentfloor++;
        try {
            Thread.sleep(timeToPassOneFloor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopOnTheFloor() {
        try {
            Thread.sleep(floorStopTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void pickUpPassenger(Passenger passenger) {
        System.out.println(Thread.currentThread().getName() + " забрал пассажира на " + passenger.getStartFloor() + " этаже");
        passengers.add(passenger);
    }

    private void pickUpPassengersOnTheFloor(boolean elevatorStopped) {
        synchronized (monitor) {
            // люди на текущем этаже
            CopyOnWriteArrayList<Passenger> passengersOnTheCurrentFloor = passengersOnTheFloors.get(currentfloor);

            if (passengersOnTheCurrentFloor != null && !passengersOnTheCurrentFloor.isEmpty()) {

                // список людей на этаже, который встанет на место старого
                CopyOnWriteArrayList<Passenger> newPassengersOnTheCurrentFloor = new CopyOnWriteArrayList<>(passengersOnTheCurrentFloor);

                for (int i = 0; i < passengersOnTheCurrentFloor.size(); i++) {
                    Passenger passenger = passengersOnTheCurrentFloor.get(i);
                    int passengerTargetFloor = passenger.getFinishFloor();

                    // если человек и лифт едут вниз, то берём человека
                    if (passengerTargetFloor < currentfloor && movingDown) {
                        if (!elevatorStopped) {
                            stopOnTheFloor();
                            elevatorStopped = true;
                        }

                        pickUpPassenger(passenger);

                        newPassengersOnTheCurrentFloor.remove(passenger);
                        // если человек и лифт едут вверх, то берём человека
                    } else if (passengerTargetFloor > currentfloor && movingUp) {
                        pickUpPassenger(passenger);

                        newPassengersOnTheCurrentFloor.remove(passenger);
                        // если лифт никуда не едет, берём всех
                    } else if (!movingDown && !movingUp) {
                        pickUpPassenger(passenger);

                        newPassengersOnTheCurrentFloor.remove(passenger);
                    }
                }
                passengersOnTheFloors.put(currentfloor, newPassengersOnTheCurrentFloor);
            }
        }
    }

    private void dropPassenger(Passenger passenger) {
        System.out.println(Thread.currentThread().getName() + " высадил пассажира на " + currentfloor + " этаже");
        passengers.remove(passenger);
    }

    private void dropPassengersOnTheFloor(boolean elevatorStopped) {
        if (!passengers.isEmpty()) {
            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                if (currentfloor == passenger.getFinishFloor()) {
                    if (!elevatorStopped) {
                        stopOnTheFloor();
                        elevatorStopped = true;
                    }

                    dropPassenger(passenger);
                }
            }
        }
    }
}