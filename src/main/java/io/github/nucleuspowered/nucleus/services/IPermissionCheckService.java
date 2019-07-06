package io.github.nucleuspowered.nucleus.services;

import org.spongepowered.api.service.permission.Subject;

public interface IPermissionCheckService {

    boolean hasPermission(Subject permissionSubject, String permission);

}
