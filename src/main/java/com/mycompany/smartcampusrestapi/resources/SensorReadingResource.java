/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusrestapi.resources;

import com.mycompany.smartcampus.exception.ResourceNotFoundException;
import com.mycompany.smartcampus.exception.SensorUnavailableException;
import com.mycompany.smartcampus.model.Sensor;
import com.mycompany.smartcampus.model.SensorReading;
import com.mycompany.smartcampus.repo.MockDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author binuripiyathma
 */

public class SensorReadingResource {

    private String sensorId;

    // Shared storage
    private static Map<String, List<SensorReading>> readings = MockDatabase.READINGS;
    private static Map<String, Sensor> sensors = MockDatabase.SENSORS;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /sensors/{id}/readings
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {

        if (!sensors.containsKey(sensorId)) {
            throw new ResourceNotFoundException("Sensor not found");
        }

        List<SensorReading> readings
                = SensorReadingResource.readings.getOrDefault(sensorId, new ArrayList<>());

        return Response.ok(readings).build();
    }

    // POST /sensors/{id}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        Sensor sensor = sensors.get(sensorId);

        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found");
        }

        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance");
        }

        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        // computeIfAbsent with simple logic
        List<SensorReading> sensorReadings = readings.get(sensorId);

        if (sensorReadings == null) {
            sensorReadings = new ArrayList<>();
            readings.put(sensorId, sensorReadings);
        }

        sensorReadings.add(reading);

        // SIDE EFFECT (IMPORTANT)
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
