package io.github.nucleuspowered.nucleus.services.impl.warmup;

import io.github.nucleuspowered.nucleus.api.service.NucleusWarmupManagerService;
import io.github.nucleuspowered.nucleus.services.IWarmupService;
import org.spongepowered.api.entity.living.player.Player;

import java.time.Duration;

public class WarmupService implements IWarmupService {

    @Override public void executeAfter(Player target, Duration duration, WarmupTask runnable) {

    }

    @Override public void executeAfterAsync(Player target, Duration duration, WarmupTask runnable) {

    }

    @Override public boolean cancel(Player player) {
        return false;
    }

    @Override public boolean awaitingExecution(Player player) {
        return false;
    }
}
