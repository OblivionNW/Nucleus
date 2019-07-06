package io.github.nucleuspowered.nucleus.internal.command.control;

import io.github.nucleuspowered.nucleus.internal.annotations.command.NucleusCommand;

public final class CommandMetadata {

    private final String moduleid;
    private final String modulename;
    private final NucleusCommand annotation;

    public CommandMetadata(String moduleid, String modulename,
            NucleusCommand annotation) {
        this.moduleid = moduleid;
        this.modulename = modulename;
        this.annotation = annotation;
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
}
