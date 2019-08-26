package io.github.nucleuspowered.nucleus.services.impl.permissioncheck;

import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.registry.PermissionRegistry;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.IPermissionCheckService;
import org.spongepowered.api.service.permission.Subject;

public class PermissionCheckService implements IPermissionCheckService, Reloadable {

    private boolean useRole = false;

    @Override public boolean hasPermission(Subject permissionSubject, String permission) {
        return PermissionRegistry.INSTANCE.hasPermission(permissionSubject, permission, this.useRole);
    }

    @Override public void onReload(INucleusServiceCollection serviceCollection) {

    }
}
