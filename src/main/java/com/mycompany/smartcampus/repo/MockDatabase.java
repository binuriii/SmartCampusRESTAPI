/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.repo;

import com.mycompany.smartcampus.model.Room;
import com.mycompany.smartcampus.model.Sensor;
import com.mycompany.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author binuripiyathma
 */

public class MockDatabase {
    // Rooms
    public static final Map<String, Room> ROOMS = new HashMap<>();

    // Sensors
    public static final Map<String, Sensor> SENSORS = new HashMap<>();

    // Readings (sensorId -> list of readings)
    public static final Map<String, List<SensorReading>> READINGS = new HashMap<>();

    static {

        // ===== Create Rooms =====
        Room room1 = new Room(UUID.randomUUID().toString(), "Lab 1", 10);
        Room room2 = new Room(UUID.randomUUID().toString(), "Lecture Hall", 100);

        ROOMS.put(room1.getId(), room1);
        ROOMS.put(room2.getId(), room2);

        // ===== Create Sensors =====
        Sensor sensor1 = new Sensor(UUID.randomUUID().toString(), "CO2", "WORKING", 400.0, room1.getId());
        Sensor sensor2 = new Sensor(UUID.randomUUID().toString(), "TEMP", "MAINTANANCE", 25.0, room2.getId());

        SENSORS.put(sensor1.getId(), sensor1);
        SENSORS.put(sensor2.getId(), sensor2);

        // 🔗 Link sensors to rooms
        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        // ===== Create Readings =====
        List<SensorReading> sensor1Readings = new ArrayList<>();
        sensor1Readings.add(new SensorReading(
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                400.0
        ));

        READINGS.put(sensor1.getId(), sensor1Readings);

        List<SensorReading> sensor2Readings = new ArrayList<>();
        sensor2Readings.add(new SensorReading(
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                25.0
        ));

        READINGS.put(sensor2.getId(), sensor2Readings);
    }
}
