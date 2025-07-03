package com.busreservation.model;

import java.io.Serializable;

/**
 * Abstract base class for all bus types.
 * This demonstrates Abstraction and forms the foundation for Inheritance.
 */
public abstract class Bus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String busId;
    private String operatorName;
    private Route route;
    private boolean isAC;
    private double fare; // Base fare


    public Bus(String busId, String operatorName, Route route, boolean isAC, double fare) {
        this.busId = busId;
        this.operatorName = operatorName;
        this.route = route;
        this.isAC = isAC;
        this.fare = fare;
    }

    // Abstract method - subclasses MUST implement this.
    public abstract String getBusType();

    // Getters
    public String getBusId() { return busId; }
    public String getOperatorName() { return operatorName; }
    public Route getRoute() { return route; }
    public boolean isAC() { return isAC; }
    public double getFare() { return fare; }

    @Override
    public String toString() {
        return operatorName + " (" + getBusType() + (isAC ? ", AC" : ", Non-AC") + ")";
    }

}