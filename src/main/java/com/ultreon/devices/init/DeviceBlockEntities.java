package com.ultreon.devices.init;

import com.ultreon.devices.Reference;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.block.entity.PaperBlockEntity;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<BlockEntityType<PaperBlockEntity>> PAPER = REGISTER.register("paper", () -> BlockEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistryObject<BlockEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register("laptop", () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistryObject<BlockEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register("printer", () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistryObject<BlockEntityType<RouterBlockEntity>> ROUTER = REGISTER.register("router", () -> BlockEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
