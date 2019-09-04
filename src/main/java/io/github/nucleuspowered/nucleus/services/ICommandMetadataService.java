/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.internal.command.control.CommandMetadata;
import io.github.nucleuspowered.nucleus.services.impl.command.CommandMetadataService;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@ImplementedBy(CommandMetadataService.class)
public interface ICommandMetadataService {

    void registerCommand(CommandMetadata metadata);

    void completeRegistrationPhase(INucleusServiceCollection serviceCollection);

    void addMapping(String newCommand, String remapped);

    void activate();

    void deactivate();

    Map<String, Boolean> getAliasMap(String command);

}
