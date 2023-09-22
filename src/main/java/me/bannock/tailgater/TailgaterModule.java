package me.bannock.tailgater;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.pi4j.io.gpio.Pin;
import me.bannock.tailgater.configuration.ConfigurationManager;
import me.bannock.tailgater.pi.GpioManager;
import me.bannock.tailgater.pi.GpioManagerImpl;
import me.bannock.tailgater.sensors.distance.DistanceSensorService;
import me.bannock.tailgater.sensors.distance.HCSR04DistanceServiceImpl;

public class TailgaterModule extends AbstractModule {

    private final ConfigurationManager configurationManager;

    public TailgaterModule(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
    }

    @Override
    protected void configure() {
        bind(ConfigurationManager.class).toInstance(configurationManager);
        bind(GpioManager.class).to(GpioManagerImpl.class);
        bind(DistanceSensorService.class).to(HCSR04DistanceServiceImpl.class);
    }

}
