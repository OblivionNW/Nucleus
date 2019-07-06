package io.github.nucleuspowered.nucleus.internal.command.control;

import io.github.nucleuspowered.nucleus.internal.command.ICommandExecutor;

import java.util.HashSet;
import java.util.Set;

public class CommandContstructor {

    private final Set<ICommandExecutor> executors = new HashSet<>();

    public void add(ICommandExecutor executor) {
        this.executors.add(executor);
    }

    public void construct() {

    }

}
