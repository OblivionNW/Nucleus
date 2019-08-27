/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chatlogger;

import static io.github.nucleuspowered.nucleus.modules.chatlogger.ChatLoggerModule.ID;

import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.chatlogger.config.ChatLoggingConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = ID, name = "Chat Logger")
public class ChatLoggerModule extends ConfigurableModule<ChatLoggingConfigAdapter> {

    public static final String ID = "chat-logger";

    @Override public ChatLoggingConfigAdapter createAdapter() {
        return new ChatLoggingConfigAdapter();
    }
}
