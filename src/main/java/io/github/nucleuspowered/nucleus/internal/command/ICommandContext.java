/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.internal.command.requirements.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public interface ICommandContext<C extends CommandSource> {

    Cause getCause();

    C getCommandSource() throws CommandException;

    int getCooldown();

    void setCooldown(int cooldown);

    double getCost();

    void setCost(double cost);

    int getWarmup();

    /**
     * Sets the warmup in seconds.
     *
     * @param warmup The warmup in seconds
     */
    void setWarmup(int warmup);

    Player getPlayer(String key, String errorKey) throws NoSuchElementException, CommandException;

    Player getCommandSourceAsPlayerUnchecked();

    User getUser(String key, String errorKey) throws NoSuchElementException, CommandException;

    <T> Optional<T> getOne(String name, Class<T> clazz);

    <T> Collection<T> getAll(String name, Class<T> clazz);

    <T> T requireOne(String name, Class<T> clazz);

    INucleusServiceCollection getServiceCollection();

    ICommandResult successResult();

    ICommandResult failResult();

    ICommandResult errorResult(String key, Text... args);

    Collection<ICommandModifier> modifiers();

    Collection<Consumer<C>> failActions();

    interface Mutable<C extends CommandSource> extends ICommandContext<C> {

        CommandSource getCommandSourceUnchecked();

        <T> void put(String name, Class<T> clazz, T obj);

        <T> void putAll(String name, Class<T> clazz, Collection<? extends T> obj);

        void applyModifier(ICommandModifier modifier);

        void addFailAction(Consumer<C> action);

    }

}
