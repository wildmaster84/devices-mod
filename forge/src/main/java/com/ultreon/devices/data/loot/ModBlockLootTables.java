package com.ultreon.devices.data.loot;

import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ModBlockLootTables extends BlockLoot {
    public ModBlockLootTables() {

    }

    @Override
    protected void addTables() {
        DeviceBlocks.getAllLaptops().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock, Registry.BLOCK_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock, Registry.BLOCK_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllPrinters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock, Registry.BLOCK_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock, Registry.BLOCK_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllRouters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock, Registry.BLOCK_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            if (Registries.getId(pBlock.asItem(), Registry.ITEM_REGISTRY).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + Registries.getId(pBlock, Registry.BLOCK_REGISTRY));
            }
            dropSelf(pBlock);
        });
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return DeviceBlocks.getAllBlocks().filter(pBlock -> pBlock.asItem() != Items.AIR && pBlock != DeviceBlocks.PAPER.get()).toList();
    }
}
