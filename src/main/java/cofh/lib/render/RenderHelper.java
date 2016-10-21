package cofh.lib.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * Contains various helper functions to assist with rendering.
 *
 * @author King Lemming
 */
public final class RenderHelper {

    public static final double RENDER_OFFSET = 1.0D / 1024.0D;
    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
    public static final ResourceLocation MC_FONT_ALTERNATE = new ResourceLocation("textures/font/ascii_sga.png");
    public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private RenderHelper() {

    }

    public static TextureManager engine() {

        return Minecraft.getMinecraft().renderEngine;
    }

    public static Tessellator tessellator() {

        return Tessellator.getInstance();
    }

    public static void setColor3ub(int color) {

        GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
    }

    public static void setColor4ub(int color) {

        GL11.glColor4ub((byte) (color >> 24 & 0xFF), (byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
    }

    public static void resetColor() {

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

	/*public static void renderItemAsBlock(RenderBlocks renderer, ItemStack item, double translateX, double translateY, double translateZ) {

		renderTextureAsBlock(renderer, item.getIconIndex(), translateX, translateY, translateZ);
	}

	public static void renderTextureAsBlock(RenderBlocks renderer, IIcon texture, double translateX, double translateY, double translateZ) {

		Tessellator tessellator = Tessellator.instance;
		Block block = Blocks.stone;

		if (texture == null) {
			return;
		}
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslated(translateX, translateY, translateZ);
		tessellator.startDrawingQuads();

		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);

		tessellator.draw();
	}

	public static void renderBlockFace(RenderBlocks renderer, IIcon texture, int face, double translateX, double translateY, double translateZ) {

		Tessellator tessellator = Tessellator.instance;
		Block block = Blocks.stone;

		if (texture == null || face < 0 || face > 5) {
			return;
		}
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslated(translateX, translateY, translateZ);
		tessellator.startDrawingQuads();

		switch (face) {
		case 0:
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 1:
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 2:
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 3:
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 4:
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		case 5:
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);
			break;
		}
		tessellator.draw();
	}

    public static void renderItemIn2D(IIcon icon) {

        ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
    }*/

    public static void renderIcon(TextureAtlasSprite icon, double z) {
        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 16, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(16, 16, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(16, 0, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(0, 0, z).tex(icon.getMinU(), icon.getMinV());
        Tessellator.getInstance().draw();

    }

    public static void renderIcon(double x, double y, double z, TextureAtlasSprite icon, int width, int height) {

        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(x + width, y + height, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(x + width, y, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(x, y, z).tex(icon.getMinU(), icon.getMinV());
        Tessellator.getInstance().draw();
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid) {
        if (fluid == null) {
            fluid = FluidRegistry.LAVA;
        }
        return getTexture(fluid.getStill());
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {

        if (fluid == null || fluid.getFluid() == null || fluid.getFluid().getStill(fluid) == null) {
            fluid = new FluidStack(FluidRegistry.LAVA, 1);
        }
        return getTexture(fluid.getFluid().getStill(fluid));
    }

    public static void bindTexture(ResourceLocation texture) {
        engine().bindTexture(texture);
    }

    public static void setBlockTextureSheet() {
        bindTexture(MC_BLOCK_SHEET);
    }

    public static void setDefaultFontTextureSheet() {
        bindTexture(MC_FONT_DEFAULT);
    }

    public static TextureAtlasSprite getTexture(String location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location);
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {
        return getTexture(location.toString());
    }

    public static void setSGAFontTextureSheet() {

        bindTexture(MC_FONT_ALTERNATE);
    }

    public static void enableGUIStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
    }

    public static void enableStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

}
