package com.ultreon.devices.core.client;

import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LaptopFontSet extends FontSet {
    public LaptopFontSet(TextureManager pTextureManager, ResourceLocation pName) {
        super(pTextureManager, pName);
    }

    @Override
    public void reload(List<GlyphProvider> pGlyphProviders) {

    }
}
