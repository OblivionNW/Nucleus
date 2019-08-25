/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.control;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class UsageCommand implements CommandCallable {

    private final INucleusServiceCollection serviceCollection;
    private final CommandControl attachedCommandControl;

    public UsageCommand(CommandControl attachedCommandControl,
            INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.attachedCommandControl = attachedCommandControl;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) {
        return process(source, arguments, null);
    }

    CommandResult process(CommandSource source, String arguments, @Nullable String previous) {
        if (!testPermission(source)) {
            this.serviceCollection.messageProvider().sendTo(source,"command.usage.nopermission");
            return CommandResult.empty();
        }

        try {
            List<Text> textMessages = usage(source, previous);

            // Header
            String command = this.attachedCommandControl.getCommand();
            Text header = this.serviceCollection.messageProvider().getMessageFor(source, "command.usage.header", command);

            Util.getPaginationBuilder(source).title(header).contents(textMessages).sendTo(source);
            return CommandResult.success();
        } catch (CommandPermissionException e) {
            this.serviceCollection.messageProvider().sendTo(source, "command.usage.nopermission");
            return CommandResult.empty();
        }
    }

    public List<Text> usage(CommandSource source, @Nullable String previous) throws CommandPermissionException {
        if (!testPermission(source)) {
            throw new CommandPermissionException();
        }

        Nucleus plugin = Nucleus.getNucleus();

        List<Text> textMessages = Lists.newArrayList();

        if (previous != null) {
            textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.noexist", previous));
            textMessages.add(Util.SPACE);
        }

        if (this.attachedCommandControl.getSourceType() == Player.class) {
            textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.playeronly"));
        }

        textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.module",
                this.attachedCommandControl.getMetadata().getModulename(),
                this.attachedCommandControl.getMetadata().getModuleid()));

        Optional<Text> desc = this.attachedCommandControl.getShortDescription(source);
        if (desc.isPresent()) {
            textMessages.add(Util.SPACE);
            textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.summary"));
            textMessages.add(desc.get());
        }

        Optional<Text> ext = this.attachedCommandControl.getShortDescription(source);
        if (ext.isPresent()) {
            textMessages.add(Util.SPACE);
            textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.description"));
            textMessages.add(ext.get());
        }

        if (this.attachedCommandControl.hasExecutor()) {
            textMessages.add(Util.SPACE);
            textMessages.add(plugin.getMessageProvider().getTextMessageWithFormat("command.usage.usage"));
            textMessages.add(Text.of(TextColors.WHITE, this.attachedCommandControl.getUsage(source)));
        }

        return textMessages;
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) {
        return Lists.newArrayList();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return this.attachedCommandControl.testPermission(source);
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.EMPTY;
    }

}
