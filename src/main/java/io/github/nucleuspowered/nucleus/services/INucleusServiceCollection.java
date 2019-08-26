/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Singleton;

@Singleton
public interface INucleusServiceCollection {

    IMessageProviderService messageProvider();

    IPermissionCheckService permissionCheck();

    IEconomyServiceProvider economyServiceProvider();

    IWarmupService warmupService();

    ICooldownService cooldownService();

    IUserPreferenceService userPreferenceService();

    IReloadableService reloadableService();

    PluginContainer pluginContainer();

    Logger logger();

}
