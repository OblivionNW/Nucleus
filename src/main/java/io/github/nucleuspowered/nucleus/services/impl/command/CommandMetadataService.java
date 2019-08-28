/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.command;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.PluginInfo;
import io.github.nucleuspowered.nucleus.guice.ConfigDirectory;
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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandMetadataService implements ICommandMetadataService, Reloadable {

    private static final String WARMUP = "warmup";
    private static final String COOLDOWN = "cooldown";
    private static final String COST = "cost";

    private final Logger logger;
    private final Map<String, String> commandremap = new HashMap<>();
    private final Path commandsFile;
    private boolean shouldReload = true;
    private ConfigurationNode commandsConfConfigNode;

    @Inject
    public CommandMetadataService(@ConfigDirectory Path configDirectory, IReloadableService reloadableService, Logger logger) {
        reloadableService.registerReloadable(this);
        this.commandsFile = configDirectory.resolve("commands.conf");
        this.logger = logger;
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

    @Override public OptionalInt getCommandWarmup(String... command) {
        return getValue(WARMUP, cn -> OptionalInt.of(cn.getInt()), OptionalInt::empty, command);
    }

    @Override public OptionalInt getCommandCooldown(String... command) {
        return getValue(COOLDOWN, cn -> OptionalInt.of(cn.getInt()), OptionalInt::empty, command);
    }

    @Override public OptionalDouble getCommandCost(String... command) {
        return getValue(COST, cn -> OptionalDouble.of(cn.getDouble()), OptionalDouble::empty, command);
    }

    @Override public Map<String, Boolean> getAliasMap(String... command) {
        return ImmutableMap.of();
    }

    @Override public void onReload(INucleusServiceCollection serviceCollection) {
        // reload the file.
        this.shouldReload = true;
    }

    private <T> T getValue(String node, Function<ConfigurationNode, T> result, Supplier<T> empty, String[] command) {
        if (this.shouldReload || this.commandsConfConfigNode == null) {
            try {
                this.commandsConfConfigNode = HoconConfigurationLoader
                        .builder()
                        .setPath(this.commandsFile)
                        .build()
                        .load();
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

        ConfigurationNode cn = this.commandsConfConfigNode.getNode((Object[]) command).getNode(node);
        if (cn.isVirtual()) {
            return empty.get();
        }

        return result.apply(cn);
    }

}
