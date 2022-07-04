package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.LaptopBlock;
import com.mrcrayfish.device.block.PaperBlock;
import com.mrcrayfish.device.block.PrinterBlock;
import com.mrcrayfish.device.block.RouterBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<LaptopBlock> WHITE_LAPTOP = REGISTER.register("white_laptop", () -> new LaptopBlock(DyeColor.WHITE));
    public static final RegistryObject<LaptopBlock> ORANGE_LAPTOP = REGISTER.register("orange_laptop", () -> new LaptopBlock(DyeColor.ORANGE));
    public static final RegistryObject<LaptopBlock> MAGENTA_LAPTOP = REGISTER.register("magenta_laptop", () -> new LaptopBlock(DyeColor.MAGENTA));
    public static final RegistryObject<LaptopBlock> LIGHT_BLUE_LAPTOP = REGISTER.register("light_blue_laptop", () -> new LaptopBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<LaptopBlock> YELLOW_LAPTOP = REGISTER.register("yellow_laptop", () -> new LaptopBlock(DyeColor.YELLOW));
    public static final RegistryObject<LaptopBlock> LIME_LAPTOP = REGISTER.register("lime_laptop", () -> new LaptopBlock(DyeColor.LIME));
    public static final RegistryObject<LaptopBlock> PINK_LAPTOP = REGISTER.register("pink_laptop", () -> new LaptopBlock(DyeColor.PINK));
    public static final RegistryObject<LaptopBlock> GRAY_LAPTOP = REGISTER.register("gray_laptop", () -> new LaptopBlock(DyeColor.GRAY));
    public static final RegistryObject<LaptopBlock> LIGHT_GRAY_LAPTOP = REGISTER.register("light_gray_laptop", () -> new LaptopBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<LaptopBlock> CYAN_LAPTOP = REGISTER.register("cyan_laptop", () -> new LaptopBlock(DyeColor.CYAN));
    public static final RegistryObject<LaptopBlock> PURPLE_LAPTOP = REGISTER.register("purple_laptop", () -> new LaptopBlock(DyeColor.PURPLE));
    public static final RegistryObject<LaptopBlock> BLUE_LAPTOP = REGISTER.register("blue_laptop", () -> new LaptopBlock(DyeColor.BLUE));
    public static final RegistryObject<LaptopBlock> BROWN_LAPTOP = REGISTER.register("brown_laptop", () -> new LaptopBlock(DyeColor.BROWN));
    public static final RegistryObject<LaptopBlock> GREEN_LAPTOP = REGISTER.register("green_laptop", () -> new LaptopBlock(DyeColor.GREEN));
    public static final RegistryObject<LaptopBlock> RED_LAPTOP = REGISTER.register("red_laptop", () -> new LaptopBlock(DyeColor.RED));
    public static final RegistryObject<LaptopBlock> BLACK_LAPTOP = REGISTER.register("black_laptop", () -> new LaptopBlock(DyeColor.BLACK));
    public static final RegistryObject<PrinterBlock> WHITE_PRINTER = REGISTER.register("white_printer", () -> new PrinterBlock(DyeColor.WHITE));
    public static final RegistryObject<PrinterBlock> ORANGE_PRINTER = REGISTER.register("orange_printer", () -> new PrinterBlock(DyeColor.ORANGE));
    public static final RegistryObject<PrinterBlock> MAGENTA_PRINTER = REGISTER.register("magenta_printer", () -> new PrinterBlock(DyeColor.MAGENTA));
    public static final RegistryObject<PrinterBlock> LIGHT_BLUE_PRINTER = REGISTER.register("light_blue_printer", () -> new PrinterBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<PrinterBlock> YELLOW_PRINTER = REGISTER.register("yellow_printer", () -> new PrinterBlock(DyeColor.YELLOW));
    public static final RegistryObject<PrinterBlock> LIME_PRINTER = REGISTER.register("lime_printer", () -> new PrinterBlock(DyeColor.LIME));
    public static final RegistryObject<PrinterBlock> PINK_PRINTER = REGISTER.register("pink_printer", () -> new PrinterBlock(DyeColor.PINK));
    public static final RegistryObject<PrinterBlock> GRAY_PRINTER = REGISTER.register("gray_printer", () -> new PrinterBlock(DyeColor.GRAY));
    public static final RegistryObject<PrinterBlock> LIGHT_GRAY_PRINTER = REGISTER.register("light_gray_printer", () -> new PrinterBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<PrinterBlock> CYAN_PRINTER = REGISTER.register("cyan_printer", () -> new PrinterBlock(DyeColor.CYAN));
    public static final RegistryObject<PrinterBlock> PURPLE_PRINTER = REGISTER.register("purple_printer", () -> new PrinterBlock(DyeColor.PURPLE));
    public static final RegistryObject<PrinterBlock> BLUE_PRINTER = REGISTER.register("blue_printer", () -> new PrinterBlock(DyeColor.BLUE));
    public static final RegistryObject<PrinterBlock> BROWN_PRINTER = REGISTER.register("brown_printer", () -> new PrinterBlock(DyeColor.BROWN));
    public static final RegistryObject<PrinterBlock> GREEN_PRINTER = REGISTER.register("green_printer", () -> new PrinterBlock(DyeColor.GREEN));
    public static final RegistryObject<PrinterBlock> RED_PRINTER = REGISTER.register("red_printer", () -> new PrinterBlock(DyeColor.RED));
    public static final RegistryObject<PrinterBlock> BLACK_PRINTER = REGISTER.register("black_printer", () -> new PrinterBlock(DyeColor.BLACK));
    public static final RegistryObject<PaperBlock> PAPER = REGISTER.register("paper", PaperBlock::new);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    public static Stream<Block> getAllBlocks() {
        return REGISTER.getEntries().stream().map(RegistryObject::get);
    }

    public static List<LaptopBlock> getLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).toList();
    }

    public static List<PrinterBlock> getPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).toList();
    }

    public static List<RouterBlock> getRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).toList();
    }
}
