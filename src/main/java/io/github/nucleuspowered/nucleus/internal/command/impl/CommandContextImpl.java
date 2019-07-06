package io.github.nucleuspowered.nucleus.internal.command.impl;

import io.github.nucleuspowered.nucleus.internal.command.ICommandContext;
import io.github.nucleuspowered.nucleus.internal.command.ICommandResult;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class CommandContextImpl<P extends CommandSource> implements ICommandContext.Mutable<P> {

    private final INucleusServiceCollection serviceCollection;
    private double cost = 0;
    private int cooldown = 0;
    private final Cause cause;
    private final CommandContext context;
    private final P source;

    CommandContextImpl(Cause cause, CommandContext context, INucleusServiceCollection serviceCollection, P source) {
        this.cause = cause;
        this.context = context;
        this.source = source;
        this.cost = 0;
        this.cooldown = 0;
        this.serviceCollection = serviceCollection;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public P getCommandSource() {
        return this.source;
    }

    @Override
    public <T> Optional<T> getOne(String name, Class<T> clazz) {
        return this.context.getOne(name);
    }

    @Override
    public <T> Collection<T> getAll(String name, Class<T> clazz) {
        return this.context.getAll(name);
    }

    @Override
    public <T> T requireOne(String name, Class<T> clazz) {
        return this.context.requireOne(name);
    }

    @Override
    public Player getPlayer(String key, String errorKey) throws NoSuchElementException {
        return getOne(key, Player.class).orElseGet(() -> getIfPlayer(errorKey));
    }

    @Override
    public User getUser(String key, String errorKey) throws NoSuchElementException {
        return getOne(key, User.class).orElseGet(() -> getIfPlayer(errorKey));
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(cooldown, 0);
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public void setCost(double cost) {
        this.cost = Math.max(cost, 0);
    }

    @Override
    public <T> void put(String name, Class<T> clazz, T obj) {
        this.context.putArg(name, obj);
    }

    @Override
    public <T> void putAll(String name, Class<T> clazz, Collection<? extends T> obj) {
        for (T o : obj) {
            this.context.putArg(name, o);
        }
    }

    @Override
    public ICommandResult successResult() {
        return CommandResultImpl.SUCCESS;
    }

    @Override
    public ICommandResult failResult() {
        return CommandResultImpl.FAILURE;
    }

    @Override
    public ICommandResult errorResult(String key, Text... args) {
        return new CommandResultImpl(false, key, args);
    }

    @Override
    public INucleusServiceCollection getServiceCollection() {
        return this.serviceCollection;
    }

    protected abstract Player getIfPlayer(String key) throws NoSuchElementException;

    public static class Any extends CommandContextImpl<CommandSource> {

        protected Any(Cause cause, CommandContext context, INucleusServiceCollection serviceCollection, CommandSource target) {
            super(cause, context, serviceCollection, target);
        }

        @Override
        protected Player getIfPlayer(String key) throws NoSuchElementException {
            if (getCommandSource() instanceof Player) {
                return (Player) getCommandSource();
            }

            throw new NoSuchElementException(
                    this.getServiceCollection().messageProvider().getMessageFor(getCommandSource(), key).toPlain()
            );
        }
    }

    public static class PlayerSource extends CommandContextImpl<Player> {

        protected PlayerSource(Cause cause, CommandContext context, INucleusServiceCollection serviceCollection, Player source) {
            super(cause, context, serviceCollection, source);
        }

        @Override
        protected Player getIfPlayer(String key) {
            return getCommandSource();
        }
    }

}
