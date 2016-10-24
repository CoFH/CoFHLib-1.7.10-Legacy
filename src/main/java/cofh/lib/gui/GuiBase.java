package cofh.lib.gui;

import cofh.lib.audio.SoundBase;
import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.tab.TabBase;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Base class for a modular GUIs. Works with Elements {@link ElementBase} and Tabs {@link TabBase} which are both modular elements.
 *
 * @author King Lemming
 */
public abstract class GuiBase extends GuiContainer {

	public static final SoundHandler guiSoundManager = FMLClientHandler.instance().getClient().getSoundHandler();

	protected boolean drawTitle = true;
	protected boolean drawInventory = true;
	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int lastIndex = -1;

	protected String name;
	protected ResourceLocation texture;

	public ArrayList<TabBase> tabs = new ArrayList<TabBase>();
	protected ArrayList<ElementBase> elements = new ArrayList<ElementBase>();

	protected List<String> tooltip = new LinkedList<String>();
	protected boolean tooltips = true;

	public static void playSound(SoundEvent sound, float pitch) {

		//TODO figure out if sound volume needs to be adjusted (was 1.0f everywhere, but getMasterRecord uses 0.25f, use constructor in that case)
		guiSoundManager.playSound(PositionedSoundRecord.getMasterRecord(sound, pitch));
	}

	public GuiBase(Container container) {

		super(container);
	}

	public GuiBase(Container container, ResourceLocation texture) {

		super(container);
		this.texture = texture;
	}

	@Override
	public void initGui() {

		super.initGui();
		tabs.clear();
		elements.clear();
	}

	@Override
	public void drawScreen(int x, int y, float partialTick) {

		updateElementInformation();

		super.drawScreen(x, y, partialTick);

		if (tooltips && mc.thePlayer.inventory.getItemStack() == null) {
			addTooltips(tooltip);
			drawTooltip(tooltip);
		}
		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		updateElements();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (drawTitle & name != null) {
			fontRendererObj.drawString(StringHelper.localize(name), getCenteredOffset(StringHelper.localize(name)), 6, 0x404040);
		}
		if (drawInventory) {
			fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 3, 0x404040);
		}
		drawElements(0, true);
		drawTabs(0, true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		drawElements(partialTick, false);
		drawTabs(partialTick, false);
		GL11.glPopMatrix();
	}

	@Override
	protected void keyTyped(char characterTyped, int keyPressed) throws IOException {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled()) {
				continue;
			}
			if (c.onKeyTyped(characterTyped, keyPressed)) {
				return;
			}
		}
		super.keyTyped(characterTyped, keyPressed);
	}

	@Override
	public void handleMouseInput() throws IOException {

		int x = Mouse.getEventX() * width / mc.displayWidth;
		int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = elements.size(); i-- > 0;) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
					continue;
				}
				if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
					return;
				}
			}
			TabBase tab = getTabAtPosition(mouseX, mouseY);

			if (tab != null && tab.onMouseWheel(mouseX, mouseY, wheelMovement)) {
				return;
			}

			if (onMouseWheel(mouseX, mouseY, wheelMovement)) {
				return;
			}
		}
		super.handleMouseInput();
	}

	protected boolean onMouseWheel(int mouseX, int mouseY, int wheelMovement) {

		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		mouseX -= guiLeft;
		mouseY -= guiTop;

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
				continue;
			}
			if (c.onMousePressed(mouseX, mouseY, mouseButton)) {
				return;
			}
		}

		TabBase tab = getTabAtPosition(mouseX, mouseY);
		if (tab != null) {
			int tmouseX = mouseX;

			if (!tab.onMousePressed(tmouseX, mouseY, mouseButton)) {
				for (int i = tabs.size(); i-- > 0;) {
					TabBase other = tabs.get(i);
					if (other != tab && other.open && other.side == tab.side) {
						other.toggleOpen();
					}
				}
				tab.toggleOpen();
				return;
			}
		}

		mouseX += guiLeft;
		mouseY += guiTop;

		if (tab != null) {
			switch (tab.side) {
			case TabBase.LEFT:
				// guiLeft -= tab.currentWidth;
				break;
			case TabBase.RIGHT:
				xSize += tab.currentWidth;
				break;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (tab != null) {
			switch (tab.side) {
			case TabBase.LEFT:
				// guiLeft += tab.currentWidth;
				break;
			case TabBase.RIGHT:
				xSize -= tab.currentWidth;
				break;
			}
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int lastClick, long timeSinceClick) {

		Slot slot = getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = mc.thePlayer.inventory.getItemStack();

		if (dragSplitting && slot != null && itemstack != null && slot instanceof SlotFalseCopy) {
			if (lastIndex != slot.slotNumber) {
				lastIndex = slot.slotNumber;
				handleMouseClick(slot, slot.slotNumber, 0, ClickType.PICKUP);
			}
		} else {
			lastIndex = -1;
			super.mouseClickMove(mouseX, mouseY, lastClick, timeSinceClick);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {

		mouseX -= guiLeft;
		mouseY -= guiTop;

		if (state >= 0 && state <= 2) { // 0:left, 1:right, 2:middle
			for (int i = elements.size(); i-- > 0;) {
				ElementBase clicked = elements.get(i);
				if (!clicked.isVisible() || !clicked.isEnabled()) {
					continue;
				}
				clicked.onMouseReleased(mouseX, mouseY, state);
			}
		}
		mouseX += guiLeft;
		mouseY += guiTop;

		super.mouseReleased(mouseX, mouseY, state);
	}

	public Slot getSlotAtPosition(int xCoord, int yCoord) {

		for (int k = 0; k < inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = inventorySlots.inventorySlots.get(k);

			if (isMouseOverSlot(slot, xCoord, yCoord)) {
				return slot;
			}
		}
		return null;
	}

	public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {

		return isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
	}

	/**
	 * Draws the elements for this GUI.
	 */
	protected void drawElements(float partialTick, boolean foreground) {

		if (foreground) {
			for (int i = 0; i < elements.size(); i++) {
				ElementBase element = elements.get(i);
				if (element.isVisible()) {
					element.drawForeground(mouseX, mouseY);
				}
			}
		} else {
			for (int i = 0; i < elements.size(); i++) {
				ElementBase element = elements.get(i);
				if (element.isVisible()) {
					element.drawBackground(mouseX, mouseY, partialTick);
				}
			}
		}
	}

	/**
	 * Draws the tabs for this GUI. Handles Tab open/close animation.
	 */
	protected void drawTabs(float partialTick, boolean foreground) {

		int yPosRight = 4;
		int yPosLeft = 4;

		if (foreground) {
			for (int i = 0; i < tabs.size(); i++) {
				TabBase tab = tabs.get(i);
				tab.update();
				if (!tab.isVisible()) {
					continue;
				}
				if (tab.side == TabBase.LEFT) {
					tab.drawForeground(mouseX, mouseY);
					yPosLeft += tab.currentHeight;
				} else {
					tab.drawForeground(mouseX, mouseY);
					yPosRight += tab.currentHeight;
				}
			}
		} else {
			for (int i = 0; i < tabs.size(); i++) {
				TabBase tab = tabs.get(i);
				tab.update();
				if (!tab.isVisible()) {
					continue;
				}
				if (tab.side == TabBase.LEFT) {
					tab.setPosition(0, yPosLeft);
					tab.drawBackground(mouseX, mouseY, partialTick);
					yPosLeft += tab.currentHeight;
				} else {
					tab.setPosition(xSize, yPosRight);
					tab.drawBackground(mouseX, mouseY, partialTick);
					yPosRight += tab.currentHeight;
				}
			}
		}
	}

	/**
	 * Called by NEI if installed
	 */
	// @Override
	public List<String> handleTooltip(int mousex, int mousey, List<String> tooltip) {

		if (mc.thePlayer.inventory.getItemStack() == null) {
			addTooltips(tooltip);
		}
		return tooltip;
	}

	public void addTooltips(List<String> tooltip) {

		TabBase tab = getTabAtPosition(mouseX, mouseY);

		if (tab != null) {
			tab.addTooltip(tooltip);
		}
		ElementBase element = getElementAtPosition(mouseX, mouseY);

		if (element != null && element.isVisible()) {
			element.addTooltip(tooltip);
		}
	}

	/* ELEMENTS */
	public ElementBase addElement(ElementBase element) {

		elements.add(element);
		return element;
	}

	public TabBase addTab(TabBase tab) {

		int yOffset = 4;
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).side == tab.side && tabs.get(i).isVisible()) {
				yOffset += tabs.get(i).currentHeight;
			}
		}
		tab.setPosition(tab.side == TabBase.LEFT ? 0 : xSize, yOffset);
		tabs.add(tab);

		if (TabTracker.getOpenedLeftTab() != null && tab.getClass().equals(TabTracker.getOpenedLeftTab())) {
			tab.setFullyOpen();
		} else if (TabTracker.getOpenedRightTab() != null && tab.getClass().equals(TabTracker.getOpenedRightTab())) {
			tab.setFullyOpen();
		}
		return tab;
	}

	protected ElementBase getElementAtPosition(int mouseX, int mouseY) {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase element = elements.get(i);
			if (element.intersectsWith(mouseX, mouseY)) {
				return element;
			}
		}
		return null;
	}

	protected TabBase getTabAtPosition(int mouseX, int mouseY) {

		int xShift = 0;
		int yShift = 4;

		for (int i = 0; i < tabs.size(); i++) {
			TabBase tab = tabs.get(i);
			if (!tab.isVisible() || tab.side == TabBase.RIGHT) {
				continue;
			}
			tab.setCurrentShift(xShift, yShift);
			if (tab.intersectsWith(mouseX, mouseY, xShift, yShift)) {
				return tab;
			}
			yShift += tab.currentHeight;
		}

		xShift = xSize;
		yShift = 4;

		for (int i = 0; i < tabs.size(); i++) {
			TabBase tab = tabs.get(i);
			if (!tab.isVisible() || tab.side == TabBase.LEFT) {
				continue;
			}
			tab.setCurrentShift(xShift, yShift);
			if (tab.intersectsWith(mouseX, mouseY, xShift, yShift)) {
				return tab;
			}
			yShift += tab.currentHeight;
		}
		return null;
	}

	protected final void updateElements() {

		for (int i = elements.size(); i-- > 0;) {
			ElementBase c = elements.get(i);
			if (c.isVisible() && c.isEnabled()) {
				c.update(mouseX, mouseY);
			}
		}
	}

	protected void updateElementInformation() {

	}

	public void handleElementButtonClick(String buttonName, int mouseButton) {

	}

	/* HELPERS */
	public void bindTexture(ResourceLocation texture) {

		mc.renderEngine.bindTexture(texture);
	}

	public void drawIcon(ResourceLocation icon, int x, int y) {

		bindTexture(icon);
		drawSizedTexturedModalRect(x, y, 0, 0, 16, 16, 16, 16);
	}

	public void drawButton(ResourceLocation icon, int x, int y, ButtonMode mode) {

		switch(mode) {
			case ACTIVE:
				drawIcon(GuiProps.ICON_BUTTON, x, y);
				break;
			case HIGHLIGHT:
				drawIcon(GuiProps.ICON_BUTTON_HIGHLIGHT, x, y);
				break;
			case INACTIVE:
				drawIcon(GuiProps.ICON_BUTTON_INACTIVE, x, y);
				break;
		}
		drawIcon(icon, x, y);
	}

	public void drawTextureMapIcon(ResourceLocation icon, int x, int y) {

		TextureMap textureMap = mc.getTextureMapBlocks();
		bindTexture(textureMap.LOCATION_BLOCKS_TEXTURE);

		drawTexturedModalRect(x, y, textureMap.getAtlasSprite(icon.toString()), 16, 16);
	}

	public void drawIcon(TextureAtlasSprite icon, int x, int y) {

		drawScaledTexturedModalRect(x, y, icon, 16, 16);
	}

	public void drawItemStack(ItemStack stack, int x, int y, boolean drawOverlay, String overlayTxt) {

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;

		FontRenderer font = null;
		if (stack != null) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = fontRendererObj;
		}

		itemRender.renderItemAndEffectIntoGUI(stack, x, y);

		if (drawOverlay) {
			itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack == null ? 0 : 8), overlayTxt);
		}
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	/**
	 * Simple method used to draw a fluid of arbitrary size.
	 */
	public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

		if (fluid == null || fluid.getFluid() == null) {
			return;
		}
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.setColor3ub(fluid.getFluid().getColor(fluid));

		drawTiledTexture(x, y, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString()), width, height);
	}

	public void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

		int i = 0;
		int j = 0;

		int drawHeight = 0;
		int drawWidth = 0;

		for (i = 0; i < width; i += 16) {
			for (j = 0; j < height; j += 16) {
				drawWidth = Math.min(width - i, 16);
				drawHeight = Math.min(height - j, 16);
				drawScaledTexturedModalRect(x + i, y + j, icon, drawWidth, drawHeight);
			}
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
	}

	public void drawSizedModalRect(int x1, int y1, int x2, int y2, int color) {

		int temp;

		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r, g, b, a);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos(x1, y2, zLevel).endVertex();
		buffer.pos(x2, y2, zLevel).endVertex();
		buffer.pos(x2, y1, zLevel).endVertex();
		buffer.pos(x1, y1, zLevel).endVertex();
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawSizedRect(int x1, int y1, int x2, int y2, int color) {

		int temp;

		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r, g, b, a);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos(x1, y2, zLevel).endVertex();
		buffer.pos(x2, y2, zLevel).endVertex();
		buffer.pos(x2, y1, zLevel).endVertex();
		buffer.pos(x1, y1, zLevel).endVertex();
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void drawSizedTexturedModalRect(int x, int y, int u, int v, int width, int height, float texW, float texH) {

		float texU = 1 / texW;
		float texV = 1 / texH;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x + 0, y + height, zLevel).tex((u + 0) * texU, (v + height) * texV).endVertex();
		buffer.pos(x + width, y + height, zLevel).tex((u + width) * texU, (v + height) * texV).endVertex();
		buffer.pos(x + width, y + 0, zLevel).tex((u + width) * texU, (v + 0) * texV).endVertex();
		buffer.pos(x + 0, y + 0, zLevel).tex((u + 0) * texU, (v + 0) * texV).endVertex();
		tessellator.draw();
	}

	public void drawScaledTexturedModalRect(int x, int y, TextureAtlasSprite icon, int width, int height) {

		if (icon == null) {
			return;
		}
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x + 0, y + 0, zLevel).tex(minU, minV).endVertex();
		buffer.pos(x + 0, y + height, zLevel).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();;
		buffer.pos(x + width, y + height, zLevel).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();;
		buffer.pos(x + width, y + 0, zLevel).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();;
		tessellator.draw();
	}

	public void drawTooltip(List<String> list) {

		drawTooltipHoveringText(list, mouseX + guiLeft, mouseY + guiTop, fontRendererObj);
		tooltip.clear();
	}

	@SuppressWarnings("rawtypes")
	protected void drawTooltipHoveringText(List list, int x, int y, FontRenderer font) {

		if (list == null || list.isEmpty()) {
			return;
		}
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int k = 0;
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			int l = font.getStringWidth(s);

			if (l > k) {
				k = l;
			}
		}
		int i1 = x + 12;
		int j1 = y - 12;
		int k1 = 8;

		if (list.size() > 1) {
			k1 += 2 + (list.size() - 1) * 10;
		}
		if (i1 + k > width) {
			i1 -= 28 + k;
		}
		if (j1 + k1 + 6 > height) {
			j1 = height - k1 - 6;
		}
		zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int l1 = -267386864;
		drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
		drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
		drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
		drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
		drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
		int i2 = 1347420415;
		int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
		drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
		drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
		drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

		for (int k2 = 0; k2 < list.size(); ++k2) {
			String s1 = (String) list.get(k2);
			font.drawStringWithShadow(s1, i1, j1, -1);

			if (k2 == 0) {
				j1 += 2;
			}
			j1 += 10;
		}
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	/**
	 * Passthrough method for tab use.
	 */
	public void mouseClicked(int mouseButton) throws IOException {

		super.mouseClicked(guiLeft + mouseX, guiTop + mouseY, mouseButton);
	}

	public FontRenderer getFontRenderer() {

		return fontRendererObj;
	}

	protected int getCenteredOffset(String string) {

		return getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {

		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
	}

	public int getGuiLeft() {

		return guiLeft;
	}

	public int getGuiTop() {

		return guiTop;
	}

	public int getMouseX() {

		return mouseX;
	}

	public int getMouseY() {

		return mouseY;
	}

	public void overlayRecipe() {

	}

	public enum ButtonMode {

		ACTIVE,
		HIGHLIGHT,
		INACTIVE
	}

}
