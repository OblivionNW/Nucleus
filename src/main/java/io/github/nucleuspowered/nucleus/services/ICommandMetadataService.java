/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandMetadata;
import io.github.nucleuspowered.nucleus.services.impl.command.CommandMetadataService;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@ImplementedBy(CommandMetadataService.class)
public interface ICommandMetadataService {

    void registerCommand(CommandControl control);

    void completeRegistrationPhase();

    void addMapping(String newCommand, String remapped);

    void activate();

    void deactivate();

    OptionalInt getCommandWarmup(String... command);

    OptionalInt getCommandCooldown(String... command);

    OptionalDouble getCommandCost(String... command);

    Map<String, Boolean> getAliasMap(String... command);
}
