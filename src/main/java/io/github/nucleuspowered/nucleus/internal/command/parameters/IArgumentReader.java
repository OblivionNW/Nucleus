package io.github.nucleuspowered.nucleus.internal.command.parameters;

import java.util.Optional;

public interface IArgumentReader {

    String next();

    Optional<String> nextIfPresent();

    String toEnd();

    String all();

}
