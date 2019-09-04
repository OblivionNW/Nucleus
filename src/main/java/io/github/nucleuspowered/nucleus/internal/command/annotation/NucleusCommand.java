/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NucleusCommand {

    /**
     * The command strings.
     *
     * @return The command.
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

    /**
     * The basic command description key. Must have a corresponding
     * <code>[key].desc</code> entry in <code>commands.properties</code>,
     * optionally having a <code>[key].extended</code>.
     *
     * @return The root of the key
     */
    String commandDescriptionKey();

}
