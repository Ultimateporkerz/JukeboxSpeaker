package net.ultimporks.betterdiscs.block.entity;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.item.DiscItems;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import net.ultimporks.betterdiscs.util.menus.JukeblockMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class JukeblockBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final List<ItemStack> playedRecords = new ArrayList<>();

    // Playing music
    private int isPlaying = 100;
    private int isStopped = 200;
    // Record
    private Optional<Holder<JukeboxSong>> currentSong;
    private ItemStack currentDisc = ItemStack.EMPTY;
    // Volume Control
    private int volume = 100;
    // Particle Control
    private int particlesEnabled = 200;
    // Record Tick Count
    private long ticksSinceSongStarted;
    //private int ticksSinceLastEvent;
    private long recordStartedTick;
    private long tickCount;

    protected final ContainerData data;

    // Container Data Keys
    private final int volumeKey = 0;
    private final int particlesKey = 1;
    private final int isPlayingKey = 2;
    private final int isStoppedKey = 3;

    private final int itsFalse = 100;
    private final int itsTrue = 200;

    public JukeblockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.JUKEBOX_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> JukeblockBlockEntity.this.volume;
                    case 1 -> JukeblockBlockEntity.this.particlesEnabled;
                    case 2 -> JukeblockBlockEntity.this.isPlaying;
                    case 3 -> JukeblockBlockEntity.this.isStopped;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> JukeblockBlockEntity.this.volume = pValue;
                    case 1 -> JukeblockBlockEntity.this.particlesEnabled = pValue;
                    case 2 -> JukeblockBlockEntity.this.isPlaying = pValue;
                    case 3 -> JukeblockBlockEntity.this.isStopped = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void tick(LevelAccessor pLevel, @Nullable BlockState pState) {
        if (this.currentSong != null && this.currentSong.isPresent()) {
            if (this.currentSong.get().value().hasFinished(this.ticksSinceSongStarted)) {
                this.shuffleNext();
            } else {
                if (this.shouldEmitJukeboxPlayingEvent() && !this.isStopped()) {
                    this.spawnMusicParticles(level, this.getBlockPos());
                }
                this.ticksSinceSongStarted++;
            }
            // Check if the Playing record is still in the inventory, if not stop playing!
            if (!isPlayingDiscInInventory()) {
                stopPlaying();
            }
        }
    }

    // Shuffle
    private void shuffleNext() {
        // Stop current sound properly
        stopPlaying();
        // Select a new disc
        this.currentDisc = selectRandomDisc();
        // If no disc is available, do not attempt to play
        if (this.currentDisc.isEmpty()) {
            BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - No more records available to shuffle!");
            setStopped();
            return;
        }
        // Reset state
        this.setPlaying();
        this.recordStartedTick = this.tickCount;
        BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - Shuffling next song at " + this.worldPosition);

        // Activate the new sound
        SpeakerLinkUtil.activateJukeblock((ServerLevel) level, this.worldPosition, this.currentDisc);
        SpeakerLinkUtil.activateSpeakersJukeblock((ServerLevel) level, this.worldPosition, this.currentDisc);
    }

    // Play
    public void setPlaying() {
        // Enable
        this.isPlaying = itsTrue;
        this.data.set(isPlayingKey, itsTrue);
        if (this.isStopped()) {
            this.isStopped = itsFalse;
            this.data.set(isStoppedKey, itsFalse);
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    public boolean isPlaying() {
        int isActive = this.isPlaying;

        if (isActive == itsFalse) {
            return false;
        }
        return isActive == itsTrue;
    }
    public void startPlaying() {
        this.currentDisc = selectRandomDisc();
        BetterMusicDiscs.jukeblockLOGGING("currentDisc: " + currentDisc);
        if (currentDisc != ItemStack.EMPTY) {
            this.currentSong = JukeboxSong.fromStack(this.level.registryAccess(), this.currentDisc);
        }
        BetterMusicDiscs.jukeblockLOGGING("currentSong: " + this.currentSong);
        this.setPlaying();
        this.recordStartedTick = this.tickCount;
        this.ticksSinceSongStarted = 0L;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();

        // Send Sound Packets to Clients
        SpeakerLinkUtil.activateJukeblock((ServerLevel) level, this.worldPosition, this.currentDisc);
        SpeakerLinkUtil.activateSpeakersJukeblock((ServerLevel) level, this.worldPosition, this.currentDisc);
    }

    // Stop
    public void setStopped() {
        if (this.isPlaying()) {
            // Set stopped
            this.isStopped = itsTrue;
            this.data.set(isStoppedKey, itsTrue);

            this.isPlaying = itsFalse;
            this.data.set(isPlayingKey, itsFalse);

            this.stopPlaying();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    public boolean isStopped() {
        int isStopped = this.isStopped;

        if (isStopped == itsFalse) {
            return false;
        }
        return isStopped == itsTrue;
    }
    public void stopPlaying() {
        this.currentDisc = ItemStack.EMPTY;
        this.ticksSinceSongStarted = 0L;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();

        SpeakerLinkUtil.deactivateJukeblock((ServerLevel) level, this.worldPosition);
        SpeakerLinkUtil.deactivateSpeakersJukeblock((ServerLevel) level, this.worldPosition);
    }

    // Volume
    public void setVolume(int newVolume) {
        this.volume = newVolume;
        this.data.set(volumeKey, newVolume);
        setChanged();
        level.sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
    public int getVolume() {
        return this.volume;
    }

    // Particles
    private void spawnMusicParticles(Level pLevel, BlockPos pPos) {
        if (areParticlesEnabled()) {
            if (pLevel instanceof ServerLevel serverlevel) {
                Vec3 vec3 = Vec3.atBottomCenterOf(pPos).add(0.0D, 1.2D, 0.0D);
                float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
                serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 1, f, 0.0D, 0.0D, 1.0D);
            }
        }
    }
    public void setParticlesEnabled(boolean enabled) {
        if (enabled) {
            // Enable
            this.particlesEnabled = itsTrue;
            this.data.set(particlesKey, itsTrue);
        } else {
            // Disable
            this.particlesEnabled = itsFalse;
            this.data.set(particlesKey, itsFalse);
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
    public boolean areParticlesEnabled() {
        int particlesNumber = this.particlesEnabled;

        if (particlesNumber == itsFalse) {
            return false;
        }
        return particlesNumber == itsTrue;
    }
    private boolean shouldEmitJukeboxPlayingEvent() {
        return this.ticksSinceSongStarted % 20L == 0L;
    }

    public ItemStack selectRandomDisc() {
        List<ItemStack> discs = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();

                // Check if it's a vanilla disc, or modded disc (jukebox song)
                boolean isMusicDisc = DiscItems.isMusicDisc(stack);
                boolean isJukeboxSong = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null;

                if (isMusicDisc || isJukeboxSong) {
                    discs.add(stack);
                }
            }
        }

        if (discs.isEmpty()) return ItemStack.EMPTY;

        // Remove current disc if there are others
        if (discs.size() > 1 && !currentDisc.isEmpty()) {
            discs.removeIf(stack -> ItemStack.isSameItem(stack, currentDisc));
        }

        // Remove discs that have already been played
        if (discs.size() > 1) {
            discs.removeIf(stack -> playedRecords.stream().anyMatch(p -> ItemStack.isSameItem(p, stack)));
        }

        // If all have been played, stop and reset the list.
        if (discs.isEmpty()) {
            playedRecords.clear();
            this.stopPlaying();
            return ItemStack.EMPTY;
        }

        // Pick a random disc and play it.
        ItemStack selected = discs.get(level.random.nextInt(discs.size()));
        playedRecords.add(selected.copy());
        return selected;
    }

    public boolean isPlayingDiscInInventory() {
        if (currentDisc != null) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.is(currentDisc.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }


    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, Player pPlayer) {
        return new JukeblockMenu(pContainerId, pPlayerInventory, this, this.data);
    }
    @Override
    public void onChunkUnloaded() {
        this.setStopped();
        super.onChunkUnloaded();
    }
    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.isPlaying = pTag.getInt("isPlaying");
        this.isStopped = pTag.getInt("isStopped");
        this.volume = pTag.getInt("Volume");
        this.particlesEnabled = pTag.getInt("ParticlesActive");
        this.recordStartedTick = pTag.getLong("RecordStartTick");
        this.tickCount = pTag.getLong("TickCount");
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
    }
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("isPlaying", this.isPlaying);
        pTag.putInt("isStopped", this.isStopped);
        pTag.putInt("Volume", this.volume);
        pTag.putInt("ParticlesActive", this.particlesEnabled);
        pTag.putLong("RecordStartTick", this.recordStartedTick);
        pTag.putLong("TickCount", this.tickCount);
        pTag.put("inventory", this.itemHandler.serializeNBT(pRegistries));
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.betterdiscs.jukebox");
    }
}