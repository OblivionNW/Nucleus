/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.annotation;

import io.github.nucleuspowered.nucleus.internal.command.requirements.CommandModifiers;
import org.spongepowered.api.command.CommandSource;

public @interface CommandModifier {

    CommandModifiers value();

    String exemptPermission() default "";

    Class<? extends CommandSource> target() default CommandSource.class;

    boolean configGenerationOnly() default false;

}
