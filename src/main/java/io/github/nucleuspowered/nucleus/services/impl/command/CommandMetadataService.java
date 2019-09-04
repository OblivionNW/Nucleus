/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.PluginInfo;
import io.github.nucleuspowered.nucleus.guice.ConfigDirectory;
import io.github.nucleuspowered.nucleus.internal.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.internal.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.internal.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandMetadata;
import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.ICommandMetadataService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.IReloadableService;
import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandMetadataService implements ICommandMetadataService, Reloadable {

    private static final String ALIASES = "aliases";

    private final Logger logger;
    private final Map<String, String> commandremap = new HashMap<>();
    private final Path commandsFile;
    private boolean shouldReload = true;
    private final Map<String, CommandMetadata> commandMetadataMap = new HashMap<>();
    private final Map<CommandControl, List<String>> controlToAliases = new HashMap<>();

    private ConfigurationNode commandsConfConfigNode;
    private boolean registrationComplete = false;

    @Inject
    public CommandMetadataService(@ConfigDirectory Path configDirectory, IReloadableService reloadableService, Logger logger) {
        reloadableService.registerReloadable(this);
        this.commandsFile = configDirectory.resolve("commands.conf");
        this.logger = logger;
    }

    @Override public void registerCommand(CommandMetadata metadata) {
        Preconditions.checkState(!this.registrationComplete, "Registration has completed.");
        this.commandMetadataMap.put(metadata.getCommandKey(), metadata);
    }

    /**
     * This is where the magic happens with registering commands. We need to:
     *
     * <ol>
     *     <li>Update command.conf</li>
     *     <li>Sift through and get the aliases to register.</li>
     *     <li>Register "root" aliases</li>
     *     <li>Then subcommands... obviously.</li>
     * </ol>
     */
    @Override public void completeRegistrationPhase(final INucleusServiceCollection serviceCollection) {
        Preconditions.checkState(!this.registrationComplete, "Registration has completed.");
        this.registrationComplete = true;
        load();

        SortedMap<Integer, Map<CommandMetadata, Set<String>>> levels = new TreeMap<>();
        Map<String, CommandControl> aliases = new HashMap<>();
        Map<CommandMetadata, CommandControl> commands = new HashMap<>();

        // We need aliases out
        for (CommandMetadata metadata : this.commandMetadataMap.values()) {
            Map<String, Boolean> map = getAliasMap(metadata.getCommandKey());
            for (String alias : metadata.getAliases()) {
                map.putIfAbsent(alias, true);
            }

            for (Map.Entry<String, Boolean> m : map.entrySet()) {
                if (m.getValue()) {
                    int level = m.getKey().length() - m.getKey().replace(".", "").length();
                    levels.computeIfAbsent(level, l -> new HashMap<>())
                            .computeIfAbsent(metadata, mm -> new HashSet<>())
                            .add(m.getKey().toLowerCase());
                }
            }

            // Add the aliases to the commands config.
            this.commandsConfConfigNode.getNode(metadata.getCommandKey()).getNode(ALIASES).setValue(map);
        }

        // Now we've created the levels, now we create the commands. We start with the first level.
        // Sorted map, means we just iterate!
        for (Map.Entry<Integer, Map<CommandMetadata, Set<String>>> entry : levels.entrySet()) {
            int level = entry.getKey();
            Map<CommandMetadata, Set<String>> map = entry.getValue();

            // For each entry, create the mapping.
            for (Map.Entry<CommandMetadata, Set<String>> metadata : map.entrySet()) {
                CommandControl control = commands.computeIfAbsent(metadata.getKey(), mm -> construct(mm, serviceCollection));
                this.controlToAliases.computeIfAbsent(control, c -> new ArrayList<>()).addAll(metadata.getValue()); // for docgen
                metadata.getValue().forEach(alias -> aliases.put(alias, control));

                // Register if we have level 0...
                if (level == 0) {
                    Sponge.getCommandManager().register(serviceCollection.pluginContainer(), control, new ArrayList<>(metadata.getValue()));
                } else {
                    for (String string : metadata.getValue()) {
                        int index = string.lastIndexOf(" ");
                        aliases.get(string.substring(0, index)).attach(string.substring(index + 1), control);
                    }
                }
            }
        }

        // Okay, now we've created our commands, time to update command conf with the modifiers.
        refreshCommandConfig();
        save();
    }

    private void refreshCommandConfig() {
        this.controlToAliases.keySet().forEach(control -> {
            CommandModifiersConfig config = control.getCommandModifiersConfig();
            ConfigurationNode node = this.commandsConfConfigNode.getNode(control.getCommandKey());
            for (CommandModifier modifier : control.getCommandModifiers()) {
                modifier.value().setupConfig(control.getCommandModifiersConfig(), node);
            }
        });

        save();
    }

    private CommandControl construct(CommandMetadata metadata, INucleusServiceCollection serviceCollection) {
        ICommandExecutor<?> executor = serviceCollection.injector().getInstance(metadata.getExecutor());
        return new CommandControl(
                executor,
                metadata,
                serviceCollection
        );
    }

    @Override public void addMapping(String newCommand, String remapped) {
        if (this.commandremap.containsKey(newCommand.toLowerCase())) {
            throw new IllegalArgumentException("command already in use");
        }

        this.commandremap.put(newCommand.toLowerCase(), remapped);
    }

    @Override public void activate() {
        for (Map.Entry<String, String> entry : this.commandremap.entrySet()) {
            if (!Sponge.getCommandManager().get(entry.getKey()).isPresent()) {
                Sponge.getCommandManager().get(entry.getValue()).ifPresent(x -> {
                    Sponge.getCommandManager().register(Nucleus.getNucleus(), x.getCallable(), entry.getKey());
                });
            }
        }
    }

    @Override public void deactivate() {
        for (Map.Entry<String, String> entry : this.commandremap.entrySet()) {
            Optional<? extends CommandMapping> mappingOptional = Sponge.getCommandManager().get(entry.getKey());
            if (mappingOptional.isPresent() &&
                    Sponge.getCommandManager().getOwner(mappingOptional.get()).map(x -> x.getId().equals(PluginInfo.ID)).orElse(false)) {
                Sponge.getCommandManager().removeMapping(mappingOptional.get());
            }
        }
    }

    @Override public Map<String, Boolean> getAliasMap(String command) {
        return ImmutableMap.of();
    }

    @Override public void onReload(INucleusServiceCollection serviceCollection) {
        // reload the file.
        this.shouldReload = true;
    }

    private <T> T getValue(String node, Function<ConfigurationNode, T> result, Supplier<T> empty, String command) {
        if (this.shouldReload || this.commandsConfConfigNode == null) {
            try {
                load();
            } catch (Exception ex) {
                // something bad happened.
                new PrettyPrinter()
                        .add("[Nucleus] Could not load commands.conf")
                        .hr()
                        .add("We could not read your commands.conf file and so things like cooldowns and warmups are not available.")
                        .add("")
                        .add("The error is below. Check that your config file is not malformed.")
                        .hr()
                        .add("Stack trace:")
                        .add(ex)
                        .log(this.logger, Level.ERROR);
                if (this.commandsConfConfigNode == null) {
                    throw new RuntimeException(ex);
                }
            }
        }

        ConfigurationNode cn = this.commandsConfConfigNode.getNode(command).getNode(node);
        if (cn.isVirtual()) {
            return empty.get();
        }

        return result.apply(cn);
    }

    private void load() {
        try {
            this.commandsConfConfigNode = HoconConfigurationLoader
                    .builder()
                    .setPath(this.commandsFile)
                    .build()
                    .load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            HoconConfigurationLoader
                    .builder()
                    .setPath(this.commandsFile)
                    .build()
                    .save(this.commandsConfConfigNode);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
