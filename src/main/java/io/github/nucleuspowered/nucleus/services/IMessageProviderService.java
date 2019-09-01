/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.MessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

@ImplementedBy(MessageProviderService.class)
public interface IMessageProviderService {

    Locale getDefaultLocale();

    Text getMessageFor(Locale locale, String key);

    Text getMessageFor(Locale locale, String key, Text... args);

    Text getMessageFor(Locale locale, String key, Object... replacements);

    Text getMessageFor(Locale locale, String key, String... replacements);

    String getMessageString(Locale locale, String key, String... replacements);

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

    default Text getMessage(String key) {
        return getMessageForDefault(key);
    }

    default Text getMessage(String key, String... replacements) {
        return getMessageFor(getDefaultLocale(), key, replacements);
    }

    default Text getMessage(String key, Text... replacements) {
        return getMessageFor(getDefaultLocale(), key, replacements);
    }

    default Text getMessage(String key, Object... replacements) {
        return getMessageFor(getDefaultLocale(), key, replacements);
    }

    default String getMessageString(String key, String... replacements) {
        return getMessageString(getDefaultLocale(), key, replacements);
    }

    default Text getMessageFor(CommandSource source, String key, Object... replacements) {
        return getMessageFor(source.getLocale(), key, replacements);
    }

    default void sendMessageTo(CommandSource receiver, String key) {
        receiver.sendMessage(getMessageFor(receiver.getLocale(), key));
    }

    default void sendMessageTo(CommandSource receiver, String key, Object... replacements) {
        receiver.sendMessage(getMessageFor(receiver.getLocale(), key, replacements));
    }

    default void sendMessageTo(CommandSource receiver, String key, Text... replacements) {
        receiver.sendMessage(getMessageFor(receiver.getLocale(), key, replacements));
    }

    default void sendMessageTo(CommandSource receiver, String key, String... replacements) {
        receiver.sendMessage(getMessageFor(receiver.getLocale(), key, replacements));
    }

    default void sendMessageTo(Supplier<CommandSource> receiver, String key, String... replacements) {
        sendMessageTo(receiver.get(), key, replacements);
    }

}
