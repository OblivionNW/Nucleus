/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.message.MessageUserPrefKeys;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map;

@RunAsync
@NoModifiers
@Permissions
@RegisterCommand({"msgtoggle", "messagetoggle", "mtoggle"})
@NonnullByDefault
public class MsgToggleCommand extends AbstractCommand<Player> {

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> mpi = super.permissionSuffixesToRegister();
        mpi.put("bypass", PermissionInformation.getWithTranslation("permission.msgtoggle.bypass", SuggestedLevel.ADMIN));
        return mpi;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    protected CommandResult executeCommand(Player src, CommandContext args, Cause cause) {
        UserPreferenceService userPreferenceService = getServiceUnchecked(UserPreferenceService.class);
        boolean flip = args.<Boolean>getOne(NucleusParameters.Keys.BOOL)
                .orElseGet(() -> userPreferenceService.getUnwrapped(src.getUniqueId(), MessageUserPrefKeys.RECEIVING_MESSAGES));

        userPreferenceService.set(src.getUniqueId(), MessageUserPrefKeys.RECEIVING_MESSAGES, flip);
        sendMessageTo(src, "command.msgtoggle.success." + flip);

        return CommandResult.success();
    }

}
