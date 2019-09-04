/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.control;

import io.github.nucleuspowered.nucleus.internal.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.internal.command.annotation.NucleusCommand;

public final class CommandMetadata {

    private final String moduleid;
    private final String modulename;
    private final NucleusCommand annotation;
    private final Class<? extends ICommandExecutor<?>> executor;
    private final String commandKey;

    public CommandMetadata(String moduleid, String modulename, NucleusCommand annotation, Class<? extends ICommandExecutor<?>> executor) {
        this.moduleid = moduleid;
        this.modulename = modulename;
        this.annotation = annotation;
        this.executor = executor;
        this.commandKey = annotation.value()[0].replace(' ', '.');
    }

    public String getModuleid() {
        return this.moduleid;
    }

    public String getModulename() {
        return this.modulename;
    }

    public NucleusCommand getCommandAnnotation() {
        return this.annotation;
    }

    public String getCommandKey() {
        return this.commandKey;
    }

    public String[] getAliases() {
        return this.annotation.value();
    }

    public Class<? extends ICommandExecutor<?>> getExecutor() {
        return this.executor;
    }
}
