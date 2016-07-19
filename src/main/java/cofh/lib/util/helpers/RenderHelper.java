package cofh.lib.util.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

/**
 * Contains various helper functions to assist with rendering.
 *
 * @author King Lemming
 *
 */
public final class RenderHelper {

	public static final double RENDER_OFFSET = 1.0D / 1024.0D;
	public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
	public static final ResourceLocation MC_ITEM_SHEET = new ResourceLocation("textures/atlas/items.png");
	public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
	public static final ResourceLocation MC_FONT_ALTERNATE = new ResourceLocation("textures/font/ascii_sga.png");
	public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private RenderHelper() {

	}

	public static final TextureManager engine() {

		return Minecraft.getMinecraft().renderEngine;
	}

	public static final Tessellator tessellator() {

		return Tessellator.getInstance();
	}

	public static void setColor3ub(int color) {

		GlStateManager.color((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	}

	public static void setColor4ub(int color) {

        GlStateManager.color((byte) (color >> 24 & 0xFF), (byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
	}

	public static void resetColor() {

        GlStateManager.color(1F, 1F, 1F, 1F);
	}

}
