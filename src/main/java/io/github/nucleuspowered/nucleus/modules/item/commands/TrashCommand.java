/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@RegisterCommand("trash")
@Permissions(suggestedLevel = SuggestedLevel.USER)
@NonnullByDefault
public class TrashCommand extends AbstractCommand<Player> {

    @Override
    protected CommandResult executeCommand(Player src, CommandContext args, Cause cause) throws Exception {
        if (src.openInventory(Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Nucleus.getNucleus().getMessageProvider().getTextMessageWithFormat("command.trash.title")))
                .build(Nucleus.getNucleus())).isPresent()) {
            return CommandResult.success();
        }

        throw ReturnMessageException.fromKey("command.trash.error");
    }
}
