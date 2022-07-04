package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * @author Qboi
 */
public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> LAPTOPS = createTag("laptops");
        public static final TagKey<Item> PRINTERS = createTag("printers");
        public static final TagKey<Item> FLASH_DRIVES = createTag("flash_drives");
        public static final TagKey<Item> ROUTERS = createTag("routers");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(MrCrayfishDeviceMod.res(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> LAPTOPS = createTag("laptops");
        public static final TagKey<Block> PRINTERS = createTag("printers");
        public static final TagKey<Block> ROUTERS = createTag("routers");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(MrCrayfishDeviceMod.res(name));
        }
    }
}
