/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messageprovider;

import io.github.nucleuspowered.nucleus.internal.interfaces.Reloadable;
import io.github.nucleuspowered.nucleus.services.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.PropertiesMessageRepository;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository.UTF8Control;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageProviderService implements IMessageProviderService, Reloadable {

    private static final String MESSAGES_BUNDLE = "assets.nucleus.messages";

    private static final String MESSAGES_BUNDLE_RESOURCE_LOC = "/assets/nucleus/messages.properties.{0}";

    private final INucleusServiceCollection serviceCollection;

    private Locale defaultLocale = Sponge.getServer().getConsole().getLocale();
    private boolean useMessagesFile;
    private boolean useClientLocalesWhenPossible;

    private final PropertiesMessageRepository defaultMessagesResource;

    private final Map<Locale, PropertiesMessageRepository> messagesMap = new HashMap<>();

    @Inject
    MessageProviderService(INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        serviceCollection.reloadableService().registerReloadable(this);
        this.defaultMessagesResource = new PropertiesMessageRepository(
                serviceCollection.playerDisplayNameService(),
                ResourceBundle.getBundle(MESSAGES_BUNDLE, Locale.ROOT, UTF8Control.INSTANCE));
    }

    @Override
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override public Text getMessageFor(Locale locale, String key) {
        return getMessagesResource(locale).getText(key);
    }

    @Override
    public Text getMessageFor(Locale locale, String key, Text... args) {
        return getMessagesResource(locale).getText(key, args);
    }

    @Override public Text getMessageFor(Locale locale, String key, Object... replacements) {
        return getMessagesResource(locale).getText(key, replacements);
    }

    @Override public Text getMessageFor(Locale locale, String key, String... replacements) {
        return getMessagesResource(locale).getText(key, replacements);
    }

    @Override public String getMessageString(Locale locale, String key, String... replacements) {
        return getMessagesResource(locale).getString(key, replacements);
    }

    @Override public void onReload(INucleusServiceCollection serviceCollection) {
        // TODO: Config
    }

    private PropertiesMessageRepository getMessagesResource(Locale locale) {
        final Locale toUse;
        if (this.useClientLocalesWhenPossible) {
            toUse = locale;
        } else {
            toUse = this.defaultLocale;
        }

        return this.messagesMap.computeIfAbsent(locale, key -> {
            if (getClass().getClassLoader().getResource(MessageFormat.format(MESSAGES_BUNDLE_RESOURCE_LOC, locale.toLanguageTag())) != null) {
                // it exists
                return new PropertiesMessageRepository(
                        this.serviceCollection.playerDisplayNameService(),
                        ResourceBundle.getBundle(MESSAGES_BUNDLE, locale, UTF8Control.INSTANCE));
            } else {
                return this.defaultMessagesResource;
            }
        });
    }

}
