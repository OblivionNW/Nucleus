package io.github.nucleuspowered.nucleus.internal.annotations.command;

import io.github.nucleuspowered.nucleus.internal.command.CommandTypeFlags;

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
     * The warmup exempt permission for a command
     *
     * @return The warmup exempt permission
     */
    String warmupExemptPermission() default "";

    /**
     * The cooldown exempt permission for a command
     *
     * @return The cooldown exempt permission
     */
    String cooldownExemptPermission() default "";

    /**
     * The cost exempt permission for a command
     *
     * @return The cost exempt permission
     */
    String costExemptPermission() default "";

    /**
     * Whether the command has a help subcommand
     *
     * @return Whether it has one
     */
    boolean hasHelpCommand() default true;

    /**
     * Flags
     *
     * @return The command flags
     */
    CommandTypeFlags[] commandFlags() default {};

}
