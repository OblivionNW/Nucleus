/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.gson.JsonObject;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.impl.NucleusServiceCollection;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Singleton;

@Singleton
@ImplementedBy(NucleusServiceCollection.class)
public interface INucleusServiceCollection {

    IMessageProviderService messageProvider();

    IPermissionService permissionCheck();

    IEconomyServiceProvider economyServiceProvider();

    IWarmupService warmupService();

    ICooldownService cooldownService();

    IUserPreferenceService userPreferenceService();

    IReloadableService reloadableService();

    IPlayerOnlineService playerOnlineService();

    IMessageTokenService messageTokenService();

    IStorageManager<JsonObject> storageManager();

    Injector injector();

    PluginContainer pluginContainer();

    Logger logger();

    <I, C extends I> void registerService(Class<I> key, C service, boolean rereg);

    <I, C extends I> void registerServiceSupplier(Class<I> key, Supplier<C> service, boolean rereg);

    @SuppressWarnings("unchecked") <I> Optional<I> getService(Class<I> key);

    @SuppressWarnings("unchecked") <I> I getServiceUnchecked(Class<I> key);
}
