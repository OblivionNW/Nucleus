/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit;

import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = KitModule.ID, name = "Kit")
public class KitModule extends ConfigurableModule<KitConfigAdapter> {

    public static final String ID = "kit";

    @Override
    public KitConfigAdapter createAdapter() {
        return new KitConfigAdapter();
    }

    @Override public void setPermissionPredicates() {
        PermissionResolverImpl.INSTANCE.registerPermissionPredicate(perm -> perm.toLowerCase().startsWith("nucleus.kits."), SuggestedLevel.ADMIN);
    }
}
