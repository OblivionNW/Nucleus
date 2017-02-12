/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.iapi.data.MuteData;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCooldown;
import io.github.nucleuspowered.nucleus.internal.annotations.NoCost;
import io.github.nucleuspowered.nucleus.internal.annotations.NoWarmup;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.mute.handler.MuteHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.Optional;

/**
 * Checks the mute status of a subject.
 *
 * Command Usage: /checkmute user Permission: quickstart.checkmute.base
 */
@Permissions(suggestedLevel = SuggestedLevel.MOD)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
@RegisterCommand("checkmute")
public class CheckMuteCommand extends AbstractCommand<CommandSource> {

    @Inject private MuteHandler handler;
    private final String playerArgument = "subject";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {GenericArguments.onlyOne(GenericArguments.user(Text.of(playerArgument)))};
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        // Get the user.
        User user = args.<User>getOne(playerArgument).get();

        Optional<MuteData> omd = handler.getPlayerMuteData(user);
        if (!omd.isPresent()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkmute.none", user.getName()));
            return CommandResult.success();
        }

        // Muted, get information.
        MuteData md = omd.get();
        String name;
        if (md.getMuter().equals(Util.consoleFakeUUID)) {
            name = Sponge.getServer().getConsole().getName();
        } else {
            Optional<User> ou = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(md.getMuter());
            name = ou.isPresent() ? ou.get().getName() : plugin.getMessageProvider().getMessageWithFormat("standard.unknown");
        }

        Tuple<String, String> a = md.getForString();
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.checkmute.mute", user.getName(), name, a.getSecond(), a.getFirst()));
        src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("standard.reason", md.getReason()));
        return CommandResult.success();
    }
}
