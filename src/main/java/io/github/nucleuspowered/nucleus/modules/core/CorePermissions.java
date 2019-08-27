/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.core;

import io.github.nucleuspowered.nucleus.annotationprocessor.RegisterPermissions;
import io.github.nucleuspowered.nucleus.services.impl.permission.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;

@RegisterPermissions
public class CorePermissions {
    private CorePermissions() {
        throw new AssertionError("Nope");
    }

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "commandinfo" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_COMMANDINFO = "nucleus.commandinfo.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS = "nucleus.nucleus.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus clearcache" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_CLEARCACHE = "nucleus.nucleus.clearcache.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus debug" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_DEBUG = "nucleus.nucleus.debug.base";

    @PermissionMetadata(descriptionKey = "permission.exempt.cooldown", replacements = { "nucleus debug" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COOLDOWN_NUCLEUS_DEBUG = "nucleus.nucleus.debug.exempt.cooldown";

    @PermissionMetadata(descriptionKey = "permission.exempt.cost", replacements = { "nucleus debug" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COST_NUCLEUS_DEBUG = "nucleus.nucleus.debug.exempt.cost";

    @PermissionMetadata(descriptionKey = "permission.exempt.warmup", replacements = { "nucleus debug" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_WARMUP_NUCLEUS_DEBUG = "nucleus.nucleus.debug.exempt.warmup";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "debug getuuids" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_DEBUG_GETUUIDS = "nucleus.nucleus.debug.getuuids.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "debug refreshuniquevisitors" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_DEBUG_REFRESHUNIQUEVISITORS = "nucleus.nucleus.debug.refreshuniquevisitors.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "debug setsession" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_DEBUG_SETSESSION = "nucleus.nucleus.debug.setsession.base";

    @PermissionMetadata(descriptionKey = "permission.exempt.cooldown", replacements = { "debug setsession" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COOLDOWN_DEBUG_SETSESSION = "nucleus.nucleus.debug.setsession.exempt.cooldown";

    @PermissionMetadata(descriptionKey = "permission.exempt.cost", replacements = { "debug setsession" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_COST_DEBUG_SETSESSION = "nucleus.nucleus.debug.setsession.exempt.cost";

    @PermissionMetadata(descriptionKey = "permission.exempt.warmup", replacements = { "debug setsession" }, level = SuggestedLevel.ADMIN)
    public static final String EXEMPT_WARMUP_DEBUG_SETSESSION = "nucleus.nucleus.debug.setsession.exempt.warmup";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus getuser" }, level = SuggestedLevel.NONE)
    public static final String BASE_NUCLEUS_GETUSER = "nucleus.nucleus.getuser.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus info" }, level = SuggestedLevel.OWNER)
    public static final String BASE_NUCLEUS_INFO = "nucleus.nucleus.info.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus itemalias" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_ITEMALIAS = "nucleus.nucleus.itemalias.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "itemalias clear" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_ITEMALIAS_CLEAR = "nucleus.nucleus.itemalias.clear.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "itemalias remove" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_ITEMALIAS_REMOVE = "nucleus.nucleus.itemalias.remove.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "itemalias set" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_ITEMALIAS_SET = "nucleus.nucleus.itemalias.set.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus printperms" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_PRINTPERMS = "nucleus.nucleus.printperms.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus rebuildusercache" }, level = SuggestedLevel.OWNER)
    public static final String BASE_NUCLEUS_REBUILDUSERCACHE = "nucleus.nucleus.rebuildusercache.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus reload" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_RELOAD = "nucleus.nucleus.reload.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus resetuser" }, level = SuggestedLevel.OWNER)
    public static final String BASE_NUCLEUS_RESETUSER = "nucleus.nucleus.resetuser.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus save" }, level = SuggestedLevel.ADMIN)
    public static final String BASE_NUCLEUS_SAVE = "nucleus.nucleus.save.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus setupperms" }, level = SuggestedLevel.OWNER)
    public static final String BASE_NUCLEUS_SETUPPERMS = "nucleus.nucleus.setupperms.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nucleus update-messages" }, level = SuggestedLevel.OWNER)
    public static final String BASE_NUCLEUS_UPDATE_MESSAGES = "nucleus.nucleus.update-messages.base";

    @PermissionMetadata(descriptionKey = "permission.base", replacements = { "nuserprefs" }, level = SuggestedLevel.USER)
    public static final String BASE_NUSERPREFS = "nucleus.userprefs.base";

    @PermissionMetadata(descriptionKey = "permission.others", replacements = { "nuserprefs" }, level = SuggestedLevel.ADMIN)
    public static final String OTHERS_NUSERPREFS = "nucleus.userprefs.others";

}