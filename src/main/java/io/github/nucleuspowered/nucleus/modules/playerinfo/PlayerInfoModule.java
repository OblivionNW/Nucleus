/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo;

import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.afk.AFKModule;
import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = PlayerInfoModule.ID, name = "Player Info", softDependencies = AFKModule.ID)
public class PlayerInfoModule extends ConfigurableModule<PlayerInfoConfigAdapter> {

    public static final String ID = "playerinfo";

    @Override
    public PlayerInfoConfigAdapter createAdapter() {
        return new PlayerInfoConfigAdapter();
    }

}
