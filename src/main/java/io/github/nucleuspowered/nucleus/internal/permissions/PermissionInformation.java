/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.permissions;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import org.spongepowered.api.text.Text;

public class PermissionInformation {

    public final Text description;
    public final String plainDescription;
    public final SuggestedLevel level;
    public final boolean isOre = true;
    public final boolean isNormal = true;
    public final String key;
    public final String[] r;

    public static PermissionInformation getWithTranslation(String key, SuggestedLevel level) {
        return new PermissionInformation(key, new String[0],
                Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat(key), level);
    }

    public static PermissionInformation getWithTranslation(String key, SuggestedLevel level, String... r) {
        return new PermissionInformation(key, r,
                Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat(key), level);
    }

    public PermissionInformation(String key, String[] r, String description, SuggestedLevel level) {
        this(key, r, Text.of(description), level);
    }

    private PermissionInformation(String key, String[] r, Text description, SuggestedLevel level) {
        this.key = key;
        this.r = r;
        this.description = description;
        this.plainDescription = description.toPlain();
        this.level = level;
    }
}
