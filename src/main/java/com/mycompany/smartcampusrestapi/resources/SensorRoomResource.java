/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusrestapi.resources;

import com.mycompany.smartcampus.exception.ResourceNotFoundException;
import com.mycompany.smartcampus.exception.RoomNotEmptyException;
import com.mycompany.smartcampus.model.Room;
import com.mycompany.smartcampus.model.Sensor;
import com.mycompany.smartcampus.repo.MockDatabase;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author binuripiyathma
 */

@Path("/rooms")
public class SensorRoomResource {
    
    private static Map<String, Room> rooms = MockDatabase.ROOMS;
    private static Map<String, Sensor> sensors = MockDatabase.SENSORS;
    private Object sensorId;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok(rooms)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        String id = UUID.randomUUID().toString();
        room.setId(id);

        if (room.getSensorIds()!= null) {
            for (String sensorId : room.getSensorIds()) {
                if (!sensors.containsKey(sensorId)) {
                    
                    throw new ResourceNotFoundException("Sensor ID not found: " + sensorId);
                }
            }
        }    
            
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        rooms.put(id, room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room not found");
        }

        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room not found");
        }

        // BUSINESS RULE
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room has active sensors and cannot be deleted");
        }

        rooms.remove(roomId);

        return Response.ok("Room deleted successfully").build();
    }
}
