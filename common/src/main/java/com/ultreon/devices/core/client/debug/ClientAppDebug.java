package com.ultreon.devices.core.client.debug;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.core.laptop.client.ClientLaptopScreen;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Adds a button to the title screen to test system applications that don't require the system
 */
public class ClientAppDebug {
    public static void register() {

        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
            if (DeviceConfig.DEBUG_BUTTON.get()) {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = new Button(screen.width / 2 - 100, y + rowHeight * -1, 200, 20, Component.literal("DV TEST"), (button) -> {
                    Minecraft.getInstance().setScreen(new Laptop(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.WHITE_LAPTOP.get().defaultBlockState()), true));
                }, Button.NO_TOOLTIP, (sdcvb) -> {

                    return Component.empty();
                });
                access.addRenderableWidget(a);
            }
        }));

        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
            if (DeviceConfig.DEBUG_BUTTON.get()) {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = new Button(screen.width / 2 - 100, y + rowHeight * -2, 200, 20, Component.literal("DV TEST #2"), (button) -> {
                    var serverLaptop = new ServerLaptop();
                    ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
                    var clientLaptop = new ClientLaptop();
                    clientLaptop.setUuid(serverLaptop.getUuid());
                    ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
                    Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
                }, Button.NO_TOOLTIP, (scvfgre) -> Component.empty());
                access.addRenderableWidget(a);
            }
        }));
    }
}

