/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.impl;

import com.google.common.collect.ImmutableList;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.command.ICommandContext;
import io.github.nucleuspowered.nucleus.internal.command.ICommandResult;
import io.github.nucleuspowered.nucleus.internal.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.internal.command.requirements.CommandModifiers;
import io.github.nucleuspowered.nucleus.internal.command.requirements.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.storage.util.ThrownSupplier;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CommandContextImpl<P extends CommandSource> implements ICommandContext.Mutable<P> {

    private final INucleusServiceCollection serviceCollection;
    private double cost = 0;
    private int cooldown = 0;
    private final Cause cause;
    private final CommandContext context;
    private final ThrownSupplier<P, CommandException> source;
    private final Set<ICommandModifier> modifiers = new HashSet<>();
    private final ArrayList<Consumer<P>> failActions = new ArrayList<>();

    CommandContextImpl(Cause cause,
            CommandContext context,
            INucleusServiceCollection serviceCollection,
            ThrownSupplier<P, CommandException> source,
            CommandControl control,
            CommandModifiersConfig modifiersConfig) throws CommandException {
        this.cause = cause;
        this.context = context;
        this.source = source;
        this.cost =
                Util.getDoubleOptionFromSubject(source.get(), String.format("nucleus.%s.cost", control.getCommand().replace(" ", ".")))
                        .orElse(modifiersConfig.getCost());
        this.cooldown =
                Util.getIntOptionFromSubject(source.get(), String.format("nucleus.%s.cooldown", control.getCommand().replace(" ", ".")))
                        .orElse(modifiersConfig.getCooldown());
        this.serviceCollection = serviceCollection;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public P getCommandSource() throws CommandException {
        return this.source.get();
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
        return getOne(key, Player.class).orElseGet(() -> {
            try {
                return getIfPlayer(errorKey);
            } catch (CommandException e) {
                throw new NoSuchElementException();
            }
        });
    }

    @Override
    public User getUser(String key, String errorKey) throws NoSuchElementException {
        return getOne(key, User.class).orElseGet(() -> {
            try {
                return getIfPlayer(errorKey);
            } catch (CommandException e) {
                throw new NoSuchElementException();
            }
        });
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

    protected abstract Player getIfPlayer(String key) throws NoSuchElementException, CommandException;

    @Override public Collection<ICommandModifier> modifiers() {
        return ImmutableList.copyOf(this.modifiers);
    }

    @Override public void applyModifier(ICommandModifier modifier) {
        this.modifiers.add(modifier);
    }

    @Override public Collection<Consumer<P>> failActions() {
        return ImmutableList.copyOf(this.failActions);
    }

    @Override public void addFailAction(Consumer<P> action) {
        this.failActions.add(action);
    }

    public static class Any extends CommandContextImpl<CommandSource> {

        public Any(Cause cause, CommandContext context, INucleusServiceCollection serviceCollection, CommandSource target,
                CommandControl control, CommandModifiersConfig modifiersConfig) throws CommandException {
            super(cause, context, serviceCollection, () -> target, control, modifiersConfig);
        }

        @Override
        protected Player getIfPlayer(String key) throws NoSuchElementException, CommandException {
            if (getCommandSource() instanceof Player) {
                return (Player) getCommandSource();
            }

            throw new NoSuchElementException(
                    this.getServiceCollection().messageProvider().getMessageFor(getCommandSource(), key).toPlain()
            );
        }
    }

    public static class PlayerSource extends CommandContextImpl<Player> {

        public PlayerSource(Cause cause, CommandContext context, INucleusServiceCollection serviceCollection,
                ThrownSupplier<Player, CommandException> source,
                CommandControl control, CommandModifiersConfig modifiersConfig) throws CommandException {
            super(cause, context, serviceCollection, source, control, modifiersConfig);
        }

        @Override
        protected Player getIfPlayer(String key) throws CommandException {
            return getCommandSource();
        }
    }

}
