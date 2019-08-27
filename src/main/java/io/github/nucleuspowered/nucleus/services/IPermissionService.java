/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.argumentparsers.NucleusRequirePermissionArgument;
import io.github.nucleuspowered.nucleus.services.impl.permission.NucleusPermissionService;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

@ImplementedBy(NucleusPermissionService.class)
public interface IPermissionService {

    boolean isOpOnly();

    void registerContextCalculator(ContextCalculator<Subject> calculator);

    void checkServiceChange(ProviderRegistration<PermissionService> service);

    boolean hasPermission(Subject subject, String permission);

    boolean hasPermissionWithConsoleOverride(Subject subject, String permission, boolean permissionIfConsoleAndOverridden);

    void registerDescriptions();

    void register(String permission, PermissionMetadata metadata);

    default NucleusRequirePermissionArgument createPermissionParameter(CommandElement wrapped, String permission) {
        return new NucleusRequirePermissionArgument(wrapped, this, permission);
    }

}
