package me.bannock.tailgater;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import me.bannock.tailgater.configuration.ConfigurationManager;
import me.bannock.tailgater.configuration.PiJsonConfigurationManagerImpl;
import me.bannock.tailgater.pi.GpioManager;
import me.bannock.tailgater.pi.GpioManagerImpl;
import me.bannock.tailgater.sensors.distance.DistanceSensorService;
import me.bannock.tailgater.sensors.distance.HCSR04DistanceServiceImpl;

public class TailgaterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigurationManager.class).toInstance(new PiJsonConfigurationManagerImpl()
                .setConfigurationDefault(ConfigKeys.HC_SR04_TRIGGER_PIN, RaspiPin.GPIO_25)
                .setConfigurationDefault(ConfigKeys.HC_SR04_ECHO_PIN, RaspiPin.GPIO_27)
                .loadConfiguration()
        );
        bind(GpioManager.class).to(GpioManagerImpl.class);
        bind(DistanceSensorService.class).to(HCSR04DistanceServiceImpl.class);
    }

}
