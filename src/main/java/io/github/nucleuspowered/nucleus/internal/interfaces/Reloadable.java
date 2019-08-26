package io.github.nucleuspowered.nucleus.internal.interfaces;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

public interface Reloadable {

    void onReload(INucleusServiceCollection serviceCollection);

}
