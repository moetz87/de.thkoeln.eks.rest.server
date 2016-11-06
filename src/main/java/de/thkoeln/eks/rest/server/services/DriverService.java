package de.thkoeln.eks.rest.server.services;

import de.thkoeln.eks.rest.server.persistence.Persistence;
import de.thkoeln.eks.rest.server.resources.Car;
import de.thkoeln.eks.rest.server.resources.Driver;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("drivers")
public class DriverService {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Driver> getDriver() {
        return Persistence.getInstance().getDriver();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Boolean addDriver(Driver driver) {
        return Persistence.getInstance().addDriver(driver);
    }

    @POST
    @Path("{name}/cars")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Boolean addCarToDriver(@PathParam("name") String name, Car car) {
        Optional<Driver> driver = Persistence.getInstance().getDriver(name);
        if (!driver.isPresent()) {
            return null;
        }
        car.setDriver(driver.get());
        return driver.get().getCars().add(car);
    }

}
