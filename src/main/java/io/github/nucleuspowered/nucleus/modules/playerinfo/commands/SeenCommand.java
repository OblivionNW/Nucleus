/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.PermissionRegistry;
import io.github.nucleuspowered.nucleus.internal.annotations.RunAsync;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.CommandBuilder;
import io.github.nucleuspowered.nucleus.internal.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.impl.messageprovider.legacy.MessageProvider;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.core.CoreKeys;
import io.github.nucleuspowered.nucleus.modules.core.services.SafeTeleportService;
import io.github.nucleuspowered.nucleus.modules.misc.commands.SpeedCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.services.SeenHandler;
import io.github.nucleuspowered.nucleus.modules.teleport.commands.TeleportPositionCommand;
import io.github.nucleuspowered.nucleus.services.IPlayerOnlineService;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.util.TriFunction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

// TODO: 7.1 cleanup
@Permissions
@RunAsync
@RegisterCommand({"seen", "seenplayer", "lookup"})
@EssentialsEquivalent("seen")
@NonnullByDefault
public class SeenCommand extends AbstractCommand<CommandSource> {

    private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat("0.00");
    private final SeenHandler seenHandler = getServiceUnchecked(SeenHandler.class);

    private static final String EXTENDED_SUFFIX = "extended";
    public static final String EXTENDED_PERMISSION = PermissionRegistry.PERMISSIONS_PREFIX + "seen." + EXTENDED_SUFFIX;
    private static final String IP_PERMISSION = EXTENDED_PERMISSION + ".ip";
    private static final String UUID_PERMISSION = EXTENDED_PERMISSION + ".uuid";
    private static final String FIRST_PLAYED_PERMISSION = EXTENDED_PERMISSION + ".firstplayed";
    private static final String LAST_PLAYED_PERMISSION = EXTENDED_PERMISSION + ".lastplayed";
    private static final String SPEED_PERMISSION = EXTENDED_PERMISSION + ".speed";
    private static final String WALKING_SPEED_PERMISSION = SPEED_PERMISSION + ".walking";
    private static final String FLYING_SPEED_PERMISSION = SPEED_PERMISSION + ".flying";
    private static final String LOCATION_PERMISSION = EXTENDED_PERMISSION + ".location";
    private static final String FLYING_PERMISSION = EXTENDED_PERMISSION + ".flying";
    private static final String CANFLY_FLYING_PERMISSION = FLYING_PERMISSION + ".canfly";
    private static final String ISFLYING_FLYING_PERMISSION = FLYING_PERMISSION + ".isflying";
    private static final String GAMEMODE_PERMISSION = EXTENDED_PERMISSION + ".gamemode";

    // keeps order!
    private final ImmutableMap<String, TriFunction<CommandSource, User, IUserDataObject, Text>> entries
            = ImmutableMap.<String, TriFunction<CommandSource, User, IUserDataObject, Text>>builder()
                    .put(UUID_PERMISSION, this::getUUID)
                    .put(IP_PERMISSION, this::getIP)
                    .put(FIRST_PLAYED_PERMISSION, this::getFirstPlayed)
                    .put(LAST_PLAYED_PERMISSION, this::getLastPlayed)
                    .put(WALKING_SPEED_PERMISSION, this::getWalkingSpeed)
                    .put(FLYING_SPEED_PERMISSION, this::getFlyingSpeed)
                    .put(LOCATION_PERMISSION, this::getLocation)
                    .put(FLYING_PERMISSION, this::getIsFlying)
                    .put(CANFLY_FLYING_PERMISSION, this::getCanFly)
                    .put(ISFLYING_FLYING_PERMISSION, this::getIsFlying)
                    .put(GAMEMODE_PERMISSION, this::getGameMode)
                    .build();

    @Nullable
    private Text getUUID(CommandSource source, User user, IUserDataObject userDataModule) {
        return getMessageFor(source, "command.seen.uuid", user.getUniqueId());
    }

    @Nullable
    private Text getIP(CommandSource source, User user, IUserDataObject userDataModule) {
        @Nullable Tuple<Text, String> res = user.getPlayer()
                    .map(pl -> Tuple.of(
                            getMessageFor(source, "command.seen.ipaddress", pl.getConnection().getAddress().getAddress().toString()),
                            pl.getConnection().getAddress().getAddress().toString()))
                    .orElseGet(() -> userDataModule.get(CoreKeys.IP_ADDRESS).map(x ->
                            Tuple.of(
                                    getMessageFor(source, "command.seen.lastipaddress", x),
                                    x
                            )).orElse(null));
        if (res != null) {
            if (Sponge.getCommandManager().get("nucleus:getfromip").isPresent()) {
                return res.getFirst().toBuilder()
                        .onHover(TextActions.showText(getMessageFor(source, "command.seen.ipclick")))
                        .onClick(TextActions.runCommand("/nucleus:getfromip " + res.getSecond().replaceAll("^/", "")))
                        .build();
            }
            return res.getFirst();
        }

        return null;
    }

    @Nullable
    private Text getFirstPlayed(CommandSource source, User user, IUserDataObject userDataModule) {
        Optional<Instant> i = user.get(Keys.FIRST_DATE_PLAYED);
        if (!i.isPresent()) {
            i = userDataModule.get(CoreKeys.FIRST_JOIN);
        }

        return i.map(x -> getMessageFor(source, "command.seen.firstplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(source.getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private Text getLastPlayed(CommandSource source, User user, IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return null;
        }

        return user.get(Keys.LAST_DATE_PLAYED).map(x -> getMessageFor(source, "command.seen.lastplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(source.getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private Text getLocation(CommandSource source, User user, IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return getLocationString("command.seen.currentlocation", user.getPlayer().get().getLocation(), source);
        }

        Optional<WorldProperties> wp = user.getWorldUniqueId().map(x -> Sponge.getServer().getWorldProperties(x).orElse(null));
        return wp.map(worldProperties ->
                    getLocationString("command.seen.lastlocation", worldProperties, user.getPosition(), source))
                .orElseGet(() -> Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("standard.unknown"));
    }

    @Nullable
    private Text getWalkingSpeed(CommandSource source, User user, IUserDataObject userDataModule) {
        return user.get(Keys.WALKING_SPEED)
                .map(x -> getMessageFor(source, "command.seen.speed.walk", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private Text getFlyingSpeed(CommandSource source, User user, IUserDataObject userDataModule) {
        return user.get(Keys.FLYING_SPEED)
                .map(x -> getMessageFor(source, "command.seen.speed.fly", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private Text getCanFly(CommandSource source, User user, IUserDataObject userDataModule) {
        return getMessageFor(source, "command.seen.canfly", getYesNo(user.get(Keys.CAN_FLY).orElse(false)));
    }

    @Nullable
    private Text getIsFlying(CommandSource source, User user, IUserDataObject userDataModule) {
        return getMessageFor(source, "command.seen.isflying", getYesNo(user.get(Keys.IS_FLYING).orElse(false)));
    }

    @Nullable
    private Text getGameMode(CommandSource source, User user, IUserDataObject userDataModule) {
        return user.get(Keys.GAME_MODE).map(x -> getMessageFor(source, "command.seen.gamemode", x.getName())).orElse(null);
    }

    @Override protected Map<String, PermissionInformation> permissionsToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put(EXTENDED_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extended", SuggestedLevel.NONE));
        m.put(IP_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.ip", SuggestedLevel.ADMIN));
        m.put(UUID_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.uuid", SuggestedLevel.ADMIN));
        m.put(FIRST_PLAYED_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.firstplayed", SuggestedLevel.ADMIN));
        m.put(LAST_PLAYED_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.lastplayed", SuggestedLevel.ADMIN));
        m.put(SPEED_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.speed", SuggestedLevel.ADMIN));
        m.put(FLYING_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.flying", SuggestedLevel.ADMIN));
        m.put(GAMEMODE_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.gamemode", SuggestedLevel.ADMIN));
        m.put(LOCATION_PERMISSION, PermissionInformation.getWithTranslation("permission.seen.extendedperms.location", SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.firstParsing(
                NucleusParameters.ONE_USER_UUID,
                NucleusParameters.ONE_USER
            )
        };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args, Cause cause) {
        User user = args.<User>getOne(NucleusParameters.Keys.USER_UUID)
                .orElseGet(() -> args.<User>getOne(NucleusParameters.Keys.USER).get());
        // Get the player in case the User is displaying the wrong name.
        user = user.getPlayer().map(x -> (User) x).orElse(user);
        IUserDataObject userDataObject = Nucleus.getNucleus()
                .getStorageManager().getUserService().getOrNewOnThread(user.getUniqueId());

        List<Text> messages = new ArrayList<>();
        final MessageProvider messageProvider = Nucleus.getNucleus().getMessageProvider();

        // Everyone gets the last online time.
        IPlayerOnlineService playerOnlineService = getServiceManager().getServiceUnchecked(IPlayerOnlineService.class);
        if (playerOnlineService.isOnline(src, user)) {
            messages.add(messageProvider.getTextMessageWithFormat("command.seen.iscurrently.online", user.getName()));
            userDataObject.get(CoreKeys.LAST_LOGIN).ifPresent(x -> messages.add(
                    messageProvider.getTextMessageWithFormat("command.seen.loggedon", Util.getTimeToNow(x))));
        } else {
            messages.add(messageProvider.getTextMessageWithFormat("command.seen.iscurrently.offline", user.getName()));
            playerOnlineService.lastSeen(src, user).ifPresent(x -> messages.add(
                    messageProvider.getTextMessageWithFormat("command.seen.loggedoff", Util.getTimeToNow(x))));
        }

        messages.add(messageProvider.getTextMessageWithFormat("command.seen.displayname", TextSerializers.FORMATTING_CODE.serialize(
                Nucleus.getNucleus().getNameUtil().getName(user))));

        messages.add(Util.SPACE);
        for (Map.Entry<String, TriFunction<CommandSource, User, IUserDataObject, Text>> entry : this.entries.entrySet()) {
            if (hasPermission(src, entry.getKey())) {
                @Nullable Text m = entry.getValue().accept(src, user, userDataObject);
                if (m != null) {
                    messages.add(m);
                }
            }
        }

        // Add the extra module information.
        messages.addAll(this.seenHandler.buildInformation(src, user));

        Util.getPaginationBuilder(src).contents(messages).padding(Text.of(TextColors.GREEN, "-"))
                .title(messageProvider.getTextMessageWithFormat("command.seen.title", user.getName())).sendTo(src);
        return CommandResult.success();
    }

    private Text getLocationString(String key, Location<World> lw, CommandSource source) {
        return getLocationString(key, lw.getExtent().getProperties(), lw.getPosition(), source);
    }

    private Text getLocationString(String key, WorldProperties worldProperties, Vector3d position, CommandSource source) {
        Text text = getMessageFor(source, key, getMessageFor(source,"command.seen.locationtemplate", worldProperties.getWorldName(),
                position.toInt().toString()));
        if (CommandBuilder.isCommandRegistered(TeleportPositionCommand.class)
            && Nucleus.getNucleus().getPermissionRegistry().getPermissionsForNucleusCommand(TeleportPositionCommand.class).testBase(source)) {

            final Text.Builder building = text.toBuilder().onHover(TextActions.showText(
                    getMessageFor(source, "command.seen.teleportposition")
            ));

            Sponge.getServer().getWorld(worldProperties.getUniqueId()).ifPresent(
                    x -> building.onClick(TextActions.executeCallback(cs -> {
                        if (cs instanceof Player) {
                            SafeTeleportService.setLocation((Player) cs, new Location<>(x, position));
                        }
                    })
            ));

            return building.build();
        }

        return text;
    }

    private String getYesNo(@Nullable Boolean bool) {
        if (bool == null) {
            bool = false;
        }

        return Nucleus.getNucleus().getMessageProvider().getMessageWithFormat("standard.yesno." + bool.toString().toLowerCase());
    }
}
