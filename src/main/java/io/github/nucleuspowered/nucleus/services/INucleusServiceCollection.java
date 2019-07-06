package io.github.nucleuspowered.nucleus.services;

public interface INucleusServiceCollection {

    IMessageProviderService messageProvider();

    IPermissionCheckService permissionCheck();

    IEconomyServiceProvider economyServiceProvider();

    IWarmupService warmupService();

}
