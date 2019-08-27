/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp;

import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.warp.config.WarpConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = "warp", name = "Warp")
public class WarpModule extends ConfigurableModule<WarpConfigAdapter> {

    @Override
    public WarpConfigAdapter createAdapter() {
        return new WarpConfigAdapter();
    }

    @Override public void setPermissionPredicates() {
        PermissionResolverImpl.INSTANCE.registerPermissionPredicate(
                perm -> perm.toLowerCase().startsWith("nucleus.warps."), SuggestedLevel.ADMIN);
    }
}
