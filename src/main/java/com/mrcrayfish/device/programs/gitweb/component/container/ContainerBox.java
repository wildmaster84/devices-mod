package com.mrcrayfish.device.programs.gitweb.component.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public abstract class ContainerBox extends Component {
    public static final int WIDTH = 128;
    protected static final ResourceLocation CONTAINER_BOXES_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png");
    protected List<Slot> slots = new ArrayList<>();
    protected int boxU, boxV;
    protected int height;
    protected ItemStack icon;
    protected String title;

    public ContainerBox(int left, int top, int boxU, int boxV, int height, ItemStack icon, String title) {
        super(left, top);
        this.boxU = boxU;
        this.boxV = boxV;
        this.height = height;
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void render(PoseStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        RenderSystem.setShaderTexture(0, CONTAINER_BOXES_TEXTURE);
        RenderUtil.drawRectWithTexture(x, y + 12, boxU, boxV, WIDTH, height, WIDTH, height, 256, 256);

        int contentOffset = (WIDTH - (Laptop.font.width(title) + 8 + 4)) / 2;
        pose.pushPose();
        {
            pose.translate(x + contentOffset, y, 0);
            pose.scale(0.5f, 0.5f, 0.5f);
            RenderUtil.renderItem(0, 0, icon, false);
        }
        pose.popPose();

        RenderUtil.drawStringClipped(pose, title, x + contentOffset + 8 + 4, y, 110, Color.WHITE.getRGB(), true);

        slots.forEach(slot -> slot.render(x, y + 12));
    }

    @Override
    protected void renderOverlay(PoseStack pose, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        slots.forEach(slot -> slot.renderOverlay(pose, laptop, xPosition, yPosition + 12, mouseX, mouseY));
    }

    protected static class Slot {
        private final int slotX;
        private final int slotY;
        private final ItemStack stack;

        public Slot(int slotX, int slotY, ItemStack stack) {
            this.slotX = slotX;
            this.slotY = slotY;
            this.stack = stack;
        }

        public void render(int x, int y) {
            RenderUtil.renderItem(x + slotX, y + slotY, stack, true);
        }

        public void renderOverlay(PoseStack pose, Laptop laptop, int x, int y, int mouseX, int mouseY) {
            if (GuiHelper.isMouseWithin(mouseX, mouseY, x + slotX, y + slotY, 16, 16)) {
                if (!stack.isEmpty()) {
                    laptop.renderTooltip(pose, laptop.getTooltipFromItem(stack), Optional.empty(), mouseX, mouseY, stack);
                }
            }

            // Todo: fix this.
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableDepth();
        }

        public ItemStack getStack() {
            return stack;
        }
    }
}
