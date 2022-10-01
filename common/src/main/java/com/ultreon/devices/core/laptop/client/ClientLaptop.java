package com.ultreon.devices.core.laptop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


// NO STATICS
public class ClientLaptop {
    public static final HashMap<UUID, ClientLaptop> laptops = new HashMap<>(); // current active client laptops
    private UUID uuid;
    static {
        laptops.put(new UUID(430985038594038L, 493058808830598L), new ClientLaptop());
    }
    public ClientLaptop() {
        //super(Component.translatable("laptop")); //todo
    }

    public void handlePacket(String type, CompoundTag nbt) {

    }

    public void renderBezels(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) { // no bezels


    }

    //@Override
    public void render(final @NotNull PoseStack pose, float partialTicks) {
        Minecraft.getInstance().font.draw(pose, "HA", 15, 15, 0xffffffff);
    }
}
