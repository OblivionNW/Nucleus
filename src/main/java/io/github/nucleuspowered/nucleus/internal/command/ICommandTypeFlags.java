package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public interface ICommandTypeFlags {

    /**
     * Tests to see if the state fulfills this requirement.
     *
     * <p>This will return an empty optional if the requirement is met, or
     * a {@link Text} object otherwise, explaining the problem.</p>
     */
    default Optional<Text> testRequirement(CommandSource source, INucleusServiceCollection serviceCollection) {
        return Optional.empty();
    }

}
