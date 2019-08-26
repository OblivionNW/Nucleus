/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy.listeners;

import com.google.common.collect.ImmutableSet;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.CommandPermissionHandler;
import io.github.nucleuspowered.nucleus.internal.interfaces.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.internal.text.TextParsingUtils;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyModule;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyUserPrefKeys;
import io.github.nucleuspowered.nucleus.modules.commandspy.commands.CommandSpyCommand;
import io.github.nucleuspowered.nucleus.modules.commandspy.config.CommandSpyConfig;
import io.github.nucleuspowered.nucleus.modules.commandspy.config.CommandSpyConfigAdapter;
import io.github.nucleuspowered.nucleus.util.CommandNameCache;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandSpyListener implements SimpleReloadable, ListenerBase.Conditional {

    private final String basePermission;
    private final String exemptTarget;
    private CommandSpyConfig config = new CommandSpyConfig();
    private Set<String> toSpy = ImmutableSet.of();
    private boolean listIsEmpty = true;
    private final UserPreferenceService userPreferenceService;

    public CommandSpyListener() {
        CommandPermissionHandler permissionHandler =
                Nucleus.getNucleus().getPermissionRegistry().getPermissionsForNucleusCommand(CommandSpyCommand.class);
        this.basePermission = permissionHandler.getBase();
        this.exemptTarget = permissionHandler.getPermissionWithSuffix("exempt.target");
        this.userPreferenceService = getServiceUnchecked(UserPreferenceService.class);
    }

    @Listener(order = Order.LAST)
    public void onCommand(SendCommandEvent event, @Root Player player) {

        if (!hasPermission(player, this.exemptTarget)) {
            boolean isInList = false;
            if (!this.listIsEmpty) {
                String command = event.getCommand().toLowerCase();
                Set<String> cmd = CommandNameCache.INSTANCE.getFromCommandAndSource(command, player);
                cmd.retainAll(this.toSpy);
                isInList = !cmd.isEmpty();
            }

            // If the command is in the list, report it.
            if (isInList == this.config.isUseWhitelist()) {
                UUID currentUUID = player.getUniqueId();
                List<Player> playerList = Sponge.getServer().getOnlinePlayers()
                    .stream()
                    .filter(x -> !x.getUniqueId().equals(currentUUID))
                    .filter(x -> hasPermission(x, this.basePermission))
                    .filter(x -> this.userPreferenceService.getUnwrapped(x.getUniqueId(), CommandSpyUserPrefKeys.COMMAND_SPY))
                    .collect(Collectors.toList());

                if (!playerList.isEmpty()) {
                    Text prefix = this.config.getTemplate().getForCommandSource(player);
                    TextParsingUtils.StyleTuple st = TextParsingUtils.getLastColourAndStyle(prefix, null);
                    Text messageToSend = prefix
                            .toBuilder()
                            .append(Text.of(st.colour, st.style, "/", event.getCommand(), Util.SPACE, event.getArguments())).build();
                    playerList.forEach(x -> x.sendMessage(messageToSend));
                }
            }
        }
    }

    @Override
    public void onReload() throws Exception {
        this.config = Nucleus.getNucleus().getModuleHolder().getConfigAdapterForModule(CommandSpyModule.ID, CommandSpyConfigAdapter.class)
            .getNodeOrDefault();
        this.listIsEmpty = this.config.getCommands().isEmpty();
        this.toSpy = this.config.getCommands().stream().map(String::toLowerCase).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public boolean shouldEnable() {
        return !this.config.isUseWhitelist() || !this.listIsEmpty;
    }
}
