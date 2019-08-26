/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.vanish.VanishUserPrefKeys;
import io.github.nucleuspowered.nucleus.modules.vanish.listener.VanishListener;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@Permissions(value = VanishListener.LOGIN_VANISH_PERMISSION)
@RunAsync
@NoModifiers
@RegisterCommand(value = {"vanishonlogin", "vonlogin"})
@NonnullByDefault
public class ToggleVanishOnLoginCommand extends AbstractCommand<Player> {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args, Cause cause) {
        UserPreferenceService ups = getServiceManager().getServiceUnchecked(UserPreferenceService.class);
        boolean keys = ups.get(src.getUniqueId(), VanishUserPrefKeys.VANISH_ON_LOGIN).orElse(true);

        // If specified - get the key. Else, the inverse of what we have now.
        boolean toggle = args.<Boolean>getOne(NucleusParameters.Keys.BOOL).orElse(!keys);
        ups.set(src.getUniqueId(), VanishUserPrefKeys.VANISH_ON_LOGIN, toggle);

        sendMessageTo(src, "command.vanishonlogin.toggle", getMessageFor(src, toggle ? "standard.enabled" : "standard.disabled"));
        return CommandResult.success();
    }

}
