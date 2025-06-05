package net.ultimporks.betterdiscs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.init.ModRecipes;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import net.ultimporks.betterdiscs.util.menus.RecordLatheStationMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RecordLatheBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 600;
    private int selectedItemIndex = -1;

    public RecordLatheBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RECORD_LATHE_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RecordLatheBlockEntity.this.progress;
                    case 1 -> RecordLatheBlockEntity.this.maxProgress;
                    case 2 -> RecordLatheBlockEntity.this.selectedItemIndex;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RecordLatheBlockEntity.this.progress = pValue;
                    case 1 -> RecordLatheBlockEntity.this.maxProgress = pValue;
                    case 2 -> RecordLatheBlockEntity.this.selectedItemIndex = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public ItemStack getRenderStack() {
        if (itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return itemHandler.getStackInSlot(INPUT_SLOT);
        } else {
            return itemHandler.getStackInSlot(OUTPUT_SLOT);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.betterdiscs.record_lathe");
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new RecordLatheStationMenu(pContainerId, pPlayerInventory, this, this.data);
    }
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("record_lathe_progress", progress);
        pTag.putInt("selected_item_index", selectedItemIndex);
        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("record_lathe_progress");
        selectedItemIndex = pTag.getInt("selected_item_index");
    }

    public void drops()  {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    private void resetProgress() {
        progress = 0;
        this.setSelectedItemIndex(-1);
    }
    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }
    private void increaseCraftingProgress() {
        progress++;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe() && slotHasItem()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }
    private void craftItem() {
        Optional<RecordLatheRecipe> recipeOpt = getCurrentRecipe();

        if (recipeOpt.isPresent()) {
            RecordLatheRecipe recipe = recipeOpt.get();
            ItemStack result = recipe.getResultItem(null);

            BetterMusicDiscs.generalLOGGING("(RecordLatheBlockEntity) - SelectedItemIndex: " + selectedItemIndex + " Associated Item: " + result.getItem().getDescriptionId());

            this.itemHandler.extractItem(INPUT_SLOT, 1, false);

            // Check if the output slot is empty or the correct item can be inserted
            if (this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
                this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(), result.getCount()));
            } else if (this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(result.getItem())) {
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).grow(result.getCount());
            }

        } else {
            BetterMusicDiscs.generalLOGGING("(RecordLatheBlockEntity) - No valid recipe found for crafting");
        }
    }
    private boolean hasRecipe() {
        Optional<RecordLatheRecipe> recipe = getCurrentRecipe();

        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(this.level.registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }
    public boolean slotHasItem() {
        return !this.itemHandler.getStackInSlot(0).isEmpty();
    }
    private Optional<RecordLatheRecipe> getCurrentRecipe() {
        List<RecordLatheRecipe> recipes = getRecipes();

        if (selectedItemIndex >= 0 && selectedItemIndex < recipes.size()) {
            return Optional.of(recipes.get(selectedItemIndex));
        }
        return Optional.empty();
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }
    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }
    public List<RecordLatheRecipe> getRecipes() {
        return this.level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.RECORD_LATHE_TYPE.get())
                .stream()
                .map(holder -> holder.value())
                .sorted(Comparator.comparing(r -> r.getType().toString()))
                .toList();
    }
    public void setSelectedItemIndex(int index) {
        this.selectedItemIndex = index;
        setChanged();
    }
}