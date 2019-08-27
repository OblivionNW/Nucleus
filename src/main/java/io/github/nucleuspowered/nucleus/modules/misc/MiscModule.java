/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc;

import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.misc.config.MiscConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = MiscModule.ID, name = "Miscellaneous")
public class MiscModule extends ConfigurableModule<MiscConfigAdapter> {

    public final static String ID = "misc";

    @Override
    public MiscConfigAdapter createAdapter() {
        return new MiscConfigAdapter();
    }
}
