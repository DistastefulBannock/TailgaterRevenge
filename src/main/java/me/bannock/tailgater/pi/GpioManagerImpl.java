package me.bannock.tailgater.pi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GpioManagerImpl implements GpioManager {

    private final GpioController controller = GpioFactory.getInstance();

    private final Map<Pin, GpioPinDigitalOutput> pinOutputObjects = new HashMap<>();
    private final Map<Pin, GpioPinDigitalInput> pinInputObjects = new HashMap<>();
    private final Map<Pin, List<Consumer<GpioPinDigitalStateChangeEvent>>> listeners = new HashMap<>();

    @Override
    public void setPinState(Pin pin, PinState state) {
        // If the pin is used for input then throw exception
        if (pinInputObjects.containsKey(pin)){
            throw new IllegalArgumentException("Pin " + pin + " is used for input and cannot be controlled");
        }

        // Create new pin object if none exists
        if (!pinOutputObjects.containsKey(pin)){
            GpioPinDigitalOutput outputPin = controller.provisionDigitalOutputPin(pin, state);
            outputPin.setShutdownOptions(true, PinState.LOW); // Basic setup
            pinOutputObjects.put(pin, outputPin);
        }

        // Set the state of the pin
        pinOutputObjects.get(pin).setState(state);
    }

    @Override
    public void addPinListener(Pin pin, Consumer<GpioPinDigitalStateChangeEvent> listener) {
        // If the pin is used for output then throw exception
        if (pinOutputObjects.containsKey(pin)){
            throw new IllegalArgumentException("Pin " + pin + " is used for output and cannot be listened to");
        }

        // Add list for pin if none exists. Also create new pin object because in this state none will exist
        if (!listeners.containsKey(pin)){
            listeners.put(pin, new ArrayList<>());
            GpioPinDigitalInput inputPin = controller.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
            inputPin.setShutdownOptions(true); // Basic setup
            inputPin.addListener((GpioPinListenerDigital) this::eventController); // Add event controller method
            pinInputObjects.put(pin, inputPin);
        }

        // Add the listener to the list
        listeners.get(pin).add(listener);

    }

    @Override
    public boolean removePinListener(Pin pin, Consumer<GpioPinDigitalStateChangeEvent> listener) {
        if (!listeners.containsKey(pin))
            return false;
        return listeners.get(pin).remove(listener);
    }

    /**
     * Handles a pin state change event and sends it to the proper listeners
     * @param evt the event to handle
     */
    private void eventController(GpioPinDigitalStateChangeEvent evt){

        // Get the listeners for the pin that triggered the event
        Pin pin = evt.getPin().getPin();
        List<Consumer<GpioPinDigitalStateChangeEvent>> pinListeners = listeners.get(pin);
        if (pinListeners == null)
            return;

        // Send the event to the found listeners
        for (Consumer<GpioPinDigitalStateChangeEvent> listener : pinListeners){
            try{
                listener.accept(evt);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
