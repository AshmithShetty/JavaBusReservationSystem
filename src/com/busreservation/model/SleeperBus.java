package com.busreservation.model;

/**
 * A concrete subclass representing a Sleeper bus.
 * This demonstrates Inheritance.
 */
public class SleeperBus extends Bus {

    public SleeperBus(String busId, String operatorName, Route route, boolean isAC, double fare) {
        super(busId, operatorName, route, isAC, fare);
    }

    @Override
    public String getBusType() {
        return "Sleeper";
    }
}