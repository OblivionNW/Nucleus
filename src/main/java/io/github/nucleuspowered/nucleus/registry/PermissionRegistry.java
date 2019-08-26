/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.registry;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionMetadata;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import org.slf4j.event.Level;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class PermissionRegistry {

    public final static PermissionRegistry INSTANCE = new PermissionRegistry();
    private final Set<String> failedChecks = new HashSet<>();

    private PermissionRegistry() { }

    private static final String PERMISSION_PREFIX = "nucleus.";

    private final Map<String, Metadata> metadataMap = new HashMap<>();
    private final Map<String, Metadata> prefixMetadataMap = new HashMap<>();

    private boolean init = false;

    public void registerDescriptions() {
        Preconditions.checkState(!this.init);
        this.init = true;
        PermissionService ps = Sponge.getServiceManager().provide(PermissionService.class).orElse(null);
        boolean isPresent = ps != null;

        for (Map.Entry<String, Metadata> entry : this.metadataMap.entrySet()) {
            SuggestedLevel level = entry.getValue().suggestedLevel;
            if (isPresent && level.getRole() != null) {
                ps.newDescriptionBuilder(Nucleus.getNucleus())
                        .assign(level.getRole(), true)
                        .description(Text.of(entry.getValue().description))
                        .id(entry.getKey()).register();
            }
        }
    }

    public void register(String permission, PermissionMetadata metadata) {
        Metadata m = new Metadata(permission, metadata);
        if (metadata.isPrefix()) {
            this.prefixMetadataMap.put(permission.toLowerCase(), m);
        } else {
            this.metadataMap.put(permission.toLowerCase(), m);
        }
    }

    public boolean hasPermission(Subject subject, String permission, boolean checkRole) {
        if (checkRole && permission.startsWith("nucleus")) {
            Tristate tristate = subject.getPermissionValue(subject.getActiveContexts(), permission);
            if (tristate == Tristate.UNDEFINED) {
                @Nullable Metadata result = this.metadataMap.get(permission);
                if (result != null) { // check the "parent" perm
                    String perm = result.suggestedLevel.getPermission();
                    if (perm == null) {
                        return false;
                    } else {
                        return subject.hasPermission(perm);
                    }
                }

                for (Map.Entry<String, Metadata> entry : this.prefixMetadataMap.entrySet()) {
                    if (permission.startsWith(entry.getKey())) {
                        String perm = entry.getValue().suggestedLevel.getPermission();
                        if (perm == null) {
                            return false;
                        } else {
                            return subject.hasPermission(perm);
                        }
                    }
                }

                // if we get here, no registered permissions were found
                // therefore, warn
                if (this.failedChecks.add(permission)) {
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

    public static class Metadata {

        private final String description;
        private final String permission;
        private final SuggestedLevel suggestedLevel;
        private final boolean isPrefix;
        private final String[] replacements;

        Metadata(String permission, PermissionMetadata metadata) {
            this(
                    metadata.descriptionKey(),
                    metadata.replacements(),
                    permission,
                    metadata.level(),
                    metadata.isPrefix()
            );
        }

        Metadata(String description, String[] replacements, String permission, SuggestedLevel suggestedLevel, boolean isPrefix) {
            this.description = description;
            this.replacements = replacements;
            this.permission = PERMISSION_PREFIX + permission.toLowerCase();
            this.suggestedLevel = suggestedLevel;
            this.isPrefix = isPrefix;
        }

        public boolean isPrefix() {
            return this.isPrefix;
        }

        public SuggestedLevel getSuggestedLevel() {
            return this.suggestedLevel;
        }

        public String getDescription() {
            return Nucleus.getNucleus().getMessageProvider().getMessageWithFormat(this.description, this.replacements);
        }

        public String getPermission() {
            return this.permission;
        }

    }

}
