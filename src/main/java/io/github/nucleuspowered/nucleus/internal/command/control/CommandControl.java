package io.github.nucleuspowered.nucleus.internal.command.control;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.internal.command.CommandTypeFlags;
import io.github.nucleuspowered.nucleus.internal.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.internal.command.NucleusCommandException;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
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
import org.spongepowered.api.text.Text;
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
    private final ImmutableList<CommandTypeFlags> flags;

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
        if (executor == null || executor.arguments().length == 0) {
            this.element = GenericArguments.none();
        } else if (executor.arguments().length == 1){
            this.element = executor.arguments()[0];
        } else {
            this.element = GenericArguments.seq(executor.arguments());
        }

        this.aliases = aliasesToRegister;
        this.flags = ImmutableList.copyOf(meta.getCommandAnnotation().commandFlags());
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
        return process(source, this.aliases.get(0), arguments, args);
    }

    public CommandResult process(@NonNull CommandSource source, @NonNull String command, @NonNull String arguments, CommandArgs args) throws CommandException {
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
                        return ((CommandControl) callable).process(source, command + " " + next, arguments, args);
                    }

                    return callable.process(source, arguments);
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

        // Can we run this command? Exception will be thrown if not.
        for (CommandTypeFlags x : this.flags) {
            x.testRequirement(source, this.serviceCollection);
        }

        // Do we have permission?
        if (!testPermission(source)) {
            throw new CommandPermissionException();
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

        return null;
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

    Collection<CommandCallable> getSubcommands() {
        return this.subcommands.values();
    }

    String getCommand() {
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

}
