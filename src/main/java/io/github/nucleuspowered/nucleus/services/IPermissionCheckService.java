/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.permissioncheck.PermissionCheckService;
import org.spongepowered.api.service.permission.Subject;

@FunctionalInterface
@ImplementedBy(PermissionCheckService.class)
public interface IPermissionCheckService {

    boolean hasPermission(Subject subject, String permission);

}
