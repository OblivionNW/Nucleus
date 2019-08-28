/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository;

import io.github.nucleuspowered.nucleus.services.IPlayerDisplayNameService;

import java.nio.file.Path;

public class ConfigFileMessagesRepository extends AbstractMessageRepository implements IMessageRepository {

    private final Path file;

    public ConfigFileMessagesRepository(IPlayerDisplayNameService playerDisplayNameService, Path file) {
        super(playerDisplayNameService);
        this.file = file;
    }

    @Override
    public void invalidateIfNecessary() {
        this.cachedMessages.clear();
        this.cachedStringMessages.clear();
    }

    @Override String getEntry(String key) {
        return null;
    }
}
