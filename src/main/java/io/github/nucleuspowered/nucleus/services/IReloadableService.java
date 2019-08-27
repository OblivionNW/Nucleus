package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.impl.reloadable.ReloadableService;

@ImplementedBy(ReloadableService.class)
public interface IReloadableService {

    void registerReloadable(Reloadable reloadable);

    void removeReloadable(Reloadable reloadable);

    void fireReloadables(INucleusServiceCollection serviceCollection);

}
