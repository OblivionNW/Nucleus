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

    private final LinkedHashSet<DisplayNameResolver> resolvers = new LinkedHashSet<>();
    private final LinkedHashSet<DisplayNameQuery> queries = new LinkedHashSet<>();

    @Override
    public void provideDisplayNameResolver(DisplayNameResolver resolver) {
        this.resolvers.add(resolver);
    }

    @Override
    public void provideDisplayNameQuery(DisplayNameQuery resolver) {
        this.queries.add(resolver);
    }

    @Override
    public Optional<User> getUser(Text displayName) {
        Optional<User> withRealName = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(displayName.toPlain());
        if (withRealName.isPresent()) {
            return withRealName;
        }

        for (DisplayNameQuery query : this.queries) {
            Optional<User> user = query.resolve(displayName);
            if (user.isPresent()) {
                return user;
            }
        }

        return Optional.empty();
    }

    @Override
    public Text getDisplayName(final UUID playerUUID) {
        if (playerUUID == Util.consoleFakeUUID) {
            return getDisplayName(Sponge.getServer().getConsole());
        }

        for (DisplayNameResolver resolver : this.resolvers) {
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
    public Text getDisplayName(CommandSource source) {
        if (source instanceof User) {
            return getDisplayName(((User) source).getUniqueId());
        }

        return Text.of(source.getName());
    }

}
