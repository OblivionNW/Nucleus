/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository;

import io.github.nucleuspowered.nucleus.services.IPlayerDisplayNameService;

import java.util.ResourceBundle;

public class PropertiesMessageRepository extends AbstractMessageRepository implements IMessageRepository {

    private final ResourceBundle resource;

    public PropertiesMessageRepository(IPlayerDisplayNameService playerDisplayNameService, ResourceBundle resource) {
        super(playerDisplayNameService);
        this.resource = resource;
    }

    @Override
    String getEntry(String key) {
        if (this.resource.containsKey(key)) {
            return this.resource.getString(key);
        }

        throw new IllegalArgumentException("The key " + key + " does not exist!");
    }

}
