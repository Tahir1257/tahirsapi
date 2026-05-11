package com.smartcampus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, Sensor> getSensorsMap() {
        return sensors;
    }

    // POST create sensor
    @POST
    public Response createSensor(Sensor sensor) {

        if (sensor == null || sensor.getId() == null || sensor.getRoomId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid sensor data")
                    .build();
        }

        // Check if room exists(high marks section)
        if (!RoomResource.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room does not exist"); //part 5 exception handling
        }

        sensors.put(sensor.getId(), sensor);

        // Link sensor to room
        Room room = RoomResource.rooms.get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // GET all sensors



    @GET
    public Collection<Sensor> getSensors(
            @QueryParam("roomId") String roomId,
            @QueryParam("type") String type
    ) {

        List<Sensor> result = new ArrayList<>();

        for (Sensor s : sensors.values()) {

            boolean matches = true;

            // Check roomId
            if (roomId != null && s.getRoomId() != null &&
                    !roomId.equals(s.getRoomId())) {
                matches = false;
            }

            // Check type (SAFE)
            if (type != null) {
                if (s.getType() == null ||
                        !type.equalsIgnoreCase(s.getType())) {
                    matches = false;
                }
            }

            if (matches) {
                result.add(s);
            }
        }

        return result;
    }
    // GET sensor by ID
    @GET
    @Path("/{id}")
    public Response getSensor(@PathParam("id") String id) {

        Sensor sensor = sensors.get(id);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sensor).build();
    }

    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }


}