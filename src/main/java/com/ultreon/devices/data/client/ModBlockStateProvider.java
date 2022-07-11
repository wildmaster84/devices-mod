package com.ultreon.devices.data.client;

import com.ultreon.devices.Reference;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.DeviceBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Reference.MOD_ID, exFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Block States and Models";
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void registerStatesAndModels() {
        for (LaptopBlock block : DeviceBlocks.getAllLaptops()) {
            laptop(block);
        }
        for (PrinterBlock block : DeviceBlocks.getAllPrinters()) {
            printer(block);
        }
        for (RouterBlock block : DeviceBlocks.getAllRouters()) {
            router(block);
        }
    }

    @Override
    public void simpleBlock(Block block) {
        try {
            super.simpleBlock(block);
        } catch (IllegalArgumentException e) {
            String name = Objects.requireNonNull(block.getRegistryName()).getPath();
            super.simpleBlock(block, models().cubeAll(name, modLoc("wip")));
        }
    }

    private void laptop(LaptopBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(block.getRegistryName()).getPath();
            boolean opened = state.getValue(LaptopBlock.OPEN);
            return ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(opened ? name : name + "_closed", modLoc(opened ? "block/laptop_full" : "block/laptop_closed"))
                            .texture("2", mcLoc("block/" + block.getColor().getName() + "_wool")))
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
        });
    }

    private void printer(PrinterBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(block.getRegistryName()).getPath();
            return ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(name, modLoc("block/printer"))
                            .texture("2", mcLoc("block/" + block.getColor().getName() + "_wool")))
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
        });
    }

    private void router(RouterBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(block.getRegistryName()).getPath();
            return ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(name, modLoc("block/router"))
                            .texture("1", mcLoc("block/" + block.getColor().getName() + "_wool")))
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
        });
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = block.getRegistryName();
        return new ResourceLocation(Objects.requireNonNull(name).getNamespace(), "block/" + name.getPath());
    }
}
