/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat;

import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfigAdapter;
import uk.co.drnaylor.quickstart.annotations.ModuleData;

@ModuleData(id = ChatModule.ID, name = "Chat")
public class ChatModule extends ConfigurableModule<ChatConfigAdapter> {

    public final static String ID = "chat";

    @Override
    public ChatConfigAdapter createAdapter() {
        return new ChatConfigAdapter();
    }

}
