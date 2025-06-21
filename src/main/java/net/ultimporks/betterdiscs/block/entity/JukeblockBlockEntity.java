package net.ultimporks.betterdiscs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.item.DiscItems;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import net.ultimporks.betterdiscs.util.menus.JukeblockMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class JukeblockBlockEntity extends BlockEntity implements MenuProvider {
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
    public final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    protected final ContainerData data;

    // Playing music
    private int isPlaying = 100;
    private int isStopped = 200;
    // Record
    private Optional<Holder<JukeboxSong>> currentSong;
    private ItemStack currentDisc = ItemStack.EMPTY;
    private final Set<ItemStack> playedRecords = new HashSet<>();
    private final Deque<ItemStack> trackHistory = new ArrayDeque<>();
    // Volume Control
    private int volume = 100;
    // Particle Control
    private int particlesEnabled = 200;
    // Record Tick Count
    private long ticksSinceSongStarted;
    private long recordStartedTick;
    private long tickCount;
    // Container Data Keys
    private final int volumeKey = 0;
    private final int particlesKey = 1;
    private final int isPlayingKey = 2;
    private final int isStoppedKey = 3;
    private final int itsFalse = 100;
    private final int itsTrue = 200;

    public void tick(LevelAccessor pLevel, @Nullable BlockState pState) {
        if (this.currentSong != null && this.currentSong.isPresent()) {
            if (this.shouldStartNextSong()) {
                BetterMusicDiscs.jukeblockLOGGING("Song has ENDED!");
                this.shuffleNext();
            } else {
                if (this.shouldEmitJukeboxPlayingEvent() && !this.isStopped()) {
                    this.spawnMusicParticles(level, this.getBlockPos());
                }
                this.ticksSinceSongStarted++;
            }
            // Check if the Playing record is still in the inventory, if not stop playing!
            if (!isPlayingDiscInInventory()) {
                this.stopPlaying(false);
            }
        }
    }

    // Rewind
    public void previousTrack() {
        if (!trackHistory.isEmpty()) {
            this.stopPlaying(true);
            this.currentDisc = trackHistory.pop();
            this.setChanged();
            this.startPlaying(true, true);
        }
    }

    // Next Track
    public void nextTrack() {
        this.pushCurrentToHistory();
        // Stop playing after previous is saved
        this.stopPlaying(true);
        // Select a new disc
        this.currentDisc = selectRandomDisc();
        // If no disc is available, do not attempt to play
        if (this.currentDisc.isEmpty()) {
            BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - No more records available to shuffle!");
            this.stopPlaying(false);
            return;
        }
        this.startPlaying(true, true);
        BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - Skipping to next song at " + this.worldPosition);
    }

    // Shuffle
    public void shuffleNext() {
        this.pushCurrentToHistory();
        // Stop current sound properly
        this.stopPlaying(true);
        // Select a new disc
        this.currentDisc = selectRandomDisc();
        // If no disc is available, do not attempt to play
        if (this.currentDisc.isEmpty()) {
            BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - No more records available to shuffle!");
            this.stopPlaying(false);
            return;
        }
        // Reset state
        this.startPlaying(true, false);
        BetterMusicDiscs.jukeblockLOGGING("(JukeblockBlockEntity) - Shuffling next song at " + this.worldPosition);

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
    public void startPlaying(boolean isShuffling, boolean previousTrack) {
        // If it's not shuffling, choose the record.
        if (!isShuffling || !previousTrack) {
            this.currentDisc = selectRandomDisc();
        }
        // Set NBT Data
        setPlaying();
        this.recordStartedTick = this.tickCount;
        this.ticksSinceSongStarted = 0L;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();

        if (currentDisc != null) {
            this.currentSong = JukeboxSong.fromStack(this.level.registryAccess(), this.currentDisc);
        }

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
    public void stopPlaying(boolean isShuffling) {
        // If not shuffling, reset the played records.
        if (!isShuffling) {
            this.resetPlayedRecords();
            this.trackHistory.clear();
        }
        this.setStopped();
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

    private void resetPlayedRecords() {
        Set<ItemStack> availableDiscs = new HashSet<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                boolean isMusicDisc = DiscItems.isMusicDisc(stack);
                boolean isJukeboxSong = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null;

                if (isMusicDisc || isJukeboxSong) {
                    availableDiscs.add(stack.copy());
                }
            }
        }

        boolean allPlayed = availableDiscs.stream().allMatch(disc ->
                playedRecords.stream().anyMatch(p -> ItemStack.isSameItemSameComponents(p, disc))
        );

        if (allPlayed) {
            playedRecords.clear();
            BetterMusicDiscs.jukeblockLOGGING("All records have been played. Resetting playedRecords.");
        }
    }
    private boolean shouldStartNextSong() {
        return this.currentSong.get().value().hasFinished(this.ticksSinceSongStarted);
    }
    private ItemStack selectRandomDisc() {
        List<ItemStack> discs = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                boolean isMusicDisc = DiscItems.isMusicDisc(stack);
                boolean isJukeboxSong = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null;

                if (isMusicDisc || isJukeboxSong) {
                    discs.add(stack);
                }
            }
        }

        if (discs.isEmpty()) return ItemStack.EMPTY;

        if (discs.size() > 1 && !currentDisc.isEmpty()) {
            discs.removeIf(stack -> ItemStack.isSameItemSameComponents(stack, currentDisc));
        }

        if (discs.size() > 1) {
            discs.removeIf(stack ->
                    playedRecords.stream().anyMatch(p -> ItemStack.isSameItemSameComponents(p, stack))
            );
        }

        if (discs.isEmpty()) return ItemStack.EMPTY;

        ItemStack selected = discs.get(level.random.nextInt(discs.size()));
        this.playedRecords.add(selected.copy()); // must copy to freeze state
        return selected;
    }
    private void pushCurrentToHistory() {
        if (!currentDisc.isEmpty()) {
            trackHistory.push(currentDisc.copy());
            if (trackHistory.size() > 6) {
                trackHistory.removeLast();
            }
        }
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
        this.stopPlaying(false);
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