/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.control;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.command.ICommandContext;
import io.github.nucleuspowered.nucleus.internal.command.ICommandResult;
import io.github.nucleuspowered.nucleus.internal.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.internal.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.internal.command.impl.CommandContextImpl;
import io.github.nucleuspowered.nucleus.internal.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.internal.command.NucleusCommandException;
import io.github.nucleuspowered.nucleus.internal.command.requirements.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class CommandControl implements CommandCallable {

    private static final InputTokenizer tokeniser = InputTokenizer.quotedStrings(false);

    private final INucleusServiceCollection serviceCollection;
    private final ImmutableList<String> basicPermission;
    private final CommandMetadata metadata;
    @Nullable private final ICommandExecutor<? extends CommandSource> executor;
    private final Class<? extends CommandSource> sourceType;
    private final UsageCommand usageCommand;

    private final SortedMap<String, CommandCallable> subcommands = new TreeMap<>();
    private final CommandElement element;
    private final List<String> aliases;
    private final ImmutableList<CommandModifier> flags;
    private final CommandModifiersConfig commandModifiersConfig = new CommandModifiersConfig();

    private final String command;

    public CommandControl(
            @Nullable ICommandExecutor<? extends CommandSource> executor,
            CommandMetadata meta,
            String command,
            INucleusServiceCollection serviceCollection,
            List<String> aliasesToRegister) {
        this.executor = executor;
        this.metadata = meta;
        this.basicPermission = ImmutableList.copyOf(meta.getCommandAnnotation().basePermission());
        this.serviceCollection = serviceCollection;
        if (executor == null || executor.parameters().length == 0) {
            this.element = GenericArguments.none();
        } else if (executor.parameters().length == 1) {
            this.element = executor.parameters()[0];
        } else {
            this.element = GenericArguments.seq(executor.parameters());
        }

        this.aliases = aliasesToRegister;
        this.flags = ImmutableList.copyOf(meta.getCommandAnnotation().modifiers());
        Class<? extends CommandSource> c = CommandSource.class;
        if (this.executor != null) {
            for (Type type : this.executor.getClass().getGenericInterfaces()) {
                if (type.getTypeName().startsWith(ICommandExecutor.class.getName()) && type instanceof ParameterizedType) {
                    //noinspection unchecked
                    c = (Class<? extends CommandSource>) (((ParameterizedType) type).getActualTypeArguments()[0]);
                    break;
                }
            }
        }

        this.sourceType = c;
        this.usageCommand = new UsageCommand(this, this.serviceCollection);
        this.command = command;
    }

    @Override
    @NonNull
    public CommandResult process(@NonNull CommandSource source, @NonNull String arguments) throws CommandException {
        // do we have a subcommand?
        CommandArgs args = new CommandArgs(arguments, tokeniser.tokenize(arguments, false));
        return process(Sponge.getCauseStackManager().getCurrentCause(), source, this.aliases.get(0), arguments, args);
    }

    public CommandResult process(@Nonnull Cause cause, @NonNull CommandSource source, @NonNull String command, @NonNull String arguments,
            CommandArgs args) throws CommandException {
        // Phase one: child command processing. Keep track of all thrown arguments.
        List<Tuple<String, CommandException>> thrown = Lists.newArrayList();
        final CommandContext context = new CommandContext();
        final CommandArgs.Snapshot state = args.getSnapshot();

        if (args.hasNext()) {
            String firstArg = args.peek();

            // If this works, then we're A-OK.
            CommandCallable callable = this.subcommands.get(firstArg);
            if (callable != null) {
                String next = args.next();
                try {

                    if (callable instanceof CommandControl) {
                        return ((CommandControl) callable).process(cause, source, command + " " + next, arguments, args);
                    }

                    return callable.process(source, args.getRaw().substring(args.getRawPosition()).trim());
                } catch (NucleusCommandException e) {
                    // Didn't work out. Let's move on.
                    thrown.addAll(e.getExceptions());
                    if (!e.isAllowFallback()) {
                        throw e;
                    }
                } catch (CommandException e) {
                    // If the Exception is _not_ of right type, wrap it and add it. This shouldn't happen though.
                    thrown.add(Tuple.of(command + " " + next, e));
                } finally {
                    args.applySnapshot(state);
                }
            }
        }

        // Ensure we're the correct type.
        checkSourceType(source);

        // Create the ICommandContext
        // TODO: Abstract this away
        ICommandContext<? extends CommandSource> contextSource;
        if (source instanceof Player) {
            contextSource = new CommandContextImpl.PlayerSource(
                    cause,
                    context,
                    this.serviceCollection,
                    () -> Sponge.getServer().getPlayer(((Player) source).getUniqueId()).orElseThrow(() -> new CommandException(
                            Text.of("Player is no longer available.")
                    )),
                    this,
                    this.commandModifiersConfig
            );
        } else {
            contextSource = new CommandContextImpl.Any(
                    cause,
                    context,
                    this.serviceCollection,
                    source,
                    this,
                    this.commandModifiersConfig
            );
        }

        try {
            // Get the modifiers for this command/source combination.
            Collection<ICommandModifier> modifiers = selectAppropriateModifiers(source);

            // Do we have permission?
            if (!testPermission(source)) {
                throw new CommandPermissionException();
            }

            // Can we run this command? Exception will be thrown if not.
            for (ICommandModifier x : modifiers) {
                Optional<Text> req = x.testRequirement(contextSource, this, this.serviceCollection);
                if (req.isPresent()) {
                    // Nope, we're out
                    throw new CommandException(req.get());
                }
            }

            if (this.executor == null) {
                if (thrown.isEmpty()) {
                    // OK, we just process the usage command instead.
                    return this.usageCommand.process(source, "", args.nextIfPresent().map(String::toLowerCase).orElse(null));
                } else {
                    throw new NucleusCommandException(thrown);
                }
            }

            // execution
            //noinspection unchecked
            Optional<ICommandResult> result = this.executor.preExecute((ICommandContext) contextSource);
            if (result.isPresent()) {
                // STOP.
                return onResult(source, contextSource, result.get());
            }

            // Modifiers might have something to say about it.
            for (ICommandModifier modifier : contextSource.modifiers()) {
                result = modifier.preExecute(contextSource, this, this.serviceCollection);
                if (result.isPresent()) {
                    // STOP.
                    return onResult(source, contextSource, result.get());
                }
            }

            return execute(source, contextSource);
        } catch (Exception ex) {
            // Run any fail actions.
            runFailActions(contextSource);
            throw ex;
        }
    }

    private <T extends CommandSource> void runFailActions(ICommandContext<T> contextSource) {
        T source;
        try {
            source = contextSource.getCommandSource();
            contextSource.failActions().forEach(x -> x.accept(source));
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    // Entry point for warmups.
    public void startExecute(@NonNull ICommandContext<? extends CommandSource> contextSource) {
        CommandSource source;
        try {
            source = contextSource.getCommandSource();
        } catch (CommandException ex) {
            this.serviceCollection.getLogger().warn("Could not get command source, cancelling command execution (did the player disconnect?)", ex);
            return;
        }

        try {
            execute(source, contextSource);
        } catch (CommandException ex) {
            // If we are here, then we're handling the command ourselves.
            Text message = ex.getText() == null ? Text.of(TextColors.RED, "Unknown error!") : ex.getText();
            onFail(contextSource, message);
            this.serviceCollection.getLogger().warn("Error executing command {}", this.command, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private CommandResult execute(
            CommandSource source,
            @NonNull ICommandContext<? extends CommandSource> context) throws CommandException {
        Preconditions.checkState(this.executor != null, "executor");

        // Anything else to go here?
        ICommandResult result = this.executor.execute((ICommandContext) context);
        return onResult(source, context, result);
    }

    private CommandResult onResult(CommandSource source, ICommandContext<? extends CommandSource> contextSource, ICommandResult result) throws CommandException {
        if (result.isSuccess()) {
            return onSuccess(contextSource);
        } else if (!result.isWillContinue()) {
            return onFail(contextSource, result.getErrorMessage(source).orElse(null));
        } else {
            // The command will continue later. Don't do anything.
            return CommandResult.success();
        }
    }

    private CommandResult onSuccess(ICommandContext<? extends CommandSource> source) {
        source.modifiers().forEach(x -> x.onCompletion(source, this, this.serviceCollection));
        return CommandResult.success();
    }

    private CommandResult onFail(ICommandContext<? extends CommandSource> source, @Nullable Text errorMessage) {
        // Run any fail actions.
        runFailActions(source);
        return CommandResult.empty();
    }

    private Collection<ICommandModifier> selectAppropriateModifiers(CommandSource source) {
        return this.flags.stream()
                .filter(x -> x.target().isInstance(source))
                .filter(x -> x.exemptPermission().isEmpty() || this.serviceCollection.permissionCheck().hasPermission(source, x.exemptPermission()))
                .map(CommandModifier::value)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<String> getSuggestions(@NonNull CommandSource source, @NonNull String arguments, @Nullable Location<World> targetPosition)
            throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(@NonNull CommandSource source) {
        return this.basicPermission.stream().allMatch(x -> this.serviceCollection.permissionCheck().hasPermission(source, x));
    }

    @Override
    @NonNull
    public Optional<Text> getShortDescription(@NonNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    @NonNull
    public Optional<Text> getHelp(@NonNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    @NonNull
    public Text getUsage(@NonNull final CommandSource source) {
        Text.Builder builder = Text.builder();
        String firstAlias = this.aliases.get(0);
        builder.append(this.serviceCollection.messageProvider().getMessageFor(source, "nucleus.usage.header", firstAlias))
                .append(getUsageText(source, firstAlias));
        if (!this.subcommands.isEmpty()) {
            this.subcommands.values().stream()
                    .filter(commandControl -> commandControl instanceof CommandControl && commandControl.testPermission(source))
                    .map(commandControl -> ((CommandControl) commandControl).getUsageText(source, this.aliases.get(0)))
                    .forEach(x -> {
                        builder.append(Text.NEW_LINE).append(x);
                    });
        }
        return builder.build();
    }

    public Text getUsageText(@NonNull CommandSource source, String prefix) {
        return this.serviceCollection.messageProvider().getMessageFor(source, "command.usage.bl",Text.of(prefix),
                this.element.getUsage(source));
    }

    @Nullable
    private CommandCallable getSubcommand(String subcommand, @Nullable CommandSource source) {
        CommandCallable control = this.subcommands.get(subcommand.toLowerCase());
        if (source == null || control.testPermission(source)) {
            return control;
        }

        return null;
    }

    public CommandModifiersConfig getCommandModifiersConfig() {
        return this.commandModifiersConfig;
    }

    Collection<CommandCallable> getSubcommands() {
        return this.subcommands.values();
    }

    public String getCommand() {
        return this.command;
    }

    Class<? extends CommandSource> getSourceType() {
        return this.sourceType;
    }

    CommandMetadata getMetadata() {
        return this.metadata;
    }

    boolean hasExecutor() {
        return this.executor != null;
    }


    private CommandException getExceptionFromKey(CommandSource source, String key, String... subs) {
        return new CommandException(this.serviceCollection.messageProvider().getMessageFor(source, key, subs));
    }

    private void checkSourceType(CommandSource source) throws CommandException {
        if (!this.sourceType.isInstance(source)) {
            if (this.sourceType.equals(Player.class) && !(source instanceof Player)) {
                throw getExceptionFromKey(source, "command.playeronly");
            } else if (this.sourceType.equals(ConsoleSource.class) && !(source instanceof ConsoleSource)) {
                throw getExceptionFromKey(source, "command.consoleonly");
            } else if (this.sourceType.equals(CommandBlockSource.class) && !(source instanceof CommandBlockSource)) {
                throw getExceptionFromKey(source, "command.commandblockonly");
            }

            throw getExceptionFromKey(source, "command.unknownsource");
        }
    }

}
