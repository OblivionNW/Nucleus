package io.github.nucleuspowered.nucleus.services.impl.moduleconfig;

import io.github.nucleuspowered.nucleus.services.IModuleConfigProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Singleton;

@Singleton
public class ModuleConfigProvider implements IModuleConfigProvider {

    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();

    @Override public <T> void registerModuleConfig(Class<T> typeOfConfig, Supplier<T> configGetter) {
        if (this.providers.containsKey(typeOfConfig)) {
            throw new IllegalStateException("Cannot register type more than once!");
        }

        this.providers.put(typeOfConfig, configGetter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getModuleConfig(Class<T> configType) throws IllegalArgumentException {
        if (this.providers.containsKey(configType)) {
            return (T) this.providers.get(configType);
        }

        throw new IllegalArgumentException("does not exist");
    }
}
