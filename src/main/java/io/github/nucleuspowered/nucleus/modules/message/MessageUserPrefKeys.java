/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.services.IPermissionCheckService;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl.BooleanKey;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPrefKeys;
import io.github.nucleuspowered.nucleus.modules.message.commands.MsgToggleCommand;
import io.github.nucleuspowered.nucleus.modules.message.commands.SocialSpyCommand;

public class MessageUserPrefKeys implements UserPrefKeys {

    private static final String MSG_TOGGLE_BASE =
            Nucleus.getNucleus().getPermissionRegistry().getPermissionsForNucleusCommand(MsgToggleCommand.class).getBase();
    private static final String SOCIAL_SPY_BASE =
            Nucleus.getNucleus().getPermissionRegistry().getPermissionsForNucleusCommand(SocialSpyCommand.class).getBase();
    private static final String SOCIAL_SPY_FORCE =
            Nucleus.getNucleus().getPermissionRegistry().getPermissionsForNucleusCommand(SocialSpyCommand.class).getPermissionWithSuffix("force");

    public static final PreferenceKeyImpl<Boolean> SOCIAL_SPY = new BooleanKey(
            NucleusKeysProvider.SOCIAL_SPY_KEY,
            true,
            user -> {
                IPermissionCheckService resolver = Nucleus.getNucleus().getPermissionResolver();
                return resolver.hasPermission(user, SOCIAL_SPY_BASE) && !resolver.hasPermission(user, SOCIAL_SPY_FORCE);
            },
            "userpref.socialspy"
    );

    public static final PreferenceKeyImpl<Boolean> RECEIVING_MESSAGES = new BooleanKey(
            NucleusKeysProvider.MESSAGE_TOGGLE_KEY,
            true,
            MSG_TOGGLE_BASE,
            "userpref.messagetoggle"
    );

}
