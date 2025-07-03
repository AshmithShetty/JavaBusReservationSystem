package com.busreservation.model;

/**
 * A concrete subclass representing a Seater bus.
 * This demonstrates Inheritance.
 */
public class SeaterBus extends Bus {

    public SeaterBus(String busId, String operatorName, Route route, boolean isAC, double fare) {
        super(busId, operatorName, route, isAC, fare);
    }

    @Override
    public String getBusType() {
        return "Seater";
    }
}