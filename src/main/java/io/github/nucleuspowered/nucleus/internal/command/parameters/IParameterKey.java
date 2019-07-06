package io.github.nucleuspowered.nucleus.internal.command.parameters;

public interface IParameterKey<T> {

    String name();

    Class<T> type();

}
