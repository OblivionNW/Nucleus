/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warn;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;

@RegisterPermissions
public class WarnPermissions {
    private WarnPermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "checkwarnings" }, level = SuggestedLevel.MOD)
    public static final String BASE_CHECKWARNINGS = "nucleus.checkwarnings.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "clearwarnings" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_CLEARWARNINGS = "nucleus.clearwarnings.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "removewarning" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_REMOVEWARNING = "nucleus.removewarning.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "warn" }, level = SuggestedLevel.MOD)
    public static final String BASE_WARN = "nucleus.warn.base";

    @PermissionMetadata(descriptionKey = "permission.warn.exempt.length", level = SuggestedLevel.MOD)
    public static final String WARN_EXEMPT_LENGTH = "nucleus.warn.exempt.length";

    @PermissionMetadata(descriptionKey = "permission.warn.exempt.target", level = SuggestedLevel.MOD)
    public static final String WARN_EXEMPT_TARGET = "nucleus.warn.exempt.target";

    @PermissionMetadata(descriptionKey = "permission.warn.notify", level = SuggestedLevel.MOD)
    public static final String WARN_NOTIFY = "nucleus.warn.notify";

    @PermissionMetadata(descriptionKey = "permission.warn.showonlogin", level = SuggestedLevel.MOD)
    public static final String WARN_SHOWONLOGIN = "nucleus.warn.showonlogin";

}
