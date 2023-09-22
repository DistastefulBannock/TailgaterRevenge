package me.bannock.tailgater.sensors.distance;

import com.google.inject.Inject;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import me.bannock.tailgater.ConfigKeys;
import me.bannock.tailgater.configuration.ConfigurationManager;
import me.bannock.tailgater.pi.GpioManager;
import me.bannock.tailgater.sensors.SensorException;

import java.util.function.Consumer;

public class HCSR04DistanceServiceImpl implements DistanceSensorService {

    @Inject
    private GpioManager gpioManager;

    @Inject
    public HCSR04DistanceServiceImpl(GpioManager gpioManager, ConfigurationManager configManager) {
        this.triggerPin = configManager.getConfigurationValue(ConfigKeys.HC_SR04_TRIGGER_PIN, Pin.class);
        this.echoPin = configManager.getConfigurationValue(ConfigKeys.HC_SR04_ECHO_PIN, Pin.class);
        gpioManager.addPinListener(echoPin, echoListener);
    }
    private final Pin triggerPin, echoPin;

    private boolean waitingForEcho = false;
    private long lastEchoNanos = 0;

    private long finalPingNanos = -1L;
    private Object objectUsedForThreadWaiting = new Object();

    @Override
    public double getDistanceReading(DistanceType type) throws SensorException {
        if (waitingForEcho)
            throw new SensorException("Waiting for echo", "Cannot pull trigger while waiting for echo", false);

        // Make sure we start waiting for echo before we pull the trigger
        waitingForEcho = true;

        // Set defaults for variables
        lastEchoNanos = 0;
        finalPingNanos = -1L;

        // Pull trigger
        gpioManager.setPinState(triggerPin, PinState.HIGH);
        try{
            Thread.sleep(1);
        }catch (InterruptedException ignored){}
        gpioManager.setPinState(triggerPin, PinState.LOW);

        // Wait for reading
        synchronized (objectUsedForThreadWaiting){
            try{
                objectUsedForThreadWaiting.wait(150);
            }catch (InterruptedException ignored){
                throw new SensorException("Echo reading failed", "Interrupted while waiting for echo reading", false);
            }

            // Reset waiting for echo
            waitingForEcho = false;

            // This variable will be -1L if we timed out
            if (finalPingNanos == -1L)
                return Double.MAX_VALUE;

            // TODO: Calculate distance based on ping nanos and return
            switch(type){
                case CENTIMETERS: return finalPingNanos / 58000D;
                case INCHES: return finalPingNanos / 148000D;
                case METERS: return finalPingNanos / 5800000D;
                case FEET: return finalPingNanos / 148000D / 12D;
            }

        }

        return 0;
    }

    private Consumer<GpioPinDigitalStateChangeEvent> echoListener = event -> {
        if (!waitingForEcho)
            return;

        if (event.getState() == PinState.HIGH){
            lastEchoNanos = System.nanoTime();
        }else{
            long pingNanos = System.nanoTime() - lastEchoNanos;
            synchronized (objectUsedForThreadWaiting){
                finalPingNanos = pingNanos;
                objectUsedForThreadWaiting.notifyAll();
            }
        }
    };

}
