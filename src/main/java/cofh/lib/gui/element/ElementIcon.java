package cofh.lib.gui.element;

import codechicken.lib.texture.TextureUtils;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;

public class ElementIcon extends ElementBase {

    protected TextureAtlasSprite icon;
    protected GuiColor color = new GuiColor(-1);

    public ElementIcon(GuiBase gui, int posX, int posY, TextureAtlasSprite icon) {

        super(gui, posX, posY);
        this.icon = icon;
    }

    public ElementIcon setColor(Number color) {

        this.color = new GuiColor(color.intValue());
        return this;
    }

    public ElementIcon setIcon(TextureAtlasSprite icon) {

        this.icon = icon;
        return this;
    }

    public int getColor() {

        return color.getColor();
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks) {

        if (icon != null) {
            TextureUtils.bindBlockTexture();
            GlStateManager.color(color.getFloatR(), color.getFloatG(), color.getFloatB(), color.getFloatA());
            gui.drawColorIcon(icon, posX, posY);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {

        return;
    }

}
