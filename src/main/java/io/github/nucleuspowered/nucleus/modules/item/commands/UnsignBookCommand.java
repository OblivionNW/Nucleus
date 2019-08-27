/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import io.github.nucleuspowered.nucleus.internal.annotations.RequireExistenceOf;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.services.impl.permission.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.PlainPagedData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.List;
import java.util.Optional;

@Permissions(suggestedLevel = SuggestedLevel.ADMIN, supportsOthers = true)
@RegisterCommand({"unsignbook", "unsign"})
@NonnullByDefault
@RequireExistenceOf("org.spongepowered.api.data.manipulator.mutable.item.PlainPagedData") // not in 7.1
public class UnsignBookCommand extends AbstractCommand.SimpleTargetOtherPlayer {

    @Override protected CommandResult executeWithPlayer(CommandSource source, Player target, CommandContext args, boolean isSelf)
            throws Exception {
        // Very basic for now, unsign book in hand.
        Optional<ItemStack> bookToUnsign = target.getItemInHand(HandTypes.MAIN_HAND).filter(item -> item.getType().equals(ItemTypes.WRITTEN_BOOK));
        if (bookToUnsign.isPresent()) {
            ItemStack unsignedBook = ItemStack.builder()
                    .itemType(ItemTypes.WRITABLE_BOOK)
                    .itemData(bookToUnsign.get().get(Keys.BOOK_PAGES).map(this::from).orElseGet(this::createData))
                    .quantity(bookToUnsign.get().getQuantity())
                    .build();
            target.setItemInHand(HandTypes.MAIN_HAND, unsignedBook);

            if (isSelf) {
                sendMessageTo(source, "command.unsignbook.success.self");
            } else {
                sendMessageTo(source, "command.unsignbook.success.other", target.getName());
            }
            return CommandResult.affectedItems(bookToUnsign.get().getQuantity());
        }

        if (isSelf) {
            throw ReturnMessageException.fromKey("command.unsignbook.notinhand.self");
        } else {
            throw ReturnMessageException.fromKey("command.unsignbook.notinhand.other", target.getName());
        }
    }

    private PlainPagedData from(List<Text> texts) {
        PlainPagedData ppd = createData();
        for (Text text : texts) {
            ppd.addElement(TextSerializers.FORMATTING_CODE.serialize(text));
        }

        return ppd;
    }

    private PlainPagedData createData() {
        return Sponge.getDataManager().getManipulatorBuilder(PlainPagedData.class).get().create();
    }
}
