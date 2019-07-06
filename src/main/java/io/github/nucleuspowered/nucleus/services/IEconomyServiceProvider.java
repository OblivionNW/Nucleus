package io.github.nucleuspowered.nucleus.services;

import org.spongepowered.api.service.economy.EconomyService;

public interface IEconomyServiceProvider {

    boolean serviceExists();

    EconomyService getEconomyService();

}
