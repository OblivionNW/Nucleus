/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.services.impl.messagetoken.NucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.services.impl.messagetoken.NucleusTextTemplateMessageSender;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@RunAsync
@NoModifiers
@Permissions
@RegisterCommand({"tellplain", "plaintell", "ptell"})
@NonnullByDefault
public class TellPlainCommand extends AbstractCommand<CommandSource> {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                NucleusParameters.MANY_PLAYER_OR_CONSOLE,
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args, Cause cause) throws Exception {
        try {
            new NucleusTextTemplateMessageSender(NucleusTextTemplateFactory.createFromString(
                        args.requireOne(NucleusParameters.Keys.MESSAGE)), src)
                    .send(args.getAll(NucleusParameters.Keys.PLAYER_OR_CONSOLE), cause);
        } catch (Throwable throwable) {
            if (Nucleus.getNucleus().isDebugMode()) {
                throwable.printStackTrace();
            }

            throw ReturnMessageException.fromKey("command.tellplain.failed");
        }
        return CommandResult.success();
    }
}
