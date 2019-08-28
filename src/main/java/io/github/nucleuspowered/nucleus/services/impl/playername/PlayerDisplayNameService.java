/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.playername;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.services.IPlayerDisplayNameService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

@Singleton
public class PlayerDisplayNameService implements IPlayerDisplayNameService {

    private final LinkedHashSet<Resolver> resolvers = new LinkedHashSet<>();

    @Override
    public void provideResolver(Resolver resolver) {
        this.resolvers.add(resolver);
    }

    @Override
    public Text resolve(final UUID playerUUID) {
        if (playerUUID == Util.consoleFakeUUID) {
            return resolve(Sponge.getServer().getConsole());
        }

        for (Resolver resolver : this.resolvers) {
            Optional<Text> userName = resolver.resolve(playerUUID);
            if (userName.isPresent()) {
                return userName.get();
            }
        }

        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
                .get(playerUUID)
                .map(x -> x.get(Keys.DISPLAY_NAME).orElseGet(() -> Text.of(x.getName())))
                .orElseThrow(() -> new IllegalArgumentException("That player UUID does not exist."));
    }

    @Override
    public Text resolve(CommandSource source) {
        if (source instanceof User) {
            return resolve(((User) source).getUniqueId());
        }

        return Text.of(source.getName());
    }

}
