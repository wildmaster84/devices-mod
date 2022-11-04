package com.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class DevicesModelGenerator extends FabricModelProvider {

    public DevicesModelGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        DeviceBlocks.LAPTOPS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_closed"), () -> new Gson().fromJson(String.format(laptopClosedPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_full"), () -> new Gson().fromJson(String.format(laptopFullPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_flitem"), () -> new Gson().fromJson(String.format(laptopFullItemPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(laptopStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get(), "_flitem"));
        });

        //new BlockStateGenerator() {
        //                @Override
        //                public Block getBlock() {
        //                    return block.get();
        //                }
        //
        //                @Override
        //                public JsonElement get() {
        //                    return new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class);
        //                }
        //            }
    }

    private String laptopPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptoppain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopClosedPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptopclosedpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptopstatepain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptopfullpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullItemPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptopfullitempain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
