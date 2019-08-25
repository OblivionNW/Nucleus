/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.annotation;

import io.github.nucleuspowered.nucleus.internal.command.requirements.CommandModifiers;

public @interface NucleusCommand {

    /**
     * The command root
     *
     * @return The command
     */
    String[] value();

    /**
     * Sets whether the command should register it's executor. This can be false if there are only child commands.
     *
     * @return <code>true</code> if the executor should be registered.
     */
    boolean hasExecutor() default true;

    /**
     * The basic permissions required for a command
     *
     * @return The permissions required
     */
    String[] basePermission();

    /**
     * The modifiers for this command, if any
     *
     * @return The modifiers.
     */
    CommandModifier[] modifiers() default {};

    /**
     * Whether the command has a help subcommand
     *
     * @return Whether it has one
     */
    boolean hasHelpCommand() default true;

}
