package com.mrcrayfish.device.core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public class ClientNotification implements Toast {
    private static final ResourceLocation TEXTURE_TOASTS = new ResourceLocation("cdm:textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {
    }

    @NotNull
    @Override
    public Visibility render(@NotNull PoseStack pose, ToastComponent toastComponent, long timeSinceLastVisible) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE_TOASTS);
        toastComponent.blit(pose, 0, 0, 0, 0, 160, 32);

        if (subTitle == null) {
            toastComponent.getMinecraft().font.drawShadow(pose, RenderUtil.clipStringToWidth(I18n.get(title), 118), 38, 12, -1);
        } else {
            toastComponent.getMinecraft().font.drawShadow(pose, RenderUtil.clipStringToWidth(I18n.get(title), 118), 38, 7, -1);
            toastComponent.getMinecraft().font.draw(pose, RenderUtil.clipStringToWidth(I18n.get(subTitle), 118), 38, 18, -1);
        }

        RenderSystem.setShaderTexture(0, icon.getIconAsset());
        RenderUtil.drawRectWithTexture(6, 6, icon.getU(), icon.getV(), icon.getGridWidth(), icon.getGridHeight(), icon.getIconSize(), icon.getIconSize(), icon.getSourceWidth(), icon.getSourceHeight());

        return timeSinceLastVisible >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public static ClientNotification loadFromTag(CompoundTag tag) {
        ClientNotification notification = new ClientNotification();

        int ordinal = tag.getCompound("icon").getInt("ordinal");
        String className = tag.getCompound("icon").getString("className");

        try {
            notification.icon = (IIcon) Class.forName(className).getEnumConstants()[ordinal];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        notification.title = tag.getString("title");
        if (tag.contains("subTitle", Tag.TAG_STRING)) {
            notification.subTitle = tag.getString("subTitle");
        }

        return notification;
    }

    public void push()
    {
        Minecraft.getInstance().getToasts().addToast(this);
    }
}
