package io.github.nucleuspowered.nucleus.services.impl.cooldown;

import io.github.nucleuspowered.nucleus.services.ICooldownService;
import org.spongepowered.api.util.Identifiable;

import java.time.Duration;
import java.util.Optional;

public class CooldownService implements ICooldownService {

    @Override public boolean hasCooldown(String key, Identifiable identifiable) {
        return false;
    }

    @Override public Optional<Duration> getCooldown(String key, Identifiable identifiable) {
        return Optional.empty();
    }

    @Override public void setCooldown(String key, Identifiable identifiable, Duration cooldownLength) {

    }

    @Override public void clearCooldown(String key, Identifiable identifiable) {

    }
}
