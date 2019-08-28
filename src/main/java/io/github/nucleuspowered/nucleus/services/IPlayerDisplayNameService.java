/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.playername.PlayerDisplayNameService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

@ImplementedBy(PlayerDisplayNameService.class)
public interface IPlayerDisplayNameService {

    void provideResolver(Resolver resolver);

    Text resolve(UUID playerUUID);

    default Text resolve(User user) {
        return resolve(user.getUniqueId());
    }

    Text resolve(CommandSource source);

    @FunctionalInterface
    interface Resolver {

        Optional<Text> resolve(UUID userUUID);

    }

}
