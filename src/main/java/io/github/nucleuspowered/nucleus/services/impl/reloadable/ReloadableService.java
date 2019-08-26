package io.github.nucleuspowered.nucleus.services.impl.reloadable;

import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.IReloadableService;

import java.util.HashSet;
import java.util.Set;

public class ReloadableService implements IReloadableService {

    private final Set<Reloadable> reloadables = new HashSet<>();

    @Override public void registerReloadable(Reloadable reloadable) {
        this.reloadables.add(reloadable);
    }

    @Override public void removeReloadable(Reloadable reloadable) {
        this.reloadables.remove(reloadable);
    }

    @Override public void fireReloadables(INucleusServiceCollection serviceCollection) {
        for (Reloadable reloadable1 : this.reloadables) {
            reloadable1.onReload(serviceCollection);
        }
    }
}
