/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.serverlist;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;

@RegisterPermissions
public class ServerListPermissions {

    private ServerListPermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "serverlist" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_SERVERLIST = "nucleus.serverlist.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "serverlist message" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_SERVERLIST_MESSAGE = "nucleus.serverlist.message.base";

    @PermissionMetadata(descriptionKey = "permission.exempt.cooldown", replacements = { "serverlist message" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COOLDOWN_SERVERLIST_MESSAGE = "nucleus.serverlist.message.exempt.cooldown";

    @PermissionMetadata(descriptionKey = "permission.exempt.cost", replacements = { "serverlist message" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COST_SERVERLIST_MESSAGE = "nucleus.serverlist.message.exempt.cost";

    @PermissionMetadata(descriptionKey = "permission.exempt.warmup", replacements = { "serverlist message" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_WARMUP_SERVERLIST_MESSAGE = "nucleus.serverlist.message.exempt.warmup";

}
