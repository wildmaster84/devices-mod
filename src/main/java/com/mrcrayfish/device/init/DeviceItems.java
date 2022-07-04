package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.item.ColoredBlockItem;
import com.mrcrayfish.device.item.FlashDriveItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class DeviceItems {
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    // Laptops
    public static final RegistryObject<BlockItem> WHITE_LAPTOP = REGISTER.register("white_laptop", () -> new ColoredBlockItem(DeviceBlocks.WHITE_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.WHITE));
    public static final RegistryObject<BlockItem> ORANGE_LAPTOP = REGISTER.register("orange_laptop", () -> new ColoredBlockItem(DeviceBlocks.ORANGE_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.ORANGE));
    public static final RegistryObject<BlockItem> MAGENTA_LAPTOP = REGISTER.register("magenta_laptop", () -> new ColoredBlockItem(DeviceBlocks.MAGENTA_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.MAGENTA));
    public static final RegistryObject<BlockItem> LIGHT_BLUE_LAPTOP = REGISTER.register("light_blue_laptop", () -> new ColoredBlockItem(DeviceBlocks.LIGHT_BLUE_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.LIGHT_BLUE));
    public static final RegistryObject<BlockItem> YELLOW_LAPTOP = REGISTER.register("yellow_laptop", () -> new ColoredBlockItem(DeviceBlocks.YELLOW_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.YELLOW));
    public static final RegistryObject<BlockItem> LIME_LAPTOP = REGISTER.register("lime_laptop", () -> new ColoredBlockItem(DeviceBlocks.LIME_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.LIME));
    public static final RegistryObject<BlockItem> PINK_LAPTOP = REGISTER.register("pink_laptop", () -> new ColoredBlockItem(DeviceBlocks.PINK_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.PINK));
    public static final RegistryObject<BlockItem> GRAY_LAPTOP = REGISTER.register("gray_laptop", () -> new ColoredBlockItem(DeviceBlocks.GRAY_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.GRAY));
    public static final RegistryObject<BlockItem> LIGHT_GRAY_LAPTOP = REGISTER.register("light_gray_laptop", () -> new ColoredBlockItem(DeviceBlocks.LIGHT_GRAY_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.LIGHT_GRAY));
    public static final RegistryObject<BlockItem> CYAN_LAPTOP = REGISTER.register("cyan_laptop", () -> new ColoredBlockItem(DeviceBlocks.CYAN_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.CYAN));
    public static final RegistryObject<BlockItem> PURPLE_LAPTOP = REGISTER.register("purple_laptop", () -> new ColoredBlockItem(DeviceBlocks.PURPLE_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.PURPLE));
    public static final RegistryObject<BlockItem> BLUE_LAPTOP = REGISTER.register("blue_laptop", () -> new ColoredBlockItem(DeviceBlocks.BLUE_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.BLUE));
    public static final RegistryObject<BlockItem> BROWN_LAPTOP = REGISTER.register("brown_laptop", () -> new ColoredBlockItem(DeviceBlocks.BROWN_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.BROWN));
    public static final RegistryObject<BlockItem> GREEN_LAPTOP = REGISTER.register("green_laptop", () -> new ColoredBlockItem(DeviceBlocks.GREEN_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.GREEN));
    public static final RegistryObject<BlockItem> RED_LAPTOP = REGISTER.register("red_laptop", () -> new ColoredBlockItem(DeviceBlocks.RED_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.RED));
    public static final RegistryObject<BlockItem> BLACK_LAPTOP = REGISTER.register("black_laptop", () -> new ColoredBlockItem(DeviceBlocks.BLACK_LAPTOP.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE), DyeColor.BLACK));

    // Flash drives
    public static final RegistryObject<FlashDriveItem> WHITE_FLASH_DRIVE = REGISTER.register("white_flash_drive", () -> new FlashDriveItem(DyeColor.WHITE));
    public static final RegistryObject<FlashDriveItem> ORANGE_FLASH_DRIVE = REGISTER.register("orange_flash_drive", () -> new FlashDriveItem(DyeColor.ORANGE));
    public static final RegistryObject<FlashDriveItem> MAGENTA_FLASH_DRIVE = REGISTER.register("magenta_flash_drive", () -> new FlashDriveItem(DyeColor.MAGENTA));
    public static final RegistryObject<FlashDriveItem> LIGHT_BLUE_FLASH_DRIVE = REGISTER.register("light_blue_flash_drive", () -> new FlashDriveItem(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<FlashDriveItem> YELLOW_FLASH_DRIVE = REGISTER.register("yellow_flash_drive", () -> new FlashDriveItem(DyeColor.YELLOW));
    public static final RegistryObject<FlashDriveItem> LIME_FLASH_DRIVE = REGISTER.register("lime_flash_drive", () -> new FlashDriveItem(DyeColor.LIME));
    public static final RegistryObject<FlashDriveItem> PINK_FLASH_DRIVE = REGISTER.register("pink_flash_drive", () -> new FlashDriveItem(DyeColor.PINK));
    public static final RegistryObject<FlashDriveItem> GRAY_FLASH_DRIVE = REGISTER.register("gray_flash_drive", () -> new FlashDriveItem(DyeColor.GRAY));
    public static final RegistryObject<FlashDriveItem> LIGHT_GRAY_FLASH_DRIVE = REGISTER.register("light_gray_flash_drive", () -> new FlashDriveItem(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<FlashDriveItem> CYAN_FLASH_DRIVE = REGISTER.register("cyan_flash_drive", () -> new FlashDriveItem(DyeColor.CYAN));
    public static final RegistryObject<FlashDriveItem> PURPLE_FLASH_DRIVE = REGISTER.register("purple_flash_drive", () -> new FlashDriveItem(DyeColor.PURPLE));
    public static final RegistryObject<FlashDriveItem> BLUE_FLASH_DRIVE = REGISTER.register("blue_flash_drive", () -> new FlashDriveItem(DyeColor.BLUE));
    public static final RegistryObject<FlashDriveItem> BROWN_FLASH_DRIVE = REGISTER.register("brown_flash_drive", () -> new FlashDriveItem(DyeColor.BROWN));
    public static final RegistryObject<FlashDriveItem> GREEN_FLASH_DRIVE = REGISTER.register("green_flash_drive", () -> new FlashDriveItem(DyeColor.GREEN));
    public static final RegistryObject<FlashDriveItem> RED_FLASH_DRIVE = REGISTER.register("red_flash_drive", () -> new FlashDriveItem(DyeColor.RED));
    public static final RegistryObject<FlashDriveItem> BLACK_FLASH_DRIVE = REGISTER.register("black_flash_drive", () -> new FlashDriveItem(DyeColor.BLACK));
    public static final RegistryObject<BlockItem> PAPER = REGISTER.register("paper", () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE)));


    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    public static Stream<Item> getAllItems() {
        return REGISTER.getEntries().stream().map(RegistryObject::get);
    }

    @Nullable
    public static FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return getAllFlashDrives().stream()
                .filter(item -> item.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public static List<FlashDriveItem> getAllFlashDrives() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof FlashDriveItem)
                .map(item -> (FlashDriveItem) item.asItem())
                .toList();
    }
}
