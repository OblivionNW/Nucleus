/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.services;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.internal.interfaces.ServiceBase;
import io.github.nucleuspowered.nucleus.internal.traits.PermissionTrait;
import io.github.nucleuspowered.nucleus.modules.vanish.VanishKeys;
import io.github.nucleuspowered.nucleus.modules.vanish.commands.VanishCommand;
import io.github.nucleuspowered.nucleus.modules.vanish.config.VanishConfigAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VanishService implements SimpleReloadable, PermissionTrait, ServiceBase {

    private static final String CAN_SEE_PERM = Nucleus.getNucleus().getPermissionRegistry()
            .getPermissionsForNucleusCommand(VanishCommand.class).getPermissionWithSuffix("see");
    private boolean isAlter = false;
    private final Map<UUID, Instant> lastVanish = new HashMap<>();

    @Override
    public void onReload() {
        String property = System.getProperty("nucleus.vanish.tablist.enable");
        this.isAlter = property != null && !property.isEmpty() &&
            Nucleus.getNucleus().getInternalServiceManager().getServiceUnchecked(VanishConfigAdapter.class).getNodeOrDefault().isAlterTabList();
    }

    public boolean isVanished(User player) {
        return Nucleus.getNucleus().getStorageManager().getUserService()
                .getOnThread(player.getUniqueId())
                .flatMap(x -> x.get(VanishKeys.VANISH_STATUS))
                .orElse(false);
    }

    public void vanishPlayer(User player) {
        vanishPlayer(player, false);
    }

    public void vanishPlayer(User player, boolean delay) {
        Nucleus.getNucleus().getStorageManager().getUserService()
                .getOrNewOnThread(player.getUniqueId())
                .set(VanishKeys.VANISH_STATUS, true);

        if (player instanceof Player) {
            if (delay) {
                Task.builder().execute(() -> vanishPlayerInternal((Player) player)).delayTicks(0).name("Nucleus Vanish runnable").submit(Nucleus.getNucleus());
            } else {
                this.lastVanish.put(player.getUniqueId(), Instant.now());
                vanishPlayerInternal((Player) player);
            }
        }
    }

    private void vanishPlayerInternal(Player player) {
        vanishPlayerInternal(player,
                Nucleus.getNucleus().getStorageManager().getUserService()
                        .getOrNewOnThread(player.getUniqueId())
                        .get(VanishKeys.VANISH_STATUS)
                        .orElse(false));
    }

    private void vanishPlayerInternal(Player player, boolean vanish) {
        if (vanish) {
            player.offer(Keys.VANISH, true);
            player.offer(Keys.VANISH_IGNORES_COLLISION, true);
            player.offer(Keys.VANISH_PREVENTS_TARGETING, true);

            if (this.isAlter) {
                Sponge.getServer().getOnlinePlayers().stream().filter(x -> !player.equals(x) || !hasPermission(x, CAN_SEE_PERM))
                        .forEach(x -> x.getTabList().removeEntry(player.getUniqueId()));
            }
        }
    }

    public void unvanishPlayer(User user) {
        Nucleus.getNucleus().getStorageManager().getUserService()
                .getOrNew(user.getUniqueId())
                .thenAccept(x -> x.set(VanishKeys.VANISH_STATUS, false));
        user.offer(Keys.VANISH, false);
        user.offer(Keys.VANISH_IGNORES_COLLISION, false);
        user.offer(Keys.VANISH_PREVENTS_TARGETING, false);

        if (this.isAlter && user instanceof Player) {
            Player player = (Player) user;
            Sponge.getServer().getOnlinePlayers().forEach(x -> {
                if (!x.getTabList().getEntry(player.getUniqueId()).isPresent()) {
                    x.getTabList().addEntry(TabListEntry.builder()
                            .displayName(Text.of(player.getName()))
                            .profile(player.getProfile())
                            .gameMode(player.gameMode().get())
                            .latency(player.getConnection().getLatency())
                            .list(x.getTabList()).build());
                }
            });
        }
    }

    public void setLastVanishedTime(UUID pl, Instant instant) {
        this.lastVanish.put(pl, instant);
    }

    Optional<Instant> getLastVanishTime(UUID pl) {
        return Optional.ofNullable(this.lastVanish.get(pl));
    }

    public void clearLastVanishTime(UUID pl) {
        this.lastVanish.remove(pl);
    }

}
