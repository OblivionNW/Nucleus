/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.listeners;

import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.NameUtil;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.chat.NucleusNoFormatChannel;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.interfaces.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.legacy.MessageProvider;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.services.IPermissionService;
import io.github.nucleuspowered.nucleus.internal.text.TextParsingUtils;
import io.github.nucleuspowered.nucleus.modules.chat.ChatModule;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfig;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatTemplateConfig;
import io.github.nucleuspowered.nucleus.modules.chat.util.TemplateUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.transform.SimpleTextFormatter;
import org.spongepowered.api.text.transform.SimpleTextTemplateApplier;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * A listener that modifies all chat messages. Uses the
 * {@link NucleusMessageTokenService}, which
 * should be used if tokens need to be registered.
 */
public class ChatListener implements SimpleReloadable, ListenerBase.Conditional {

    private static final String prefix = PermissionRegistry.PERMISSIONS_PREFIX + "chat.";

    // Order is important here!
    private static final Map<String, PermissionInformation> permissionToDesc = Maps.newHashMap();
    private static final Map<String, Tuple<String[], Function<String, String>>> replacements = createReplacements();

    private static Map<String, Tuple<String[], Function<String, String>>> createReplacements() {
        Map<String, Tuple<String[], Function<String, String>>> t = new HashMap<>();

        MessageProvider mp = Nucleus.getNucleus().getMessageProvider();

        BiFunction<String, String, String> fss = (key, s) -> s.replaceAll("[&]+[" + key.toLowerCase() + key.toUpperCase() + "]", "");
        NameUtil.getColours().forEach((key, value) -> {
            t.put("&" + key, Tuple.of(
                new String[]{ prefix + "colour." + value.getName(), prefix + "color." + value.getName() },
                s -> fss.apply(key.toString(), s)));

            permissionToDesc.put(prefix + "colour." + value.getName(),
                    PermissionInformation.getWithTranslation("permission.chat.colourspec",
                            SuggestedLevel.ADMIN,
                            value.getName().toLowerCase(), key.toString()));
            permissionToDesc.put(prefix + "color." + value.getName(),
                    PermissionInformation.getWithTranslation("permission.chat.colorspec",
                            SuggestedLevel.ADMIN,
                            value.getName().toLowerCase(), key.toString()));
        });

        NameUtil.getStyleKeys().entrySet().stream().filter(x -> x.getKey() != 'k').forEach((k) -> {
            t.put("&" + k.getKey(), Tuple.of(new String[] { prefix + "style." + k.getValue().toLowerCase() },
                    s -> fss.apply(k.getKey().toString(), s)));
            permissionToDesc.put(prefix + "style." + k.getValue().toLowerCase(),
                    PermissionInformation.getWithTranslation("permission.chat.stylespec",
                            SuggestedLevel.ADMIN,
                            k.getValue().toLowerCase(), k.toString()));
        });

        t.put("&k", Tuple.of(new String[] { prefix + "magic" }, s -> s.replaceAll("[&]+[kK]", "")));

        return t;
    }

    public static String stripPermissionless(Subject source, String message) {
        if (message.contains("&")) {
            String m = message.toLowerCase();
            IPermissionService resolver = Nucleus.getNucleus().getPermissionResolver();
            for (Map.Entry<String, Tuple<String[], Function<String, String>>> r : replacements.entrySet()) {
                if (m.contains(r.getKey()) && Arrays.stream(r.getValue().getFirst()).noneMatch(x -> resolver.hasPermission(source, x))) {
                    message = r.getValue().getSecond().apply(message);
                }
            }
        }

        return message;
    }

    // --- Listener Proper
    private ChatConfig chatConfig = null;
    private final TemplateUtil templateUtil = Nucleus.getNucleus().getInternalServiceManager().getServiceUnchecked(TemplateUtil.class);

    @Override
    public Map<String, PermissionInformation> getPermissions() {
        Map<String, PermissionInformation> mp = new HashMap<>();
        mp.put(prefix + "color", PermissionInformation.getWithTranslation("permission.chat.color", SuggestedLevel.ADMIN));
        mp.put(prefix + "style", PermissionInformation.getWithTranslation("permission.chat.style", SuggestedLevel.ADMIN));
        mp.put(prefix + "magic", PermissionInformation.getWithTranslation("permission.chat.magic", SuggestedLevel.ADMIN));
        mp.put(prefix + "url", PermissionInformation.getWithTranslation("permission.chat.urls", SuggestedLevel.ADMIN));
        permissionToDesc.putAll(mp);
        return mp;
    }

    // We do this first so that other plugins can alter it later if needs be.
    @Listener(order = Order.EARLY, beforeModifications = true)
    public void onPlayerChat(MessageChannelEvent.Chat event) {
        Util.onPlayerSimulatedOrPlayer(event, this::onPlayerChatInternal);
    }

    private boolean shouldNotFormat(MessageChannelEvent.Chat event) {
        if (!event.getContext().get(EventContexts.SHOULD_FORMAT_CHANNEL).orElse(true)) {
            return true;
        }

        return event.getChannel().map(this::shouldNotFormat).orElseGet(() -> shouldNotFormat(event.getOriginalChannel()));
    }

    private boolean shouldNotFormat(MessageChannel channel) {
        return channel instanceof NucleusNoFormatChannel && !((NucleusNoFormatChannel) channel).formatMessages();
    }

    @Nullable
    private NucleusNoFormatChannel getChannel(MessageChannelEvent.Chat event) {
        if (event.getChannel().filter(x -> x instanceof NucleusNoFormatChannel).isPresent()) {
            return (NucleusNoFormatChannel) event.getChannel().get();
        }
        return event.getOriginalChannel() instanceof NucleusNoFormatChannel ? (NucleusNoFormatChannel) event.getOriginalChannel() : null;
    }

    private void onPlayerChatInternal(MessageChannelEvent.Chat event, Player player) {
        if (shouldNotFormat(event)) {
            @Nullable NucleusNoFormatChannel channel = getChannel(event);
            if (channel != null && channel.removePrefix()) {
                event.getFormatter().setHeader(Text.EMPTY);
            }

            // Not interested in applying these transforms.
            return;
        }

        MessageEvent.MessageFormatter eventFormatter = event.getFormatter();
        Text rawMessage = eventFormatter.getBody().isEmpty() ? event.getRawMessage() : eventFormatter.getBody().toText();

        SimpleTextFormatter headerFormatter = eventFormatter.getHeader();
        SimpleTextFormatter footerFormatter = eventFormatter.getFooter();
        if (this.chatConfig.isOverwriteEarlyPrefixes()) {
            eventFormatter.setHeader(Text.EMPTY);
            headerFormatter.clear();
        } else if (this.chatConfig.isTryRemoveMinecraftPrefix()) { // Avoid adding <name>.
            // We should remove the applier.
            for (SimpleTextTemplateApplier stta : eventFormatter.getHeader()) {
                if (stta instanceof MessageEvent.DefaultHeaderApplier) {
                    eventFormatter.getHeader().remove(stta); // the iterator is read only, so we have to do this...
                }
            }
        }

        if (this.chatConfig.isOverwriteEarlySuffixes()) {
            footerFormatter.clear();
        }

        final ChatTemplateConfig ctc;
        if (this.chatConfig.isUseGroupTemplates()) {
            ctc = this.templateUtil.getTemplateNow(player);
        } else {
            ctc = this.chatConfig.getDefaultTemplate();
        }

        if (!ctc.getPrefix().isEmpty()) {
            SimpleTextTemplateApplier headerApplier = new SimpleTextTemplateApplier();
            headerApplier.setTemplate(TextTemplate.of(ctc.getPrefix().getForCommandSource(player)));
            event.getFormatter().getHeader().add(headerApplier);
        }

        if (!ctc.getSuffix().isEmpty()) {
            SimpleTextTemplateApplier footerApplier = new SimpleTextTemplateApplier();
            footerApplier.setTemplate(TextTemplate.of(ctc.getSuffix().getForCommandSource(player)));
            event.getFormatter().getFooter().add(footerApplier);
        }

        event.getFormatter().setBody(this.chatConfig.isModifyMainMessage() ? useMessage(player, rawMessage, ctc) : rawMessage);
    }

    @Override public boolean shouldEnable() {
        return Nucleus.getNucleus().getConfigValue(ChatModule.ID, ChatConfigAdapter.class, ChatConfig::isModifychat).orElse(false);
    }

    private Text useMessage(Player player, Text rawMessage, ChatTemplateConfig chatTemplateConfig) {
        String m = TextSerializers.FORMATTING_CODE.serialize(rawMessage);
        if (this.chatConfig.isRemoveBlueUnderline()) {
            m = m.replaceAll("&9&n([A-Za-z0-9-.]+)(&r)?", "$1");
        }

        m = stripPermissionless(player, m);

        Text result;
        if (hasPermission(player, prefix + "url")) {
            result = TextParsingUtils.addUrls(m, !this.chatConfig.isRemoveBlueUnderline());
        } else {
            result = TextSerializers.FORMATTING_CODE.deserialize(m);
        }

        String chatcol = Util.getOptionFromSubject(player, "chatcolour", "chatcolor").orElseGet(chatTemplateConfig::getChatcolour);
        String chatstyle = Util.getOptionFromSubject(player, "chatstyle").orElseGet(chatTemplateConfig::getChatstyle);

        NameUtil nu = Nucleus.getNucleus().getNameUtil();
        return Text.of(nu.getColourFromString(chatcol), nu.getTextStyleFromString(chatstyle), result);
    }

    @Override public void onReload() {
        this.chatConfig = Nucleus.getNucleus().getInternalServiceManager().getServiceUnchecked(ChatConfigAdapter.class).getNodeOrDefault();
    }
}
