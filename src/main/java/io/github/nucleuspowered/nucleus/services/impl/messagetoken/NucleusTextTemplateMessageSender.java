/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messagetoken;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.events.NucleusTextTemplateEvent;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class NucleusTextTemplateMessageSender {

    private final NucleusTextTemplate textTemplate;
    private final CommandSource sender;

    public NucleusTextTemplateMessageSender(NucleusTextTemplate textTemplate, CommandSource sender) {
        this.textTemplate = textTemplate;
        this.sender = sender;
    }

    public boolean send(Cause cause) {
        List<CommandSource> members = Lists.newArrayList(Sponge.getServer().getConsole());
        members.addAll(Sponge.getServer().getOnlinePlayers());
        return send(members, true, cause);
    }

    public boolean send(Collection<CommandSource> source, Cause cause) {
        return send(source, false, cause);
    }

    private boolean send(Collection<CommandSource> source, boolean isBroadcast, Cause cause) {
        NucleusTextTemplateEvent event;
        if (isBroadcast) {
            event = new NucleusTextTemplateEventImpl.Broadcast(
                    this.textTemplate,
                    source,
                    cause
            );
        } else {
            event = new NucleusTextTemplateEventImpl(
                    this.textTemplate,
                    source,
                    cause
            );
        }

        if (Sponge.getEventManager().post(event)) {
            return false;
        }

        NucleusTextTemplate template = event.getMessage();
        if (!template.containsTokens()) {
            Text text = this.textTemplate.getForCommandSource(Sponge.getServer().getConsole());
            event.getRecipients().forEach(x -> x.sendMessage(text));
        } else {
            Map<String, Function<CommandSource, Optional<Text>>> m = Maps.newHashMap();
            m.put("sender", cs -> Nucleus.getNucleus().getMessageTokenService().applyPrimaryToken("displayname", this.sender));
            event.getRecipients().forEach(x -> x.sendMessage(this.textTemplate.getForCommandSource(x, m, null)));
        }
        return true;
    }
}
