/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatTemplateConfig;
import io.github.nucleuspowered.nucleus.modules.chat.util.TemplateUtil;
import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

@Deprecated
public class NameUtil implements InternalServiceManagerTrait {

    public Text getNameOrConsole(@Nullable UUID player) {
        if (player == null || player == Util.consoleFakeUUID) {
            return Text.of(Sponge.getServer().getConsole().getName());
        }

        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
                .get(player)
                .map(this::getName)
                .orElseGet(() -> Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("standard.unknown"));
    }

     /**
     * Gets the display name from a {@link User} as Sponge sees it.
     *
     * @param player The {@link User} to get the data from.
     * @return The {@link Text}
     */
    public Text getName(User player) {
        Preconditions.checkNotNull(player);

        TextColor tc = getNameColour(player);
        TextStyle ts = getNameStyle(player);

        Text.Builder tb = getService(NicknameService.class)
                .map(service -> service.getNicknameWithPrefix(player).map(Text::toBuilder).orElse(null))
                .orElseGet(() ->
                        player.get(Keys.DISPLAY_NAME)
                                .map(Text::toBuilder).orElseGet(() -> Text.builder(player.getName())));

        tb.onHover(TextActions.showText(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("name.hover.ign", player.getName()))).build();
        if (tc != TextColors.NONE && tb.getColor() == TextColors.NONE) {
            List<Text> lt = tb.getChildren();
            if (lt.isEmpty() || lt.get(0).getColor().equals(TextColors.NONE)) {
                tb.color(tc);
            }
        }

        if (!ts.isEmpty()) {
            tb.style(ts);
        }
        return tb.build();
    }

    public TextColor getColourFromString(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return TextColors.NONE;
        }

        if (s.length() == 1) {
            return colourMap.getOrDefault(s.charAt(0), TextColors.NONE);
        } else {
            return Sponge.getRegistry().getType(TextColor.class, s.toUpperCase()).orElse(TextColors.NONE);
        }
    }

    public TextStyle getTextStyleFromString(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return TextStyles.NONE;
        }

        TextStyle ts = TextStyles.NONE;
        for (String split : s.split("\\s*,\\s*")) {
            if (split.length() == 1) {
                ts = ts.and(styleMap.getOrDefault(split.charAt(0), TextStyles.NONE));
            } else {
                ts = ts.and(styleMapFull.getOrDefault(split.toUpperCase(), TextStyles.NONE));
            }
        }

        return ts;
    }

    private TextColor getNameColour(User player) {
        return getStyle(player, this::getColourFromString, x -> getColourFromString(x.getNamecolour()), TextColors.NONE,
                "namecolor", "namecolour");
    }

    private TextStyle getNameStyle(User player) {
        return getStyle(player, this::getTextStyleFromString, x -> getTextStyleFromString(x.getNamestyle()), TextStyles.NONE,
                "namestyle");
    }

    private <T extends TextElement> T getStyle(User player, Function<String, T> returnIfAvailable,
            Function<ChatTemplateConfig, T> fromTemplate, T def, String... options) {
        Optional<String> os = Util.getOptionFromSubject(player, options);
        if (os.isPresent()) {
            return returnIfAvailable.apply(os.get());
        }

        return getService(TemplateUtil.class).map(templateUtil -> fromTemplate.apply(templateUtil.getTemplateNow(player))).orElse(def);

    }
}
