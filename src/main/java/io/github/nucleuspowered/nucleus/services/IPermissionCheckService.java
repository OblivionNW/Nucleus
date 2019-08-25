/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import org.spongepowered.api.service.permission.Subject;

public interface IPermissionCheckService {

    boolean hasPermission(Subject permissionSubject, String permission);

}
