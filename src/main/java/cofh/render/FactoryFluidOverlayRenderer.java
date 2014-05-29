package cofh.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class FactoryFluidOverlayRenderer implements IItemRenderer {

	private boolean canFlip;

	public FactoryFluidOverlayRenderer()
	{
		this(true);
	}

	public FactoryFluidOverlayRenderer(boolean canFlip)
	{
		this.canFlip = canFlip;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return (type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal());
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return handleRenderType(item, type) & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemstack, Object... data) {
		Item item = itemstack.getItem();
		FluidStack liquid = null;
		if (item instanceof IFluidContainerItem)
		{
			IFluidContainerItem fluidItem = (IFluidContainerItem)item;
			liquid = fluidItem.getFluid(itemstack);
		}
		else if (item instanceof IFluidOverlayItem)
		{
			if (item.getRenderPasses(itemstack.getItemDamage()) == 2)
				liquid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
		}
		doRenderItem(type, itemstack, item, liquid);
	}

	protected void doRenderItem(ItemRenderType type, ItemStack item, Item iconItem, FluidStack liquid) {
		IIcon icon = iconItem.getIcon(item, 0);
		IIcon mask = iconItem.getIcon(item, 1);
		boolean hasLiquid = liquid != null;
		IIcon fluid = hasLiquid ? liquid != null ? liquid.getFluid().getIcon(liquid) : null : mask;
		int liquidSheet = hasLiquid & liquid != null ? liquid.getFluid().getSpriteNumber() : 0;
		int colorMult = hasLiquid & liquid != null ? liquid.getFluid().getColor(liquid) : 0xFFFFFF;
		boolean isFloaty = hasLiquid & liquid != null ? liquid.getFluid().getDensity(liquid) < 0 : false;

		if (fluid == null) {
			fluid = Blocks.flowing_lava.getIcon(2, 0);
			liquidSheet = 0;
			colorMult = 0x3F3F3F;
		}

		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		GL11.glPushMatrix();

		Tessellator tessellator = Tessellator.instance;

		float iconMinX = icon.getMinU();
		float iconMaxX = icon.getMaxU();
		float iconMinY = icon.getMinV();
		float iconMaxY = icon.getMaxV();

		float maskMinX = mask.getMinU();
		float maskMaxX = mask.getMaxU();
		float maskMinY = mask.getMinV();
		float maskMaxY = mask.getMaxV();

		float fluidMinX = fluid.getMinU();
		float fluidMaxX = fluid.getMaxU();
		float fluidMinY = fluid.getMinV();
		float fluidMaxY = fluid.getMaxV();

		if (isFloaty && canFlip) {
			iconMaxY = icon.getMinV();
			iconMinY = icon.getMaxV();

			maskMaxY = mask.getMinV();
			maskMinY = mask.getMaxV();

			fluidMaxY = fluid.getMinV();
			fluidMinY = fluid.getMaxV();
		}

		TextureUtil.func_147950_a(false, false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		if (type == ItemRenderType.INVENTORY) {
			GL11.glDisable(GL11.GL_LIGHTING);

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0,  16, 0, iconMinX, iconMaxY);
			tessellator.addVertexWithUV(16, 16, 0, iconMaxX, iconMaxY);
			tessellator.addVertexWithUV(16,  0, 0, iconMaxX, iconMinY);
			tessellator.addVertexWithUV(0,   0, 0, iconMinX, iconMinY);
			tessellator.draw();

			if (hasLiquid) {
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(0,  16, 0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(16,  0, 0.001, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(0,   0, 0.001, maskMinX, maskMinY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				bindTexture(renderEngine, liquidSheet);
				OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0,  16, 0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(16,  0, 0.001, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(0,   0, 0.001, fluidMinX, fluidMinY);
				tessellator.draw();

				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			if (type == ItemRenderType.ENTITY) {
				GL11.glTranslatef(0.5f, 4 / -16f, 0);
				GL11.glRotatef(180, 0, 1, 0);
			}

			ItemRenderer.renderItemIn2D(tessellator, iconMaxX, iconMinY, iconMinX, iconMaxY, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

			if (hasLiquid) {
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.addVertexWithUV(0, 0,  0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(1, 0,  0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(1, 1,  0.001, maskMinX, maskMinY);
				tessellator.addVertexWithUV(0, 1,  0.001, maskMaxX, maskMinY);
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.addVertexWithUV(0, 1, -0.0635, maskMinX, maskMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, maskMinX, maskMaxY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				bindTexture(renderEngine, liquidSheet);
				OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 0,  0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(1, 0,  0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(1, 1,  0.001, fluidMinX, fluidMinY);
				tessellator.addVertexWithUV(0, 1,  0.001, fluidMaxX, fluidMinY);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 1, -0.0635, fluidMinX, fluidMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, fluidMinX, fluidMaxY);
				tessellator.draw();

				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		TextureUtil.func_147945_b();
		GL11.glPopMatrix();
	}

	protected void bindTexture(TextureManager renderEngine, int spriteNumber)
	{
		if (spriteNumber == 0)
			renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		else
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteNumber);
	}

}
