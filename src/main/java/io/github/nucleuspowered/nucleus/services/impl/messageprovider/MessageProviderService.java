package io.github.nucleuspowered.nucleus.services.impl.messageprovider;

import io.github.nucleuspowered.nucleus.services.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import java.util.Locale;

import javax.inject.Singleton;

@Singleton
public class MessageProviderService implements IMessageProviderService {

    @Override
    public Locale getDefaultLocale() {
        return Sponge.getServer().getConsole().getLocale();
    }

    @Override
    public Text getMessageFor(Locale locale, String key, Text... args) {
        return null;
    }
}
