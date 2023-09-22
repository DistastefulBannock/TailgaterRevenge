package me.bannock.tailgater.sensors.distance;

import me.bannock.tailgater.sensors.SensorException;

public interface DistanceSensorService {

    /**
     * Gets the distance reading from the sensor, may momentarily block thread
     * @param type The type of distance to return
     * @return The distance reading in the specified type. Or Double.MAX_VALUE if the reading failed (assumed object out of range)
     * @throws SensorException If there was an error reading the sensor
     */
    double getDistanceReading(DistanceType type) throws SensorException;

}
