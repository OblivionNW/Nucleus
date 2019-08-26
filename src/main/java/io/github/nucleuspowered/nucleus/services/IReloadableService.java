package io.github.nucleuspowered.nucleus.services;

import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;

public interface IReloadableService {

    void registerReloadable(Reloadable reloadable);

    void removeReloadable(Reloadable reloadable);

    void fireReloadables(INucleusServiceCollection serviceCollection);

}
