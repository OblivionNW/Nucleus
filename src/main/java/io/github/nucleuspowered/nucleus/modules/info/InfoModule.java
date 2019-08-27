/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.info.config.InfoConfigAdapter;
import org.spongepowered.api.Sponge;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = InfoModule.ID, name = "Info")
public class InfoModule extends ConfigurableModule<InfoConfigAdapter> {

    public static final String ID = "info";
    public static final String MOTD_KEY = "motd";

    @Override
    public InfoConfigAdapter createAdapter() {
        return new InfoConfigAdapter();
    }

    @Override public void performPreTasks() throws Exception {
        super.performPreTasks();

        Nucleus.getNucleus().addTextFileController(
                MOTD_KEY,
                Sponge.getAssetManager().getAsset(Nucleus.getNucleus(), "motd.txt").get(),
                Nucleus.getNucleus().getConfigDirPath().resolve("motd.txt"));
    }
}
