package com.ultreon.devices.data.tags;

import com.ultreon.devices.Reference;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Reference.MOD_ID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Block Tags";
    }

    @Override
    protected void addTags() {
        TagAppender<Block> laptops = this.tag(ModTags.Blocks.LAPTOPS);
        TagAppender<Block> printers = this.tag(ModTags.Blocks.PRINTERS);
        TagAppender<Block> routers = this.tag(ModTags.Blocks.ROUTERS);

        DeviceBlocks.getAllLaptops().forEach(laptops::add);
        DeviceBlocks.getAllPrinters().forEach(printers::add);
        DeviceBlocks.getAllRouters().forEach(routers::add);
    }
}
