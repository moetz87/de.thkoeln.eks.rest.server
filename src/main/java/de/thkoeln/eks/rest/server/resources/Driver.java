package de.thkoeln.eks.rest.server.resources;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "driver")
@XmlAccessorType(XmlAccessType.FIELD)
public class Driver {

    @XmlID
    private String name;
    @XmlElementWrapper(name = "cars")
    @XmlElement(name = "car")
    private List<Car> cars;

    public Driver(String name, List<Car> cars) {
        this.name = name;
        this.cars = cars;
    }

    public Driver(String name) {
        this(name, new ArrayList<>());
    }

    public Driver() {
        this(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", cars=" + cars +
                '}';
    }

}
