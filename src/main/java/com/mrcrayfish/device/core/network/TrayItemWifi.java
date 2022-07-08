package com.mrcrayfish.device.core.network;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.block.entity.DeviceBlockEntity;
import com.mrcrayfish.device.block.entity.RouterBlockEntity;
import com.mrcrayfish.device.core.Device;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.network.task.TaskConnect;
import com.mrcrayfish.device.core.network.task.TaskPing;
import com.mrcrayfish.device.object.TrayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrCrayfish
 */
public class TrayItemWifi extends TrayItem {
    private int pingTimer;

    public TrayItemWifi() {
        super(Icons.WIFI_NONE);
    }

    private static Layout createWifiMenu(TrayItem item) {
        Layout layout = new Layout.Context(100, 100);
        layout.setBackground((pose, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> Gui.fill(pose, x, y, x + width, y + height, new Color(0.65f, 0.65f, 0.65f, 0.9f).getRGB()));

        ItemList<Device> itemListRouters = new ItemList<>(5, 5, 90, 4);
        itemListRouters.setItems(getRouters());
        itemListRouters.setListItemRenderer(new ListItemRenderer<>(16) {
            @Override
            public void render(PoseStack pose, Device device, GuiComponent gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                Gui.fill(pose, x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
                RenderUtil.drawStringClipped(pose, device.getName(), x + 16, y + 4, 70, Color.WHITE.getRGB(), false);

                if (device.getPos() == null) return;

                BlockPos laptopPos = Laptop.getPos();
                assert laptopPos != null;
                double distance = Math.sqrt(device.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
                if (distance > 20) {
                    Icons.WIFI_LOW.draw(pose, mc, x + 3, y + 3);
                } else if (distance > 10) {
                    Icons.WIFI_MED.draw(pose, mc, x + 3, y + 3);
                } else {
                    Icons.WIFI_HIGH.draw(pose, mc, x + 3, y + 3);
                }
            }
        });
        itemListRouters.sortBy((o1, o2) -> {
            BlockPos laptopPos = Laptop.getPos();
            assert o1.getPos() != null;
            assert laptopPos != null;
            double distance1 = Math.sqrt(o1.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
            assert o2.getPos() != null;
            double distance2 = Math.sqrt(o2.getPos().distToCenterSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
            return Double.compare(distance1, distance2);
        });
        layout.addComponent(itemListRouters);

        com.mrcrayfish.device.api.app.component.Button buttonConnect = new com.mrcrayfish.device.api.app.component.Button(79, 79, Icons.CHECK);
        buttonConnect.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (itemListRouters.getSelectedItem() != null) {
                    TaskConnect connect = new TaskConnect(Laptop.getPos(), itemListRouters.getSelectedItem().getPos());
                    connect.setCallback((tagCompound, success) -> {
                        if (success) {
                            item.setIcon(Icons.WIFI_HIGH);
                            Laptop.getSystem().closeContext();
                        }
                    });
                    TaskManager.sendTask(connect);
                }
            }
        });
        layout.addComponent(buttonConnect);

        return layout;
    }

    private static List<Device> getRouters() {
        List<Device> routers = new ArrayList<>();

        Level level = Minecraft.getInstance().level;
        BlockPos laptopPos = Laptop.getPos();
        int range = DeviceConfig.SIGNAL_RANGE.get();

        for (int y = -range; y < range + 1; y++) {
            for (int z = -range; z < range + 1; z++) {
                for (int x = -range; x < range + 1; x++) {
                    assert laptopPos != null;
                    BlockPos pos = new BlockPos(laptopPos.getX() + x, laptopPos.getY() + y, laptopPos.getZ() + z);
                    assert level != null;
                    BlockEntity tileEntity = level.getBlockEntity(pos);
                    if (tileEntity instanceof RouterBlockEntity) {
                        routers.add(new Device((DeviceBlockEntity) tileEntity));
                    }
                }
            }
        }
        return routers;
    }

    @Override
    public void init() {
        this.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (Laptop.getSystem().hasContext()) {
                Laptop.getSystem().closeContext();
            } else {
                Laptop.getSystem().openContext(createWifiMenu(this), mouseX - 100, mouseY - 100);
            }
        });

        runPingTask();
    }

    @Override
    public void tick() {
        if (++pingTimer >= DeviceConfig.PING_RATE.get()) {
            runPingTask();
            pingTimer = 0;
        }
    }

    private void runPingTask() {
        TaskPing task = new TaskPing(Laptop.getPos());
        task.setCallback((tagCompound, success) -> {
            if (success) {
                assert tagCompound != null;
                int strength = tagCompound.getInt("strength");
                switch (strength) {
                    case 2 -> setIcon(Icons.WIFI_LOW);
                    case 1 -> setIcon(Icons.WIFI_MED);
                    case 0 -> setIcon(Icons.WIFI_HIGH);
                    default -> setIcon(Icons.WIFI_NONE);
                }
            } else {
                setIcon(Icons.WIFI_NONE);
            }
        });
        TaskManager.sendTask(task);
    }
}
