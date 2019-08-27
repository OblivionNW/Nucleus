package io.github.nucleuspowered.nucleus.guice;

import com.google.inject.AbstractModule;

/**
 * The base module that Nucleus will use to construct its basic services.
 */
public class NucleusInjectorModule extends AbstractModule {

    public static final NucleusInjectorModule INSTANCE = new NucleusInjectorModule();

    private NucleusInjectorModule() {

    }

    @Override protected void configure() { }
}
