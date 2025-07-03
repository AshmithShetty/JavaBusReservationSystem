package com.busreservation.model;

import java.io.Serializable;

/**
 * Represents a route with a source and a destination.
 */
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    private String source;
    private String destination;

    public Route(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return source + " to " + destination;
    }
}