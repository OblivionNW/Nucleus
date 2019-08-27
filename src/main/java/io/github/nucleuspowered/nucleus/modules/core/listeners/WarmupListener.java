/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core.listeners;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarmupManagerService;
import io.github.nucleuspowered.nucleus.internal.interfaces.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.modules.core.config.WarmupConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.IWarmupService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import javax.inject.Inject;

public class WarmupListener implements Reloadable, ListenerBase {

    private final IWarmupService warmupService;
    private WarmupConfig warmupConfig = Nucleus.getNucleus().getWarmupConfig();

    @Inject
    public WarmupListener(IWarmupService warmupService) {
        this.warmupService = warmupService;
    }

    @Listener(order = Order.LAST)
    public void onPlayerMovement(MoveEntityEvent event, @Root Player player) {
        // Rotating is OK!
        if (this.warmupConfig.isOnMove() && !event.getFromTransform().getLocation().equals(event.getToTransform().getLocation())) {
            cancelWarmup(player);
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerCommand(SendCommandEvent event, @Root Player player) {
        if (this.warmupConfig.isOnCommand()) {
            cancelWarmup(player);
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        cancelWarmup(event.getTargetEntity());
    }

    private void cancelWarmup(Player player) {
        if (this.warmupService.cancel(player) && player.isOnline()) {
            player.sendMessage(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("warmup.cancel"));
        }
    }

    public void onReload(INucleusServiceCollection collection) {
        this.warmupConfig = Nucleus.getNucleus().getWarmupConfig();
    }
}
