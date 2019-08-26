/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import io.github.nucleuspowered.nucleus.services.impl.permissioncheck.PermissionCheckService;
import org.spongepowered.api.service.permission.Subject;

@FunctionalInterface
public interface IPermissionCheckService {

    IPermissionCheckService SIMPLE = new PermissionCheckService();

    boolean hasPermission(Subject subject, String permission);

}
