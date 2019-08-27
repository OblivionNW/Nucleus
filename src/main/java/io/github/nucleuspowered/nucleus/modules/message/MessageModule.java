/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.messages.MessageProvider;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;
import io.github.nucleuspowered.nucleus.modules.message.commands.SocialSpyCommand;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import org.spongepowered.api.text.Text;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

import java.util.List;

@ModuleData(id = MessageModule.ID, name = "Message")
public class MessageModule extends ConfigurableModule<MessageConfigAdapter> {

    public static final String ID = "message";

    @Override
    public MessageConfigAdapter createAdapter() {
        return new MessageConfigAdapter();
    }

    @Override public void performEnableTasks() {
        createSeenModule(SocialSpyCommand.class, (cs, user) -> {
            MessageHandler handler = Nucleus.getNucleus().getInternalServiceManager().getServiceUnchecked(MessageHandler.class);
            boolean socialSpy = handler.isSocialSpy(user);
            boolean msgToggle = Nucleus.getNucleus().getInternalServiceManager().getServiceUnchecked(UserPreferenceService.class)
                    .getUnwrapped(user.getUniqueId(), MessageUserPrefKeys.RECEIVING_MESSAGES);
            MessageProvider mp = Nucleus.getNucleus().getMessageProvider();
            List<Text> lt = Lists.newArrayList(
                mp.getTextMessageWithFormat("seen.socialspy",
                    mp.getMessageWithFormat("standard.yesno." + Boolean.toString(socialSpy).toLowerCase())));

            getConfigAdapter().ifPresent(x -> lt.add(
                mp.getTextMessageWithFormat("seen.socialspylevel", String.valueOf(Util.getPositiveIntOptionFromSubject(user, MessageHandler.socialSpyOption).orElse(0)))
            ));

            lt.add(mp.getTextMessageWithFormat("seen.msgtoggle",
                    mp.getMessageWithFormat("standard.yesno." + Boolean.toString(msgToggle).toLowerCase())));

            return lt;
        });
    }
}
