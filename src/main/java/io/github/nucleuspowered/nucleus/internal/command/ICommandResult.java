/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.internal.command.impl.CommandResultImpl;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public interface ICommandResult {

    static ICommandResult success() {
        return CommandResultImpl.SUCCESS;
    }

    static ICommandResult fail() {
        return CommandResultImpl.FAILURE;
    }

    static ICommandResult error(String key, Text... args) {
        return new CommandResultImpl(false, key, args);
    }

    boolean isSuccess();

    boolean isWillContinue();

    Optional<Text> getErrorMessage(CommandSource source);

}
