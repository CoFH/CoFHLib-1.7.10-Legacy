package cofh.lib.gui;

import net.minecraft.util.ResourceLocation;

public class GuiProps {

	/* GUI */
	public static final String PATH_GFX = "cofh:textures/";
	public static final String PATH_ARMOR = PATH_GFX + "armor/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_RENDER = PATH_GFX + "blocks/";
	public static final String PATH_ELEMENTS = PATH_GUI + "elements/";
	public static final String PATH_ICONS = PATH_GUI + "icons/";

	/* ICONS */
	public static final ResourceLocation ICON_ARROW_UP_ACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_up.png");
	public static final ResourceLocation ICON_ARROW_UP_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_up_inactive.png");
	public static final ResourceLocation ICON_ARROW_DOWN_ACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_down.png");
	public static final ResourceLocation ICON_ARROW_DOWN_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_down_inactive.png");

	public static final ResourceLocation ICON_BUTTON = new ResourceLocation(PATH_ICONS + "icon_button.png");
	public static final ResourceLocation ICON_BUTTON_HIGHLIGHT = new ResourceLocation(PATH_ICONS + "icon_button_highlight.png");
	public static final ResourceLocation ICON_BUTTON_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_button_inactive.png");

	public static final ResourceLocation ICON_RS_CONTROL_DISABLED = new ResourceLocation(PATH_ICONS + "icon_rs_control_disabled.png");
	public static final ResourceLocation ICON_RS_CONTROL_LOW = new ResourceLocation(PATH_ICONS + "icon_rs_control_low.png");
	public static final ResourceLocation ICON_RS_CONTROL_HIGH = new ResourceLocation(PATH_ICONS + "icon_rs_control_high.png");

}
