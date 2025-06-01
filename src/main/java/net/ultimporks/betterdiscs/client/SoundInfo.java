package net.ultimporks.betterdiscs.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

public class SoundInfo {
    public final SoundInstance soundInstance;
    public final SoundEvent soundEvent;
    public final long totalDurationTicks;
    public boolean isPaused = false;
    public long elapsedTime;

    public SoundInfo(SoundInstance soundInstance, SoundEvent soundEvent, long elapsedTime, long totalDurationTicks) {
        this.soundInstance = soundInstance;
        this.soundEvent = soundEvent;
        this.elapsedTime = elapsedTime;
        this.totalDurationTicks = totalDurationTicks;
    }

}
