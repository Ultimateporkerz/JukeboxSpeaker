package net.ultimporks.betterdiscs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import net.ultimporks.betterdiscs.util.menus.JukeboxMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JukeblockBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // Playing music
    private int isPlaying = 100;
    private int isStopped = 200;
    // Record
    private ItemStack currentDisc;
    // Volume Control
    private int volume = 100;
    // Particle Control
    private int particlesEnabled = 200;
    // Record Tick Count
    private int ticksSinceLastEvent;
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

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        ++this.ticksSinceLastEvent;
        if (isPlaying() && currentDisc != null) {
            if (this.currentDisc.getItem() instanceof RecordItem recordItem) {
                if (shouldRecordStopPlaying(recordItem)) {
                    this.shuffleNext();
                } else if (shouldSendJukeblockPlayingEvent()) {
                    this.ticksSinceLastEvent = 0;
                    this.spawnMusicParticles(pLevel, this.getBlockPos());
                }
            }
        }
        ++this.tickCount;
    }

    public void startPlaying() {
        this.recordStartedTick = this.tickCount;
        this.currentDisc = selectRandomDisc();
        this.setPlaying();
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();

        // Send Sound Packets to Clients
        SpeakerLinkUtil.activateJukeblock(level, this.worldPosition, this.currentDisc);
        SpeakerLinkUtil.activateSpeakersJukeblock(level, this.worldPosition, this.currentDisc);
    }
    public void stopPlaying() {
        this.currentDisc = ItemStack.EMPTY;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();

        SpeakerLinkUtil.deactivateJukeblock(level, this.worldPosition);
        SpeakerLinkUtil.deactivateSpeakersJukeblock(level, this.worldPosition);
    }

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
        SpeakerLinkUtil.activateJukeblock(level, this.worldPosition, this.currentDisc);
        SpeakerLinkUtil.activateSpeakersJukeblock(level, this.worldPosition, this.currentDisc);
    }

    public ItemStack selectRandomDisc() {
        List<ItemStack> availableSlots = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() instanceof RecordItem) {
                availableSlots.add(stack);
            }
        }

        if (availableSlots.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (availableSlots.size() > 1 && currentDisc != null) {
            availableSlots.removeIf(stack -> ItemStack.isSameItem(stack, currentDisc));
            if (availableSlots.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return availableSlots.get(new Random().nextInt(availableSlots.size()));
    }

    private boolean shouldSendJukeblockPlayingEvent() {
        return this.ticksSinceLastEvent >= 20;
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

    // Menus
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new JukeboxMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void onChunkUnloaded() {
        this.setStopped();
        super.onChunkUnloaded();
    }

    // Block data
    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.isPlaying = pTag.getInt("isPlaying");
        this.isStopped = pTag.getInt("isStopped");
        this.volume = pTag.getInt("Volume");
        this.particlesEnabled = pTag.getInt("ParticlesActive");
        this.recordStartedTick = pTag.getLong("RecordStartTick");
        this.tickCount = pTag.getLong("TickCount");
        this.itemHandler.deserializeNBT(pTag.getCompound("inventory"));
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
        pTag.put("inventory", this.itemHandler.serializeNBT());
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.betterdiscs.jukebox");
    }
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> itemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}

