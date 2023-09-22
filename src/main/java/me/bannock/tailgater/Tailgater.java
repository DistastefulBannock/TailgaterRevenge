package me.bannock.tailgater;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import me.bannock.tailgater.configuration.PiJsonConfigurationManagerImpl;
import me.bannock.tailgater.pi.GpioManager;
import me.bannock.tailgater.sensors.SensorException;
import me.bannock.tailgater.sensors.distance.DistanceSensorService;
import me.bannock.tailgater.sensors.distance.DistanceType;

import java.text.DecimalFormat;
import java.util.function.Consumer;

public class Tailgater {

    private final GpioManager gpioManager;
    private final DistanceSensorService distanceSensorService;

    @Inject
    public Tailgater(GpioManager gpioManager, DistanceSensorService distanceSensorService){
        this.gpioManager = gpioManager;
        this.distanceSensorService = distanceSensorService;
    }

    /**
     * Starts the application
     */
    private void start(){
        String output = "";
        while(true){
            try {
                double cm = Double.min(distanceSensorService.getDistanceReading(DistanceType.CENTIMETERS), 3621);
                double in = Double.min(distanceSensorService.getDistanceReading(DistanceType.INCHES), 3621);
                double m = Double.min(distanceSensorService.getDistanceReading(DistanceType.METERS), 3621);
                double ft = Double.min(distanceSensorService.getDistanceReading(DistanceType.FEET), 3621);
                DecimalFormat dec = new DecimalFormat("0.00");
                System.out.print("\r" + output.replaceAll(".", " ") + "\r");
                output = "cm: " + dec.format(cm) + "; in: " + dec.format(in) + "; m: " + dec.format(m) + "; ft: " + dec.format(ft);
                System.out.print(output);
            } catch (SensorException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Guice.createInjector(new TailgaterModule()).getInstance(Tailgater.class).start();
    }

}
