package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.entity.SeatEntity;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registry.ENTITY_TYPE_REGISTRY);

    public static final RegistrySupplier<EntityType<SeatEntity>> SEAT = REGISTER.register(Devices.id("seat"), () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));

    public static void register() {

    }
}
