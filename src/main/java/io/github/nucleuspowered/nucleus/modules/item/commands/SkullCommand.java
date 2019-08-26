/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.argumentparsers.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.command.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.command.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.AbstractCommand;
import io.github.nucleuspowered.nucleus.internal.command.ReturnMessageException;
import io.github.nucleuspowered.nucleus.internal.docgen.annotations.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.internal.interfaces.SimpleReloadable;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.item.config.ItemConfigAdapter;
import io.github.nucleuspowered.nucleus.modules.item.config.SkullConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NonnullByDefault
@RegisterCommand({"skull"})
@Permissions(supportsOthers = true)
@EssentialsEquivalent({"skull", "playerskull", "head"})
public class SkullCommand extends AbstractCommand<Player> implements SimpleReloadable {

    private final String limitExemptPermission = Nucleus.getNucleus().getPermissionRegistry()
            .getPermissionsForNucleusCommand(SkullCommand.class)
            .getPermissionWithSuffix("exempt.limit");

    private final String amountKey = "amount";
    private final String player = "subject";

    private int amountLimit = Integer.MAX_VALUE;
    private boolean isUseMinecraftCommand = false;

    @Override public void onReload() {
        SkullConfig config = getServiceUnchecked(ItemConfigAdapter.class).getNodeOrDefault().getSkullConfig();
        this.isUseMinecraftCommand = config.isUseMinecraftCommand();
        this.amountLimit = config.getSkullLimit();
    }

    @Override
    public Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("exempt.limit", PermissionInformation.getWithTranslation("permission.skull.exempt.limit", SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
            GenericArguments.optionalWeak(
                requirePermissionArg(
                    GenericArguments.onlyOne(GenericArguments.user(Text.of(this.player))), this.permissions.getPermissionWithSuffix("others"))),
            GenericArguments.optional(new PositiveIntegerArgument(Text.of(this.amountKey)))
        };
    }

    @Override
    public CommandResult executeCommand(Player pl, CommandContext args, Cause cause) throws Exception {
        User user = this.getUserFromArgs(User.class, pl, this.player, args);
        int amount = args.<Integer>getOne(this.amountKey).orElse(1);

        if (amount > this.amountLimit && !pl.hasPermission(this.limitExemptPermission)) {
            // fail
            throw ReturnMessageException.fromKey(pl, "command.skull.limit", this.amountLimit);
        }

        if (this.isUseMinecraftCommand) {
            CommandResult result = Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
                String.format("minecraft:give %s skull %d 3 {SkullOwner:%s}", pl.getName(), amount, user.getName()));
            if (result.getSuccessCount().orElse(0) > 0) {
                sendMessageTo(pl, "command.skull.success.plural", String.valueOf(amount), user.getName());
                return result;
            }

            throw ReturnMessageException.fromKey(pl, "command.skull.error", user.getName());
        }

        int fullStacks = amount / 64;
        int partialStack = amount % 64;

        // Create the Skull
        ItemStack skullStack = ItemStack.builder().itemType(ItemTypes.SKULL).quantity(64).build();

        // Set it to subject skull type and set the owner to the specified subject
        if (skullStack.offer(Keys.SKULL_TYPE, SkullTypes.PLAYER).isSuccessful()
                && skullStack.offer(Keys.REPRESENTED_PLAYER, user.getProfile()).isSuccessful()) {
            List<ItemStack> itemStackList = Lists.newArrayList();

            // If there were stacks, create as many as needed.
            if (fullStacks > 0) {
                itemStackList.add(skullStack);
                for (int i = 2; i <= fullStacks; i++) {
                    itemStackList.add(skullStack.copy());
                }
            }

            // Same with the partial stacks.
            if (partialStack > 0) {
                ItemStack is = skullStack.copy();
                is.setQuantity(partialStack);
                itemStackList.add(is);
            }

            int accepted = 0;
            int failed = 0;

            Inventory inventoryToOfferTo = pl.getInventory()
                    .query(
                            QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class),
                            QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
            for (ItemStack itemStack : itemStackList) {
                int stackSize = itemStack.getQuantity();
                InventoryTransactionResult itr = inventoryToOfferTo.offer(itemStack);
                int currentFail = itr.getRejectedItems().stream().mapToInt(ItemStackSnapshot::getQuantity).sum();
                failed += currentFail;
                accepted += stackSize - currentFail;
            }

            // What was accepted?
            if (accepted > 0) {
                if (failed > 0) {
                    sendMessageTo(pl, "command.skull.semifull", String.valueOf(failed));
                }

                if (accepted == 1) {
                    sendMessageTo(pl, "command.skull.success.single", user.getName());
                } else {
                    sendMessageTo(pl, "command.skull.success.plural", String.valueOf(accepted), user.getName());
                }

                return CommandResult.success();
            }

            sendMessageTo(pl, "command.skull.full", user.getName());
            return CommandResult.empty();
        } else {
            sendMessageTo(pl, "command.skull.error", user.getName());
            return CommandResult.empty();
        }
    }
}
