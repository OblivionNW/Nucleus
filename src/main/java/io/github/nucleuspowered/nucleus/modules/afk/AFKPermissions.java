/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;

@RegisterPermissions
public class AFKPermissions {
    private AFKPermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "afk" }, level = SuggestedLevel.USER)
    public static final String BASE_AFK = "nucleus.afk.base";

    @PermissionMetadata(descriptionKey = "permission.afk.exempt.kick", level = SuggestedLevel.ADMIN)
    public static final String AFK_EXEMPT_KICK = "nucleus.afk.exempt.kick";

    @PermissionMetadata(descriptionKey = "permission.afk.exempt.toggle", level = SuggestedLevel.NONE)
    public static final String AFK_EXEMPT_TOGGLE = "nucleus.afk.exempt.toggle";

    @PermissionMetadata(descriptionKey = "permission.afk.notify", level = SuggestedLevel.ADMIN)
    public static final String AFK_NOTIFY = "nucleus.afk.notify";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "afkkick" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_AFKKICK = "nucleus.afkkick.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "afkrefresh" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_AFKREFRESH = "nucleus.afkrefresh.base";

    @PermissionMetadata(descriptionKey = "permission.exempt.cooldown", replacements = { "afkrefresh" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COOLDOWN_AFKREFRESH = "nucleus.afkrefresh.exempt.cooldown";

    @PermissionMetadata(descriptionKey = "permission.exempt.cost", replacements = { "afkrefresh" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COST_AFKREFRESH = "nucleus.afkrefresh.exempt.cost";

    @PermissionMetadata(descriptionKey = "permission.exempt.warmup", replacements = { "afkrefresh" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_WARMUP_AFKREFRESH = "nucleus.afkrefresh.exempt.warmup";

}