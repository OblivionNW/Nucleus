package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import com.google.inject.ProvidedBy;
import io.github.nucleuspowered.nucleus.services.impl.moduleconfig.ModuleConfigProvider;

import java.util.function.Supplier;

/**
 * Provides config objects.
 */
@ImplementedBy(ModuleConfigProvider.class)
public interface IModuleConfigProvider {

    /**
     * Registers how to obtain a config object.
     *
     * @param typeOfConfig The type of configuration object.
     * @param configGetter The {@link Supplier} that grabs the config.
     * @param <T> The type of config
     */
    <T> void registerModuleConfig(Class<T> typeOfConfig, Supplier<T> configGetter);

    /**
     * Gets the configuration of the given type.
     *
     * @param configType The type of configuration
     * @param <T> The type
     * @return The configuration
     * @throws IllegalArgumentException If the given type is invalid
     */
    <T> T getModuleConfig(Class<T> configType) throws IllegalArgumentException;

}
