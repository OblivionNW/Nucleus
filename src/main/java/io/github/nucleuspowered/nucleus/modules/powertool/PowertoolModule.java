/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool;

import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.modules.core.CoreModule;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = "powertool", name = "Powertool", dependencies = CoreModule.ID)
public class PowertoolModule extends StandardModule {

    @Override public void performEnableTasks() throws Exception {
        super.performEnableTasks();

    }
}
