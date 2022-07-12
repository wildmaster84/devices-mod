package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.PaperBlock;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

import static com.ultreon.devices.Devices.id;

public class DeviceBlocks {
    private static final Registrar<Block> REGISTER = Devices.REGISTRIES.get().get(Registry.BLOCK_REGISTRY);

    public static void register() {
    }

    public static final RegistrySupplier<LaptopBlock> WHITE_LAPTOP = REGISTER.register(id("white_laptop"), () -> new LaptopBlock(DyeColor.WHITE));
    public static final RegistrySupplier<LaptopBlock> ORANGE_LAPTOP = REGISTER.register(id("orange_laptop"), () -> new LaptopBlock(DyeColor.ORANGE));
    public static final RegistrySupplier<LaptopBlock> MAGENTA_LAPTOP = REGISTER.register(id("magenta_laptop"), () -> new LaptopBlock(DyeColor.MAGENTA));
    public static final RegistrySupplier<LaptopBlock> LIGHT_BLUE_LAPTOP = REGISTER.register(id("light_blue_laptop"), () -> new LaptopBlock(DyeColor.LIGHT_BLUE));
    public static final RegistrySupplier<LaptopBlock> YELLOW_LAPTOP = REGISTER.register(id("yellow_laptop"), () -> new LaptopBlock(DyeColor.YELLOW));
    public static final RegistrySupplier<LaptopBlock> LIME_LAPTOP = REGISTER.register(id("lime_laptop"), () -> new LaptopBlock(DyeColor.LIME));
    public static final RegistrySupplier<LaptopBlock> PINK_LAPTOP = REGISTER.register(id("pink_laptop"), () -> new LaptopBlock(DyeColor.PINK));
    public static final RegistrySupplier<LaptopBlock> GRAY_LAPTOP = REGISTER.register(id("gray_laptop"), () -> new LaptopBlock(DyeColor.GRAY));
    public static final RegistrySupplier<LaptopBlock> LIGHT_GRAY_LAPTOP = REGISTER.register(id("light_gray_laptop"), () -> new LaptopBlock(DyeColor.LIGHT_GRAY));
    public static final RegistrySupplier<LaptopBlock> CYAN_LAPTOP = REGISTER.register(id("cyan_laptop"), () -> new LaptopBlock(DyeColor.CYAN));
    public static final RegistrySupplier<LaptopBlock> PURPLE_LAPTOP = REGISTER.register(id("purple_laptop"), () -> new LaptopBlock(DyeColor.PURPLE));
    public static final RegistrySupplier<LaptopBlock> BLUE_LAPTOP = REGISTER.register(id("blue_laptop"), () -> new LaptopBlock(DyeColor.BLUE));
    public static final RegistrySupplier<LaptopBlock> BROWN_LAPTOP = REGISTER.register(id("brown_laptop"), () -> new LaptopBlock(DyeColor.BROWN));
    public static final RegistrySupplier<LaptopBlock> GREEN_LAPTOP = REGISTER.register(id("green_laptop"), () -> new LaptopBlock(DyeColor.GREEN));
    public static final RegistrySupplier<LaptopBlock> RED_LAPTOP = REGISTER.register(id("red_laptop"), () -> new LaptopBlock(DyeColor.RED));
    public static final RegistrySupplier<LaptopBlock> BLACK_LAPTOP = REGISTER.register(id("black_laptop"), () -> new LaptopBlock(DyeColor.BLACK));

    public static final RegistrySupplier<PrinterBlock> WHITE_PRINTER = REGISTER.register(id("white_printer"), () -> new PrinterBlock(DyeColor.WHITE));
    public static final RegistrySupplier<PrinterBlock> ORANGE_PRINTER = REGISTER.register(id("orange_printer"), () -> new PrinterBlock(DyeColor.ORANGE));
    public static final RegistrySupplier<PrinterBlock> MAGENTA_PRINTER = REGISTER.register(id("magenta_printer"), () -> new PrinterBlock(DyeColor.MAGENTA));
    public static final RegistrySupplier<PrinterBlock> LIGHT_BLUE_PRINTER = REGISTER.register(id("light_blue_printer"), () -> new PrinterBlock(DyeColor.LIGHT_BLUE));
    public static final RegistrySupplier<PrinterBlock> YELLOW_PRINTER = REGISTER.register(id("yellow_printer"), () -> new PrinterBlock(DyeColor.YELLOW));
    public static final RegistrySupplier<PrinterBlock> LIME_PRINTER = REGISTER.register(id("lime_printer"), () -> new PrinterBlock(DyeColor.LIME));
    public static final RegistrySupplier<PrinterBlock> PINK_PRINTER = REGISTER.register(id("pink_printer"), () -> new PrinterBlock(DyeColor.PINK));
    public static final RegistrySupplier<PrinterBlock> GRAY_PRINTER = REGISTER.register(id("gray_printer"), () -> new PrinterBlock(DyeColor.GRAY));
    public static final RegistrySupplier<PrinterBlock> LIGHT_GRAY_PRINTER = REGISTER.register(id("light_gray_printer"), () -> new PrinterBlock(DyeColor.LIGHT_GRAY));
    public static final RegistrySupplier<PrinterBlock> CYAN_PRINTER = REGISTER.register(id("cyan_printer"), () -> new PrinterBlock(DyeColor.CYAN));
    public static final RegistrySupplier<PrinterBlock> PURPLE_PRINTER = REGISTER.register(id("purple_printer"), () -> new PrinterBlock(DyeColor.PURPLE));
    public static final RegistrySupplier<PrinterBlock> BLUE_PRINTER = REGISTER.register(id("blue_printer"), () -> new PrinterBlock(DyeColor.BLUE));
    public static final RegistrySupplier<PrinterBlock> BROWN_PRINTER = REGISTER.register(id("brown_printer"), () -> new PrinterBlock(DyeColor.BROWN));
    public static final RegistrySupplier<PrinterBlock> GREEN_PRINTER = REGISTER.register(id("green_printer"), () -> new PrinterBlock(DyeColor.GREEN));
    public static final RegistrySupplier<PrinterBlock> RED_PRINTER = REGISTER.register(id("red_printer"), () -> new PrinterBlock(DyeColor.RED));
    public static final RegistrySupplier<PrinterBlock> BLACK_PRINTER = REGISTER.register(id("black_printer"), () -> new PrinterBlock(DyeColor.BLACK));

    public static final RegistrySupplier<RouterBlock> WHITE_ROUTER = REGISTER.register(id("white_router"), () -> new RouterBlock(DyeColor.WHITE));
    public static final RegistrySupplier<RouterBlock> ORANGE_ROUTER = REGISTER.register(id("orange_router"), () -> new RouterBlock(DyeColor.ORANGE));
    public static final RegistrySupplier<RouterBlock> MAGENTA_ROUTER = REGISTER.register(id("magenta_router"), () -> new RouterBlock(DyeColor.MAGENTA));
    public static final RegistrySupplier<RouterBlock> LIGHT_BLUE_ROUTER = REGISTER.register(id("light_blue_router"), () -> new RouterBlock(DyeColor.LIGHT_BLUE));
    public static final RegistrySupplier<RouterBlock> YELLOW_ROUTER = REGISTER.register(id("yellow_router"), () -> new RouterBlock(DyeColor.YELLOW));
    public static final RegistrySupplier<RouterBlock> LIME_ROUTER = REGISTER.register(id("lime_router"), () -> new RouterBlock(DyeColor.LIME));
    public static final RegistrySupplier<RouterBlock> PINK_ROUTER = REGISTER.register(id("pink_router"), () -> new RouterBlock(DyeColor.PINK));
    public static final RegistrySupplier<RouterBlock> GRAY_ROUTER = REGISTER.register(id("gray_router"), () -> new RouterBlock(DyeColor.GRAY));
    public static final RegistrySupplier<RouterBlock> LIGHT_GRAY_ROUTER = REGISTER.register(id("light_gray_router"), () -> new RouterBlock(DyeColor.LIGHT_GRAY));
    public static final RegistrySupplier<RouterBlock> CYAN_ROUTER = REGISTER.register(id("cyan_router"), () -> new RouterBlock(DyeColor.CYAN));
    public static final RegistrySupplier<RouterBlock> PURPLE_ROUTER = REGISTER.register(id("purple_router"), () -> new RouterBlock(DyeColor.PURPLE));
    public static final RegistrySupplier<RouterBlock> BLUE_ROUTER = REGISTER.register(id("blue_router"), () -> new RouterBlock(DyeColor.BLUE));
    public static final RegistrySupplier<RouterBlock> BROWN_ROUTER = REGISTER.register(id("brown_router"), () -> new RouterBlock(DyeColor.BROWN));
    public static final RegistrySupplier<RouterBlock> GREEN_ROUTER = REGISTER.register(id("green_router"), () -> new RouterBlock(DyeColor.GREEN));
    public static final RegistrySupplier<RouterBlock> RED_ROUTER = REGISTER.register(id("red_router"), () -> new RouterBlock(DyeColor.RED));
    public static final RegistrySupplier<RouterBlock> BLACK_ROUTER = REGISTER.register(id("black_router"), () -> new RouterBlock(DyeColor.BLACK));
    public static final RegistrySupplier<PaperBlock> PAPER = REGISTER.register(id("paper"), PaperBlock::new);


    public static Stream<Block> getAllBlocks() {
        return REGISTER.getIds().stream().map(REGISTER::get);
    }

    public static List<LaptopBlock> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).toList();
    }

    public static List<PrinterBlock> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).toList();
    }

    public static List<RouterBlock> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).toList();
    }
}
