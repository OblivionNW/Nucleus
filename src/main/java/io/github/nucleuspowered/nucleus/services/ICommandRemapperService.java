/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.commandremap.CommandRemapperService;

@ImplementedBy(CommandRemapperService.class)
public interface ICommandRemapperService {

    void addMapping(String newCommand, String remapped);

    void activate();

    void deactivate();
}
