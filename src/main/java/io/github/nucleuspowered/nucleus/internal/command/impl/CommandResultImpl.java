/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.impl;

import io.github.nucleuspowered.nucleus.internal.command.ICommandResult;
import io.github.nucleuspowered.nucleus.internal.traits.MessageProviderTrait;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandResultImpl implements ICommandResult, MessageProviderTrait {

    public static final ICommandResult SUCCESS = new CommandResultImpl(true, null, null);

    public static final ICommandResult WILL_CONTINUE = new CommandResultImpl(false, null, null);

    public static final ICommandResult FAILURE = new CommandResultImpl(false, null, null);

    @Nullable private final String key;
    @Nullable private final Text[] args;
    private final boolean success;
    private final boolean willContinue;

    public CommandResultImpl(boolean success, @Nullable String key, @Nullable Text[] args) {
        this(success, false, key, args);
    }

    private CommandResultImpl(boolean success, boolean willContinue, @Nullable String key, @Nullable Text[] args) {
        this.key = key;
        this.args = args;
        this.success = success;
        this.willContinue = willContinue;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public boolean isWillContinue() {
        return this.willContinue;
    }

    @Override
    public Optional<Text> getErrorMessage(CommandSource source) {
        if (this.key == null) {
            return Optional.empty();
        }

        return Optional.of(getMessageFor(source, this.key, this.args));
    }

}
