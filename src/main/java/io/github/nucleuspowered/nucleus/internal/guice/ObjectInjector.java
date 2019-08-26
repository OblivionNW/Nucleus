package io.github.nucleuspowered.nucleus.internal.guice;

import com.google.inject.AbstractModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

public class ObjectInjector extends AbstractModule {

    private final INucleusServiceCollection serviceCollection;

    public ObjectInjector(INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override protected void configure() {
        bind(INucleusServiceCollection.class).toInstance(this.serviceCollection);
    }
}
