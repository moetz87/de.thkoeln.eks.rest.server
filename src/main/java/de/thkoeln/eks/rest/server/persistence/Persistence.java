package de.thkoeln.eks.rest.server.persistence;

import de.thkoeln.eks.rest.server.resources.Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Persistence {

    private static Persistence instance;
    private List<Driver> drivers;

    private Persistence() {
        drivers = new ArrayList<>();
    }

    public static Persistence getInstance() {
        if (instance == null) {
            instance = new Persistence();
        }
        return instance;
    }

    public boolean addDriver(Driver driver) {
        return this.drivers.add(driver);
    }

    public Optional<Driver> getDriver(String name) {
        return this.drivers.stream().filter(d -> d.getName().equals(name)).findFirst();
    }

    public List<Driver> getDriver() {
        return this.drivers;
    }

}
