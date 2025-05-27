package net.ultimporks.betterdiscs.util.menus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.ultimporks.betterdiscs.init.ModBlocks;
import net.ultimporks.betterdiscs.init.ModMenuTypes;

/*
public class JukeblockMenu extends AbstractContainerMenu {
    public final JukeblockBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public JukeblockMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public JukeblockMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.JUKEBOX_MENU.get(), containerId);
        checkContainerSize(inv, 18);
        this.blockEntity = ((JukeblockBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 8, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 26, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 2, 44, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 3, 62, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 4, 80, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 5, 98, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 6, 116, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 7, 134, 8));
            this.addSlot(new SlotItemHandler(iItemHandler, 8, 152, 8));

            this.addSlot(new SlotItemHandler(iItemHandler, 9, 8, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 10, 26, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 11, 44, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 12, 62, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 13, 80, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 14, 98, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 15, 116, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 16, 134, 26));
            this.addSlot(new SlotItemHandler(iItemHandler, 17, 152, 26));
        });
        addDataSlots(this.data);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.JUKEBLOCK.get());
    }

    public int getVolume() {
        return blockEntity.getVolume();
    }
    public boolean areParticlesEnabled() {
        return blockEntity.areParticlesEnabled();
    }
    public boolean isPlayingMusic() {
        return blockEntity.isPlaying();
    }
    public boolean isMusicStopped() {
        return blockEntity.isStopped();
    }
    public BlockPos getJukeblockPos() {
        return blockEntity.getBlockPos();
    }

    // QuickMove helpers
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 18;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


}

 */