/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn;

import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = SpawnModule.ID, name = "Spawn")
public class SpawnModule extends ConfigurableModule<SpawnConfigAdapter> {

    public static final String ID = "spawn";

    @Override
    public SpawnConfigAdapter createAdapter() {
        return new SpawnConfigAdapter();
    }

    @Override public void setPermissionPredicates() {
        PermissionResolverImpl.INSTANCE.registerPermissionPredicate(perm -> perm.toLowerCase().startsWith("nucleus.spawn.worlds."), SuggestedLevel.ADMIN);
    }
}
