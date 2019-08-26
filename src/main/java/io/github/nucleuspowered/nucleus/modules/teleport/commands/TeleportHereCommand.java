/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.api.teleport.TeleportResult;
import io.github.nucleuspowered.nucleus.argumentparsers.IfConditionElseArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.teleport.config.TeleportConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: TeleportHere is considered an admin command, as there is a potential
 * for abuse for non-admin players trying to pull players. No cost or warmups
 * will be applied. /tpahere should be used instead in these circumstances.
 */
@Permissions(prefix = "teleport", suggestedLevel = SuggestedLevel.ADMIN, supportsSelectors = true)
@NoModifiers
@RegisterCommand({"tphere", "tph"})
@EssentialsEquivalent(value = {"tphere", "s", "tpohere"}, isExact = false,
        notes = "If you have permission, this will override '/tptoggle' automatically.")
@NonnullByDefault
public class TeleportHereCommand extends AbstractCommand<Player> implements SimpleReloadable {

    private boolean isDefaultQuiet = false;

    @Override public void onReload() {
        this.isDefaultQuiet = getServiceUnchecked(TeleportConfigAdapter.class).getNodeOrDefault().isDefaultQuiet();
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("offline", PermissionInformation.getWithTranslation("permission.tphere.offline", SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.flags().flag("q", "-quiet").buildWith(
                        IfConditionElseArgument.permission(this.permissions.getPermissionWithSuffix("offline"),
                                NucleusParameters.ONE_USER_PLAYER_KEY,
                                NucleusParameters.ONE_PLAYER))
        };
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args, Cause cause) throws Exception {
        boolean beQuiet = args.<Boolean>getOne("q").orElse(this.isDefaultQuiet);
        User target = args.<User>getOne(NucleusParameters.Keys.PLAYER).get();
        PlayerTeleporterService sts = getServiceUnchecked(PlayerTeleporterService.class);
        if (target.getPlayer().isPresent()) {
            Player to = target.getPlayer().get();
            TeleportResult result = sts.teleportWithMessage(
                    src,
                    to,
                    src,
                    false,
                    beQuiet,
                    false
            );
            return result.isSuccessful() ? CommandResult.success() : CommandResult.empty();
        } else {
            this.permissions.checkSuffix(src, "offline", () -> ReturnMessageException.fromKey("command.tphere.noofflineperms"));

            // Update the offline player's next location
            target.setLocation(src.getPosition(), src.getWorld().getUniqueId());
            sendMessageTo(src, "command.tphere.offlinesuccess", target.getName());
        }

        return CommandResult.success();
    }
}
