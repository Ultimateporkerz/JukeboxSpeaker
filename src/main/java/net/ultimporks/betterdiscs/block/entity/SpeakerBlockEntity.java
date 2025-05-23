package net.ultimporks.betterdiscs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.init.ModBlocks;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import net.ultimporks.betterdiscs.util.menus.SpeakerMenus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpeakerBlockEntity extends BlockEntity implements MenuProvider {
    private long ticksSinceSongStarted;
    @Nullable
    private Optional<Holder<JukeboxSong>> currentSong;
    private ItemStack currentDisc = ItemStack.EMPTY;

    // Playing music
    private boolean isPlaying = false;
    private boolean isPaused = false;
    public BlockPos linkedJukeboxPos;

    // Volume Control
    private int volume = 100;
    // Particle Control
    private int particlesEnabled = 200;
    // Record Tick Count
    private long tickCount;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> SpeakerBlockEntity.this.volume;
                case 1 -> SpeakerBlockEntity.this.particlesEnabled;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> SpeakerBlockEntity.this.volume = pValue;
                case 1 -> SpeakerBlockEntity.this.particlesEnabled = pValue;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SpeakerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SPEAKER_BE.get(), pPos, pBlockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.isRemoved()) return;
        ++this.ticksSinceSongStarted;
        if (this.isPlaying && !this.isPaused && currentDisc != null) {
            if (this.currentSong.get().value().hasFinished(this.ticksSinceSongStarted)) {
                SpeakerLinkUtil.deactivateSpeakerJukebox((ServerLevel) level, this.getBlockPos());
            } else if (this.shouldSendSpeakerPlayingEvent() && !getBlockState().is(ModBlocks.CEILING_SPEAKER.get())) {
                this.ticksSinceSongStarted = 0;
                this.spawnMusicParticles(level, pos);
            } else if (this.shouldSendSpeakerPlayingEvent() && getBlockState().is(ModBlocks.CEILING_SPEAKER.get())) {
                this.ticksSinceSongStarted = 0;
                this.spawnMusicParticlesCeiling(level, pos);
            }
        }
        ++this.tickCount;
    }

    public boolean isPaused() {
        return isPaused;
    }
    public boolean isPlaying() {
        return isPlaying;
    }
    private boolean shouldSendSpeakerPlayingEvent() {
        return this.ticksSinceSongStarted % 20L == 0L;
    }

    // Volume
    public void setVolume(int newVolume) {
        this.volume = newVolume;
        this.dataAccess.set(0, newVolume);
        setChanged();
        level.sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
        BetterMusicDiscs.speakerLOGGING("(SpeakerBlockEntity) - Setting volume to: " + volume);
    }
    public int getVolume() {
        return this.volume;
    }

    // Particles
    public void spawnMusicParticles(Level pLevel, BlockPos pPos) {
        if (areParticlesEnabled() && pLevel instanceof ServerLevel serverLevel) {
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos).add(0.0D, 1.2F, 0.0D);
            float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
            serverLevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, f, 0.0D, 0.0D, 1.0D);
        }
    }
    public void spawnMusicParticlesCeiling(Level pLevel, BlockPos pPos) {
        if (areParticlesEnabled() && pLevel instanceof ServerLevel serverLevel) {
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos);
            float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
            serverLevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, f, 0.0D, 0.0D, 1.0D);
        }
    }
    public void spawnParticlesForNoteBlock() {
        if (areParticlesEnabled()) {
            this.spawnMusicParticles(level, worldPosition);
        }
    }
    public void spawnParticlesForNoteblockCeiling() {
        if (areParticlesEnabled()) {
            this.spawnMusicParticlesCeiling(level, worldPosition);
        }
    }

    public void setParticlesEnabled(boolean enabled) {
        if (enabled) {
            // Enable
            this.particlesEnabled = 200;
            this.dataAccess.set(1, 200);
        } else {
            // Disable
            this.particlesEnabled = 100;
            this.dataAccess.set(1, 100);
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
    public boolean areParticlesEnabled() {
        int particlesNumber = this.particlesEnabled;

        if (particlesNumber == 100) {
            return false;
        }
        return particlesNumber == 200;
    }


    public void setActive(boolean active, ItemStack currentDisc) {
        this.isPlaying = active;
        this.isPaused = false;
        this.currentDisc = currentDisc;
        this.ticksSinceSongStarted = 0L;
        this.setChanged();
        this.level.updateNeighborsAt(getBlockPos(), level.getBlockState(getBlockPos()).getBlock());
        // sets the Song
        if (currentDisc != ItemStack.EMPTY) {
            this.currentSong = JukeboxSong.fromStack(this.level.registryAccess(), this.currentDisc);
            BetterMusicDiscs.jukeblockLOGGING("Current Song set to: " + currentSong);
        }
    }
    public void onSongChanged() {
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }


    // Menus
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SpeakerMenus(pContainerId, pPlayerInventory, this, this.dataAccess);
    }


    public int getComparatorOutput() {
        return JukeboxSong.fromStack(this.level.registryAccess(), this.currentDisc).map(Holder::value).map(JukeboxSong::comparatorOutput).orElse(0);
    }

    // Block Data
    @Override
    public void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("LinkedJukebox")) {
            this.linkedJukeboxPos = NbtUtils.readBlockPos(pTag, "LinkedJukebox").orElse(null);
        } else {
            this.linkedJukeboxPos = null;
        }
        this.isPlaying = pTag.getBoolean("isPlaying");
        this.volume = pTag.getInt("Volume");
        this.particlesEnabled = pTag.getInt("ParticlesActive");
        this.ticksSinceSongStarted = pTag.getLong("RecordStartTick");
        this.tickCount = pTag.getLong("TickCount");
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (linkedJukeboxPos != null) {
            pTag.put("LinkedJukebox", NbtUtils.writeBlockPos(this.linkedJukeboxPos));
        }
        pTag.putBoolean("isPlaying", this.isPlaying);
        pTag.putInt("Volume", this.volume);
        pTag.putInt("ParticlesActive", this.particlesEnabled);
        pTag.putLong("RecordStartTick", this.ticksSinceSongStarted);
        pTag.putLong("TickCount", this.tickCount);
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.betterdiscs.speaker");
    }
}