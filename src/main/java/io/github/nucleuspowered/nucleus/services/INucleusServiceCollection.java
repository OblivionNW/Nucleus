/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import org.slf4j.Logger;

public interface INucleusServiceCollection {

    IMessageProviderService messageProvider();

    IPermissionCheckService permissionCheck();

    IEconomyServiceProvider economyServiceProvider();

    IWarmupService warmupService();

    ICooldownService cooldownService();

    IPlayerOnlineService playerOnlineService();

    Logger getLogger();

}
