package com.ultreon.devices.data.tags;

import com.ultreon.devices.Reference;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator pGenerator, ModBlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, blockTagsProvider, Reference.MOD_ID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Item Tags";
    }

    @Override
    protected void addTags() {
        TagAppender<Item> laptops = this.tag(ModTags.Items.LAPTOPS);
        TagAppender<Item> printers = this.tag(ModTags.Items.PRINTERS);
        TagAppender<Item> routers = this.tag(ModTags.Items.ROUTERS);
        TagAppender<Item> flashDrives = this.tag(ModTags.Items.FLASH_DRIVES);

        DeviceItems.getAllLaptops().forEach(laptops::add);
        DeviceItems.getAllPrinters().forEach(printers::add);
        DeviceItems.getAllRouters().forEach(routers::add);
        DeviceItems.getAllFlashDrives().forEach(flashDrives::add);
    }
}
