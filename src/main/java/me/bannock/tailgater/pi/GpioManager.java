package me.bannock.tailgater.pi;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

import java.util.function.Consumer;

public interface GpioManager {

    /**
     * Set the state of a pin
     * @param pin The pin to set
     * @param state The state to set the pin to
     */
    void setPinState(Pin pin, PinState state);

    /**
     * Adds a listener for a pin
     * @param pin The pin to add the listener to
     * @param listener The listener to add
     */
    void addPinListener(Pin pin, Consumer<GpioPinDigitalStateChangeEvent> listener);

    /**
     * Removes a listener for a pin
     * @param listener The listener to remove
     * @return true if the listener was removed, false otherwise
     */
    boolean removePinListener(Pin pin, Consumer<GpioPinDigitalStateChangeEvent> listener);

}
