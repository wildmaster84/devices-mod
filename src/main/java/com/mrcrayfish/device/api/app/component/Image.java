package com.mrcrayfish.device.api.app.component;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Image extends Component {
    public static final Map<String, CachedImage> CACHE = new HashMap<>();
    protected ImageLoader loader;
    protected CachedImage image;
    protected boolean initialized = false;
    protected boolean drawFull = false;
    protected int imageU, imageV;
    protected int imageWidth, imageHeight;
    protected int sourceWidth, sourceHeight;
    protected int componentWidth, componentHeight;
    private Spinner spinner;
    private float alpha = 1f;

    private boolean hasBorder = false;
    private int borderColor = Color.BLACK.getRGB();
    private int borderThickness = 1;

    public Image(int left, int top, int width, int height) {
        super(left, top);
        this.componentWidth = width;
        this.componentHeight = height;
    }

    /**
     * Creates a new Image using a ResourceLocation. This automatically sets the width and height of
     * the component according to the width and height of the image.
     *
     * @param left        the amount of pixels to be offset from the left
     * @param top         the amount of pixels to be offset from the top
     * @param imageU      the u position on the image resource
     * @param imageV      the v position on the image resource
     * @param imageWidth  the image width
     * @param imageHeight the image height
     * @param resource    the resource location of the image
     */
    public Image(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) {
        this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
    }

    /**
     * Creates a new Image using a ResourceLocation. This constructor allows the specification of
     * the width and height of the component instead of automatically unlike
     * {@link Image#Image(int, int, int, int, int, int, ResourceLocation)}
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param imageU          the u position on the image resource
     * @param imageV          the v position on the image resource
     * @param imageWidth      the image width
     * @param imageHeight     the image height
     * @param resource        the resource location of the image
     */
    public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) {
        this(left, top, componentWidth, componentHeight, imageU, imageV, imageWidth, imageHeight, 256, 256, resource);
    }

    public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, int sourceWidth, int sourceHeight, ResourceLocation resource) {
        super(left, top);
        this.loader = new StandardLoader(resource);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = imageU;
        this.imageV = imageV;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    /**
     * Creates a new Image from a url. This allows a resource to be downloaded from the internet
     * and be used as the Image. In the case that the resource could not be downloaded or the player
     * is playing the game in an offline state, the Image will default to a missing texture.
     * <p>
     * It should be noted that the remote resource is cached, so updating it may not result in an
     * instant change. Caching has a default limit of 10 resources but this can be changed by the
     * player in the configuration.
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param url             the url of the resource
     */
    public Image(int left, int top, int componentWidth, int componentHeight, String url) {
        super(left, top);
        this.loader = new DynamicLoader(url);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.drawFull = true;
    }

    public Image(int left, int top, IIcon icon) {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = icon.getIconSize();
        this.componentHeight = icon.getIconSize();
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
        this.sourceWidth = icon.getGridWidth() * icon.getIconSize();
        this.sourceHeight = icon.getGridHeight() * icon.getIconSize();
    }

    public Image(int left, int top, int componentWidth, int componentHeight, IIcon icon) {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
        this.sourceWidth = icon.getGridWidth() * icon.getIconSize();
        this.sourceHeight = icon.getGridHeight() * icon.getIconSize();
    }

    @Override
    public void init(Layout layout) {
        spinner = new Spinner(left + (componentWidth / 2) - 6, top + (componentHeight / 2) - 6);
        layout.addComponent(spinner);
        initialized = true;
    }

    @Override
    public void handleLoad() {
        this.reload();
    }

    @Override
    protected void handleUnload() {
        this.initialized = false;
    }

    @Override
    public void render(PoseStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (loader != null && loader.setup) {
                image = loader.load(this);
                spinner.setVisible(false);
                loader.setup = false;
            }

            if (hasBorder) {
                fill(pose, x, y, x + componentWidth, y + componentHeight, borderColor);
            }

            if (image != null && image.textureId != -1) {
                image.restore();

                RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
                RenderSystem.enableBlend();
                RenderSystem.bindTexture(image.textureId);

                if (hasBorder) {
                    if (drawFull) {
                        GuiComponent.blit(pose, x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, 256, 256);
                    } else {
                        GuiComponent.blit(pose, x + borderThickness, y + borderThickness, componentWidth - borderThickness * 2, imageU, imageV, componentHeight - borderThickness * 2, sourceWidth, sourceHeight, imageWidth, imageHeight);
                    }
                } else {
                    if (drawFull) {
                        GuiComponent.blit(pose, x, y, componentWidth, componentHeight, imageU, imageV, 256, 256);
                    } else {
                        GuiComponent.blit(pose, x, y, componentWidth, componentHeight, imageU, imageV, sourceWidth, sourceHeight, imageWidth, imageHeight);
                    }
                }
            } else {
                if (hasBorder) {
                    fill(pose, x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, Color.LIGHT_GRAY.getRGB());
                } else {
                    fill(pose, x, y, x + componentWidth, y + componentHeight, Color.LIGHT_GRAY.getRGB());
                }
            }
        }
    }

    public void reload() {
        if (loader != null) {
            loader.setup(this);
        }
    }

    public void setImage(ResourceLocation resource) {
        setLoader(new StandardLoader(resource));
        this.drawFull = true;
    }

    public void setImage(String url) {
        setLoader(new DynamicLoader(url));
        this.drawFull = true;
    }

    private void setLoader(ImageLoader loader) {
        this.loader = loader;
        if (initialized) {
            loader.setup(this);
            spinner.setVisible(true);
        }
    }

    /**
     * Sets the alpha for this image. Must be in the range
     * of 0f to 1f
     *
     * @param alpha how transparent you want it to be.
     */
    public void setAlpha(float alpha) {
        if (alpha < 0f) {
            this.alpha = 0f;
            return;
        }
        if (alpha > 1f) {
            this.alpha = 1f;
            return;
        }
        this.alpha = alpha;
    }

    /**
     * Makes it so the border shows
     *
     * @param show should the border show
     */
    public void setBorderVisible(boolean show) {
        this.hasBorder = show;
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    private void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the thickness of the border
     *
     * @param thickness how thick in pixels
     */
    public void setBorderThickness(int thickness) {
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull) {
        this.drawFull = drawFull;
    }

    /**
     * Image Loader
     */
    private static abstract class ImageLoader {
        protected boolean setup = false;

        public final boolean isSetup() {
            return setup;
        }

        protected void setup(Image image) {
            setup = false;
        }

        public abstract CachedImage load(Image image);
    }

    private static class StandardLoader extends ImageLoader {
        private final AbstractTexture texture;
        private final ResourceLocation resource;

        public StandardLoader(ResourceLocation resource) {
            this.texture = new SimpleTexture(resource);
            this.resource = resource;
        }

        @Override
        protected void setup(Image image) {
            setup = true;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public CachedImage load(Image image) {
            @Nullable AbstractTexture textureObj = Minecraft.getInstance().getTextureManager().getTexture(resource, null);
            if (textureObj != null) {
                return new CachedImage(textureObj.getId(), 0, 0, false);
            } else {
                AbstractTexture texture = new SimpleTexture(resource);
                Minecraft.getInstance().getTextureManager().register(resource, texture);
                return new CachedImage(texture.getId(), 0, 0, false);
            }
        }
    }

    private static class DynamicLoader extends ImageLoader {
        private final String url;
        private AbstractTexture texture;

        public DynamicLoader(String url) {
            this.url = url;
        }

        @Override
        public void setup(final Image image) {
            if (CACHE.containsKey(url)) {
                setup = true;
                return;
            }
            Runnable r = () -> {
                Laptop.runLater(() -> {
                    try {
                        URL url = new URL(this.url);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                        texture = new DynamicTexture(conn.getInputStream());
                        setup = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            };
            Thread thread = new Thread(r, "Image Loader");
            thread.start();
        }

        @Override
        public CachedImage load(Image image) {
            if (CACHE.containsKey(url)) {
                CachedImage cachedImage = CACHE.get(url);
                image.imageWidth = cachedImage.width;
                image.imageHeight = cachedImage.height;
                return cachedImage;
            }

            try {
                texture.load(Minecraft.getInstance().getResourceManager());
                CachedImage cachedImage = new CachedImage(texture.getId(), image.imageWidth, image.imageHeight, true);
                CACHE.put(url, cachedImage);
                return cachedImage;
            } catch (IOException e) {
                return new CachedImage(MissingTextureAtlasSprite.getTexture().getId(), 0, 0, true);
            }
        }
    }

    private static class DynamicTexture extends AbstractTexture {
        private final InputStream in;
        private final BufferedImage bufferedImage;

        private DynamicTexture(InputStream in) throws IOException {
            byte[] bytes = in.readAllBytes();
            this.in = new ByteArrayInputStream(bytes);

            ByteArrayInputStream imageIn = new ByteArrayInputStream(bytes);

            this.bufferedImage = ImageIO.read(imageIn);
            TextureUtil.prepareImage(getId(), bufferedImage.getWidth(), bufferedImage.getHeight());
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) throws IOException {
            NativeImage nativeImage = NativeImage.read(in);
            this.upload(nativeImage);
        }

        private void upload(NativeImage nativeImage) {
            nativeImage.upload(0, 0, 0, mipmap);
        }

        public BufferedImage getBufferedImage() {
            return bufferedImage;
        }
    }

    private static class ImageCache extends LinkedHashMap<String, CachedImage> {
        private final int CAPACITY;

        private ImageCache(final int capacity) {
            super(capacity, 1f, true);
            this.CAPACITY = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CachedImage> eldest) {
            if (size() > CAPACITY) {
                eldest.getValue().delete = true;
                return true;
            }
            return false;
        }
    }

    public static class CachedImage {
        private final int textureId;
        private final int width;
        private final int height;
        private final boolean dynamic;
        private boolean delete = false;

        private CachedImage(int textureId, int width, int height, boolean dynamic) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.dynamic = dynamic;
        }

        public int getTextureId() {
            return textureId;
        }

        public void restore() {
            delete = false;
        }

        public void delete() {
            delete = true;
        }

        public boolean isDynamic() {
            return dynamic;
        }

        public boolean isPendingDeletion() {
            return delete;
        }
    }
}
