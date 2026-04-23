/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusrestapi.resources;

import com.mycompany.smartcampus.model.Room;
import com.mycompany.smartcampus.model.Sensor;
import com.mycompany.smartcampus.repo.MockDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author binuripiyathma
 */

@Path("/sensors")
public class SensorResource {

    private static Map<String, Sensor> sensors = MockDatabase.SENSORS;
    private static Map<String, Room> rooms = MockDatabase.ROOMS;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {

        // VALIDATION: room must exist
        if (sensor.getRoomId() == null || !rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room does not exist for given roomId");
        }

        String id = UUID.randomUUID().toString();
        sensor.setId(id);

        sensors.put(id, sensor);

        // Also link sensor to room
        Room room = rooms.get(sensor.getRoomId());
        room.getSensorIds().add(id);

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .type(MediaType.APPLICATION_JSON)
                .build();
    
    }
    
    // GET /sensors (with optional filter)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {

        List<Sensor> result = new ArrayList<>();

        for (Sensor sensor : sensors.values()) {
            if (type == null || sensor.getType().equalsIgnoreCase(type)) {
                result.add(sensor);
            }
        }

        return Response.ok(result).build();
    }

    

}
