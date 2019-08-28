/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.internal.command.annotation.NucleusCommand;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;

import java.util.Optional;

/**
 * Defines the logic of the command.
 *
 * <p>Note that the command must also be annotated with {@link NucleusCommand}</p>
 *
 * @param <C> The type of {@link CommandSource} that can run this command.
 */
public interface ICommandExecutor<C extends CommandSource> {

    /**
     * The elements that make up the command.
     *
     * @return The elements.
     */
    default CommandElement[] parameters() {
        return new CommandElement[0];
    }

    /**
     * Executes before things like warmups execute. If a result is returned,
     * the command will not execute.
     *
     * <p>Basically, this will run before {@link #execute(ICommandContext)} and
     * has the ability to cancel it.</p>
     *
     * @param context The {@link ICommandContext}
     * @return The result, if any
     */
    default Optional<ICommandResult> preExecute(ICommandContext<? extends C> context) {
        return Optional.empty();
    }

    /**
     * The executor of the command.
     *
     * @param context The {@link ICommandContext}
     * @return The result of the command.
     */
    ICommandResult execute(ICommandContext<? extends C> context);

}