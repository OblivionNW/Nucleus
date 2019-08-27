/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.guice;

import com.google.inject.AbstractModule;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The base module that Nucleus will use to construct its basic services.
 */
public class NucleusInjectorModule extends AbstractModule {

    private final Supplier<Path> dataDirectory;
    private final Path configDirectory;

    public NucleusInjectorModule(Supplier<Path> dataDirectory, Path configDirectory) {
        this.dataDirectory = dataDirectory;
        this.configDirectory = configDirectory;
    }

    @Override protected void configure() {
        bind(Path.class).annotatedWith(DataDirectory.class).toProvider(this.dataDirectory::get);
        bind(Path.class).annotatedWith(ConfigDirectory.class).toInstance(this.configDirectory);
    }
}
