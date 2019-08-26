/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.permissions;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.services.IPermissionCheckService;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfigAdapter;
import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import org.slf4j.event.Level;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class PermissionResolverImpl implements IPermissionCheckService, SimpleReloadable {

    public final static PermissionResolverImpl INSTANCE = new PermissionResolverImpl();

    private PermissionResolverImpl() {}

    private boolean reportFailedChecks = false;

    private final Map<String, String> permissions = new HashMap<>();
    private final Map<Predicate<String>, String> permissionPredicates = new HashMap<>();

    private final Set<String> failedChecks = new HashSet<>();

    private boolean init = false;

    public void registerPermissions() {
        Preconditions.checkState(!this.init);
        this.init = true;
        PermissionService ps = Sponge.getServiceManager().provide(PermissionService.class).orElse(null);
        boolean isPresent = ps != null;

        for (Map.Entry<String, PermissionInformation> entry : Nucleus.getNucleus().getPermissionRegistry().getPermissions().entrySet()) {
            PermissionInformation info = entry.getValue();
            SuggestedLevel level = info.level;
            registerPermission(entry.getKey(), level);
            if (isPresent && level.getRole() != null && info.isNormal) {
                    ps.newDescriptionBuilder(Nucleus.getNucleus()).assign(level.getRole(), true)
                            .description(info.description).id(entry.getKey()).register();
            }
        }
    }

    public void registerPermission(String perm, SuggestedLevel level) {
        if (this.permissions.containsKey(perm)) {
            return;
        }

        String l = level.getPermission();
        if (l != null) {
            this.permissions.put(perm, l);
        }
    }

    public void registerPermissionPredicate(Predicate<String> perm, SuggestedLevel level) {
        String l = level.getPermission();
        if (l != null) {
            this.permissionPredicates.put(perm, l);
        }
    }

    @Override
    public boolean hasPermission(Subject subject, String permission) {
        if (permission.startsWith("nucleus")) {
            Tristate tristate = subject.getPermissionValue(subject.getActiveContexts(), permission);
            if (tristate == Tristate.UNDEFINED) {
                @Nullable String result = this.permissions.get(permission);
                if (result != null) { // check the "parent" perm
                    return subject.hasPermission(result);
                }

                for (Map.Entry<Predicate<String>, String> entry : this.permissionPredicates.entrySet()) {
                    if (entry.getKey().test(permission)) {
                        return subject.hasPermission(entry.getValue()); // check the "parent" perm
                    }
                }

                // if we get here, no registered permissions were found
                // therefore, warn
                if (this.reportFailedChecks && this.failedChecks.add(permission)) {
                    PrettyPrinter printer = new PrettyPrinter(80);
                    printer.add("Nucleus Permission Not Registered").centre().hr();
                    printer.add("Nucleus has not registered a permission properly. This is an error in Nucleus - please report to the Nucleus "
                            + "github.");
                    printer.hr();
                    printer.add("Permission: %s", permission);
                    printer.log(Nucleus.getNucleus().getLogger(), Level.WARN);
                }

                // guarantees that the subject default is selected.
                return subject.hasPermission(permission);
            }

            return tristate.asBoolean();
        }

        return subject.hasPermission(permission);
    }

    @Override
    public void onReload() throws Exception {
        this.reportFailedChecks = Nucleus.getNucleus().getInternalServiceManager()
                .getService(CoreConfigAdapter.class)
                .map(x -> x.getNodeOrDefault().isPrintWhenPermissionIsntRegistered())
                .orElse(true);

    }
}
