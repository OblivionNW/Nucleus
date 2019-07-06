package io.github.nucleuspowered.nucleus.services;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.Locale;

public interface IMessageProviderService {

    Locale getDefaultLocale();

    default Text getMessageForDefault(String key, Text... args) {
        return getMessageFor(getDefaultLocale(), key, args);
    }

    default Text getMessageFor(CommandSource source, String key) {
        return getMessageFor(source.getLocale(), key);
    }

    default Text getMessageFor(CommandSource source, String key, Text... args) {
        return getMessageFor(source.getLocale(), key, args);
    }

    default Text getMessageFor(CommandSource source, String key, String... args) {
        Text[] t = Arrays.stream(args).map(TextSerializers.FORMATTING_CODE::deserialize).toArray(Text[]::new);
        return getMessageFor(source.getLocale(), key, t);
    }

    Text getMessageFor(Locale locale, String key, Text... args);

    default void sendTo(CommandSource source, String key, Text... args) {
        source.sendMessage(getMessageFor(source, key, args));
    }

}
