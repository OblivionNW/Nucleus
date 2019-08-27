/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.quickstart;

import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import uk.co.drnaylor.quickstart.exceptions.QuickStartModuleLoaderException;
import uk.co.drnaylor.quickstart.loaders.ModuleConstructor;

import java.util.List;
import java.util.Map;

public class QuickStartModuleConstructor implements ModuleConstructor<StandardModule> {

    private final Map<String, Map<String, List<String>>> moduleList;
    private final INucleusServiceCollection serviceCollection;

    public QuickStartModuleConstructor(Map<String, Map<String, List<String>>> m, INucleusServiceCollection serviceCollection) {
         this.moduleList = m;
         this.serviceCollection = serviceCollection;
    }

    @Override
    public StandardModule constructModule(Class<? extends StandardModule> moduleClass) throws QuickStartModuleLoaderException.Construction {
        StandardModule m;
        try {
            m = this.serviceCollection.injector().getInstance(moduleClass);
        } catch (Exception e) {
            throw new QuickStartModuleLoaderException.Construction(moduleClass, "Could not instantiate module!", e);
        }

        m.init(this.moduleList.get(moduleClass.getName()));
        return m;
    }
}
