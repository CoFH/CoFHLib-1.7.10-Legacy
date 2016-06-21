package cofh.lib.gui.element.tab;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.StringHelper;

import net.minecraft.util.ResourceLocation;

public class TabInfo extends TabScrolledText {

	public static boolean enable = true;
	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0xffffff;
	public static int defaultBackgroundColor = 0x555555;

	public static final ResourceLocation TAB_ICON = new ResourceLocation(GuiProps.PATH_ICONS + "icon_information.png");

	public TabInfo(GuiBase gui, String infoString) {

		this(gui, defaultSide, infoString);
	}

	public TabInfo(GuiBase gui, int side, String infoString) {

		super(gui, side, infoString);
		setVisible(enable);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;
	}

	@Override
	public ResourceLocation getTabIcon() {

		return TAB_ICON;
	}

	@Override
	public String getTitle() {

		return StringHelper.localize("info.cofh.information");
	}

}
