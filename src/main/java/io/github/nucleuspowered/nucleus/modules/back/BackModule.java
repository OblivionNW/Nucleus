/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back;

import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.back.config.BackConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = "back", name = "Back", softDependencies = "jail")
public class BackModule extends ConfigurableModule<BackConfigAdapter> {

    @Override
    public BackConfigAdapter createAdapter() {
        return new BackConfigAdapter();
    }

}
