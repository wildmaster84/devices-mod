package com.mrcrayfish.device.data.client;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.item.FlashDriveItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    public @NotNull
    String getName() {
        return "MrCrayfish's Device Mod - Item Models";
    }

    @Override
    protected void registerModels() {
        DeviceBlocks.getAllBlocks().filter((block) -> block.getClass().getPackage().getName().startsWith("com.mrcrayfish.device")).forEach(this::blockBuilder);

        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        ModelFile itemHandheld = getExistingFile(mcLoc("item/handheld"));

//        getBuilder(DeviceItems.PAPER.get(), itemGenerated, "model/paper").transforms().transform(ItemTransforms.TransformType.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f, 0.5f, 0.5f).end().end();

        for (FlashDriveItem flashDrive : DeviceItems.getAllFlashDrives()) {
            builder(flashDrive, itemGenerated);
        }
    }

    private void blockBuilder(Block block) {
        try {
            String name = Objects.requireNonNull(block.getRegistryName()).getPath();
            withExistingParent(name, modLoc("block/" + name));
        } catch (IllegalStateException ignored) {

        }
    }

    private void builder(ItemLike item, ModelFile parent) {
        String name = Objects.requireNonNull(item.asItem().getRegistryName()).getPath();
        builder(item, parent, "item/" + name);
    }

    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent) {
        String name = Objects.requireNonNull(item.asItem().getRegistryName()).getPath();
        return getBuilder(item, parent, "item/" + name);
    }

    private void builder(ItemLike item, ModelFile parent, String texture) {
        try {
            getBuilder(Objects.requireNonNull(item.asItem().getRegistryName()).getPath())
                    .parent(parent)
                    .texture("layer0", modLoc(texture));
        } catch (IllegalArgumentException e) {
            getBuilder(Objects.requireNonNull(item.asItem().getRegistryName()).getPath())
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", modLoc("wip"));
        }
    }

    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent, String texture) {
        return getBuilder(Objects.requireNonNull(item.asItem().getRegistryName()).getPath())
                .parent(parent)
                .texture("layer0", modLoc(texture));
    }

    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent, String modelPath, String texture) {
        return getBuilder(modelPath)
                .parent(parent)
                .texture("layer0", modLoc(texture));
    }
}
