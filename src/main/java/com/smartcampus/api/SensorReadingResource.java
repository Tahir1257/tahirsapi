package com.smartcampus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class SensorReadingResource {

    private static Map<String, List<SensorReading>> readingsMap = new HashMap<>();

    private String sensorId;


    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET readings for a sensor
    @GET
    public List<SensorReading> getReadings() {

        return readingsMap.getOrDefault(sensorId, new ArrayList<>());
    }

    // POST new reading
    @POST
    public Response addReading(SensorReading reading) {

        // 🔴 STEP 1 — Get sensor
        Sensor sensor = SensorResource.getSensorsMap().get(sensorId);

        // 🔴 STEP 2 — Check if sensor exists (optional but good)
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found")
                    .build();
        }

        // 🔴 STEP 3 — CHECK MAINTENANCE STATUS (THIS IS THE IMPORTANT PART)
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance");
        }

        // 🟢 STEP 4 — Add reading
        readingsMap.putIfAbsent(sensorId, new ArrayList<>());
        readingsMap.get(sensorId).add(reading);

        // 🟢 STEP 5 — Update current value
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

}