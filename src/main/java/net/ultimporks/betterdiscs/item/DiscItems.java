package net.ultimporks.betterdiscs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;

public class DiscItems {
    public static final Set<Item> MUSIC_DISCS = Set.of(
            // Vanilla Discs
            Items.MUSIC_DISC_5,
            Items.MUSIC_DISC_11,
            Items.MUSIC_DISC_13,
            Items.MUSIC_DISC_BLOCKS,
            Items.MUSIC_DISC_CAT,
            Items.MUSIC_DISC_CHIRP,
            Items.MUSIC_DISC_CREATOR,
            Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
            Items.MUSIC_DISC_FAR,
            Items.MUSIC_DISC_MALL,
            Items.MUSIC_DISC_MELLOHI,
            Items.MUSIC_DISC_OTHERSIDE,
            Items.MUSIC_DISC_PIGSTEP,
            Items.MUSIC_DISC_PRECIPICE,
            Items.MUSIC_DISC_RELIC,
            Items.MUSIC_DISC_STAL,
            Items.MUSIC_DISC_STRAD,
            Items.MUSIC_DISC_WAIT,
            Items.MUSIC_DISC_WARD


    );

    public static boolean isMusicDisc(ItemStack stack) {
        return MUSIC_DISCS.contains(stack.getItem());
    }

}
