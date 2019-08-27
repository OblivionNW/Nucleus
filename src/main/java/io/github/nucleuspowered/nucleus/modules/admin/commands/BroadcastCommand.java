/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.NoModifiers;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.services.impl.messagetoken.NucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.services.impl.messagetoken.NucleusTextTemplateMessageSender;
import io.github.nucleuspowered.nucleus.modules.admin.AdminModule;
import io.github.nucleuspowered.nucleus.modules.admin.config.AdminConfig;
import io.github.nucleuspowered.nucleus.modules.admin.config.AdminConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.admin.config.BroadcastConfig;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@RunAsync
@NoModifiers
@Permissions
@RegisterCommand({ "broadcast", "bcast", "bc" })
@EssentialsEquivalent({"broadcast", "bcast"})
@NonnullByDefault
public class BroadcastCommand extends AbstractCommand<CommandSource> implements SimpleReloadable {
    private BroadcastConfig bc = new BroadcastConfig();

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args, Cause cause) {
        String m = args.requireOne(NucleusParameters.Keys.MESSAGE);

        Text p = this.bc.getPrefix().getForCommandSource(src);
        Text s = this.bc.getSuffix().getForCommandSource(src);

        NucleusTextTemplate textTemplate =
                NucleusTextTemplateFactory.createFromAmpersandString(m, p, s);

        new NucleusTextTemplateMessageSender(textTemplate, src).send(cause);
        return CommandResult.success();
    }

    @Override public void onReload() {
        this.bc = Nucleus.getNucleus()
            .getConfigValue(AdminModule.ID, AdminConfigAdapter.class, AdminConfig::getBroadcastMessage)
            .orElseGet(BroadcastConfig::new);
    }
}
