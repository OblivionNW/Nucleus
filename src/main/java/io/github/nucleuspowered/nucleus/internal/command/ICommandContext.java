package io.github.nucleuspowered.nucleus.internal.command;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface ICommandContext<C extends CommandSource> {

    Cause getCause();

    C getCommandSource();

    int getCooldown();

    void setCooldown(int cooldown);

    double getCost();

    void setCost(double cost);

    Player getPlayer(String key, String errorKey) throws NoSuchElementException;

    User getUser(String key, String errorKey) throws NoSuchElementException;

    <T> Optional<T> getOne(String name, Class<T> clazz);

    <T> Collection<T> getAll(String name, Class<T> clazz);

    <T> T requireOne(String name, Class<T> clazz);

    INucleusServiceCollection getServiceCollection();

    ICommandResult successResult();

    ICommandResult failResult();

    ICommandResult errorResult(String key, Text... args);

    interface Mutable<C extends CommandSource> extends ICommandContext<C> {

        <T> void put(String name, Class<T> clazz, T obj);

        <T> void putAll(String name, Class<T> clazz, Collection<? extends T> obj);

    }

}
