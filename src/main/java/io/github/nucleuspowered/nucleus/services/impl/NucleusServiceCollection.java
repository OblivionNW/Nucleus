package io.github.nucleuspowered.nucleus.services.impl;

import io.github.nucleuspowered.nucleus.services.ICooldownService;
import io.github.nucleuspowered.nucleus.services.IEconomyServiceProvider;
import io.github.nucleuspowered.nucleus.services.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.IPermissionCheckService;
import io.github.nucleuspowered.nucleus.services.IReloadableService;
import io.github.nucleuspowered.nucleus.services.IUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.IWarmupService;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

public class NucleusServiceCollection implements INucleusServiceCollection {

    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

    private final IMessageProviderService messageProviderService;
    private final IEconomyServiceProvider economyServiceProvider;
    private final IWarmupService warmupService;
    private final ICooldownService cooldownService;
    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final IUserPreferenceService userPreferenceService;
    private final IPermissionCheckService permissionCheckService;
    private final IReloadableService reloadableService;

    public NucleusServiceCollection(
            IMessageProviderService messageProviderService,
            IEconomyServiceProvider economyServiceProvider,
            IWarmupService warmupService,
            ICooldownService cooldownService,
            IUserPreferenceService userPreferenceService,
            IPermissionCheckService permissionCheckService,
            IReloadableService reloadableService,
            PluginContainer pluginContainer,
            Logger logger) {
        this.messageProviderService = messageProviderService;
        this.economyServiceProvider = economyServiceProvider;
        this.warmupService = warmupService;
        this.cooldownService = cooldownService;
        this.userPreferenceService = userPreferenceService;
        this.permissionCheckService = permissionCheckService;
        this.reloadableService = reloadableService;
        this.pluginContainer = pluginContainer;
        this.logger = logger;
    }

    @Override
    public IMessageProviderService messageProvider() {
        return this.messageProviderService;
    }

    @Override
    public IPermissionCheckService permissionCheck() {
        return this.permissionCheckService;
    }

    @Override
    public IEconomyServiceProvider economyServiceProvider() {
        return this.economyServiceProvider;
    }

    @Override
    public IWarmupService warmupService() {
        return this.warmupService;
    }

    @Override
    public ICooldownService cooldownService() {
        return this.cooldownService;
    }

    @Override
    public IUserPreferenceService userPreferenceService() {
        return this.userPreferenceService;
    }

    @Override public IReloadableService reloadableService() {
        return this.reloadableService;
    }

    @Override
    public PluginContainer pluginContainer() {
        return this.pluginContainer;
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    public <I, C extends I> void registerService(Class<I> key, C service) {
        registerService(key, service, false);
    }

    public <I, C extends I> void registerService(Class<I> key, C service, boolean rereg) {
        if (!rereg && (this.instances.containsKey(key) || this.suppliers.containsKey(key))) {
            return;
        }

        this.suppliers.remove(key);
        this.instances.put(key, service);
    }

    public <I, C extends I> void registerServiceSupplier(Class<I> key, Supplier<C> service, boolean rereg) {
        if (!rereg && (this.instances.containsKey(key) || this.suppliers.containsKey(key))) {
            return;
        }

        this.instances.remove(key);
        this.suppliers.put(key, service);
    }

    @SuppressWarnings("unchecked")
    public <I> Optional<I> getService(Class<I> key) {
        if (this.instances.containsKey(key)) {
            return Optional.of((I) this.instances.get(key));
        } else if (this.suppliers.containsKey(key)) {
            return Optional.of((I) this.suppliers.get(key).get());
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <I> I getServiceUnchecked(Class<I> key) {
        if (this.instances.containsKey(key)) {
            return (I) this.instances.get(key);
        } else if (this.suppliers.containsKey(key)) {
            return (I) this.suppliers.get(key).get();
        }

        throw new NoSuchElementException(key.getName());
    }

}
