/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;

@RegisterPermissions
public class CommandSpyPermissions {

    private CommandSpyPermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "commandspy" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_COMMANDSPY = "nucleus.commandspy.base";

    @PermissionMetadata(descriptionKey = "permission.exempt.cooldown", replacements = { "commandspy" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COOLDOWN_COMMANDSPY = "nucleus.commandspy.exempt.cooldown";

    @PermissionMetadata(descriptionKey = "permission.exempt.cost", replacements = { "commandspy" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COST_COMMANDSPY = "nucleus.commandspy.exempt.cost";

    @PermissionMetadata(descriptionKey = "permission.commandspy.exempt.target", level = SuggestedLevel.ADMIN)
    public static final String COMMANDSPY_EXEMPT_TARGET = "nucleus.commandspy.exempt.target";

    @PermissionMetadata(descriptionKey = "permission.exempt.warmup", replacements = { "commandspy" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_WARMUP_COMMANDSPY = "nucleus.commandspy.exempt.warmup";

}
