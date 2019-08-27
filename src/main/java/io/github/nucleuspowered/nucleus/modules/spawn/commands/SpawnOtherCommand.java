/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.teleport.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.TeleportScanners;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.core.services.SafeTeleportService;
import io.github.nucleuspowered.nucleus.modules.spawn.config.GlobalSpawnConfig;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfig;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.spawn.events.SendToSpawnEvent;
import io.github.nucleuspowered.nucleus.modules.spawn.helpers.SpawnHelper;
import io.github.nucleuspowered.nucleus.util.CauseStackHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@NoModifiers
@NonnullByDefault
@Permissions(prefix = "spawn", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = "other", subcommandOf = SpawnCommand.class)
public class SpawnOtherCommand extends AbstractCommand<CommandSource> implements SimpleReloadable {

    private final String otherKey = "subject";
    private final String worldKey = "world";
    private GlobalSpawnConfig gsc = new GlobalSpawnConfig();
    private boolean safeTeleport = true;

    @Override public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.user(Text.of(otherKey)),
            GenericArguments.optional(GenericArguments.world(Text.of(worldKey)))
        };
    }

    @Override public void onReload() throws Exception {
        SpawnConfig sc = getServiceUnchecked(SpawnConfigAdapter.class).getNodeOrDefault();
        this.gsc = sc.getGlobalSpawn();
        this.safeTeleport = sc.isSafeTeleport();
    }

    @Override protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        return new HashMap<String, PermissionInformation>() {{
            put("offline", PermissionInformation.getWithTranslation("permission.spawnother.offline", SuggestedLevel.ADMIN));
        }};
    }

    @Override public CommandResult executeCommand(CommandSource src, CommandContext args, Cause cause) throws Exception {
        User target = args.<User>getOne(otherKey).get();
        WorldProperties world = this.getWorldProperties(src, worldKey, args)
            .orElseGet(() -> gsc.isOnSpawnCommand() ? gsc.getWorld().get() : Sponge.getServer().getDefaultWorld().get());

        Transform<World> worldTransform = SpawnHelper.getSpawn(world, target.getPlayer().orElse(null));

        EventContext context = EventContext.builder().add(EventContexts.SPAWN_EVENT_TYPE, SendToSpawnEvent.Type.COMMAND).build();
        SendToSpawnEvent event = new SendToSpawnEvent(worldTransform, target, CauseStackHelper.createCause(context, src));
        if (Sponge.getEventManager().post(event)) {
            if (event.getCancelReason().isPresent()) {
                throw ReturnMessageException.fromKey(src,"command.spawnother.other.failed.reason", target.getName(), event.getCancelReason().get());
            }

            throw ReturnMessageException.fromKey(src, "command.spawnother.other.failed.noreason", target.getName());
        }

        if (!target.isOnline()) {
            return isOffline(src, target, event.getTransformTo());
        }

        // If we don't have a rotation, then use the current rotation
        Player player = target.getPlayer().get();
        TeleportResult result = getServiceUnchecked(SafeTeleportService.class)
                .teleportPlayerSmart(
                        player,
                        event.getTransformTo(),
                        true,
                        this.safeTeleport,
                        TeleportScanners.NO_SCAN
                );
        if (result.isSuccessful()) {
            sendMessageTo(src, "command.spawnother.success.source", target.getName(), world.getWorldName());
            sendMessageTo(player, "command.spawnother.success.target", world.getWorldName());
            return CommandResult.success();
        }

        throw ReturnMessageException.fromKey("command.spawnother.fail", target.getName(), world.getWorldName());
    }

    private CommandResult isOffline(CommandSource source, User user, Transform<World> worldTransform) throws Exception {
        if (!permissions.testSuffix(source, "offline")) {
            throw new ReturnMessageException(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.spawnother.offline.permission"));
        }

        user.setLocation(worldTransform.getPosition(), worldTransform.getExtent().getUniqueId());
        sendMessageTo(source, "command.spawnother.offline.sendonlogin", user.getName(), worldTransform.getExtent().getName());
        return CommandResult.success();
    }
}
