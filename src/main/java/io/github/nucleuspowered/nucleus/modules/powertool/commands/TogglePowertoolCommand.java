/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolUserPreferenceKeys;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@Permissions(mainOverride = "powertool")
@RunAsync
@NoModifiers
@RegisterCommand(value = {"toggle"}, subcommandOf = PowertoolCommand.class)
@NonnullByDefault
@EssentialsEquivalent({"powertooltoggle", "ptt", "pttoggle"})
public class TogglePowertoolCommand extends AbstractCommand<Player> {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args, Cause cause) {
        UserPreferenceService ups = getServiceManager().getServiceUnchecked(UserPreferenceService.class);
        boolean keys = ups.get(src.getUniqueId(), PowertoolUserPreferenceKeys.POWERTOOL_ENABLED).orElse(true);

        // If specified - get the key. Else, the inverse of what we have now.
        boolean toggle = args.<Boolean>getOne(NucleusParameters.Keys.BOOL).orElse(!keys);
        ups.set(src.getUniqueId(), PowertoolUserPreferenceKeys.POWERTOOL_ENABLED, toggle);

        sendMessageTo(src, "command.powertool.toggle", getMessageFor(src, toggle ? "standard.enabled" : "standard.disabled"));
        return CommandResult.success();
    }

}
