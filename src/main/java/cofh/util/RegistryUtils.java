package cofh.util;

import com.google.common.collect.BiMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

public class RegistryUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void overwriteEntry(RegistryNamespaced registry, String name, Object object) {

		Object oldThing = registry.getObject(name);
		int id = registry.getIDForObject(oldThing);
		BiMap map = ((BiMap) registry.registryObjects);
		registry.underlyingIntegerMap.func_148746_a(object, id);
		map.remove(name);
		map.forcePut(name, object);
	}

	@SideOnly(Side.CLIENT)
	public static boolean textureExists(ResourceLocation texture) {

		try {
			Minecraft.getMinecraft().getResourceManager().getAllResources(texture);
			return true;
		} catch (Throwable t) { // pokemon!
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public static boolean textureExists(String texture) {

		return textureExists(new ResourceLocation(texture));
	}

	@SideOnly(Side.CLIENT)
	public static boolean blockTextureExists(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/blocks/" + texture;
		}
		return textureExists(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static boolean itemTextureExists(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/items/" + texture;
		}
		return textureExists(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static int getTextureColor(ResourceLocation texture) {

		try {
			BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());

			int[] a = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), a, 0, image.getWidth());

			int r = a[0];
			for (int i = a.length; i --> 1; ) {
				int t = a[i], v;
				v = (((r >> 24) & 255) + ((t >> 24) & 255)) / 2;
				r &= 0x00FFFFFF; r |= v << 24;
				v = (((r >> 16) & 255) + ((t >> 16) & 255)) / 2;
				r &= 0xFF00FFFF; r |= v << 16;
				v = (((r >>  8) & 255) + ((t >>  8) & 255)) / 2;
				r &= 0xFFFF00FF; r |= v <<  8;
				v = (((r >>  0) & 255) + ((t >>  0) & 255)) / 2;
				r &= 0xFFFFFF00; r |= v <<  0;
			}
			return r;
		} catch (Throwable t) { // pokemon!
			return 0xFFFFFF;
		}
	}

	@SideOnly(Side.CLIENT)
	public static int getTextureColor(String texture) {

		return getTextureColor(new ResourceLocation(texture));
	}

	@SideOnly(Side.CLIENT)
	public static int getBlockTextureColor(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/blocks/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/blocks/" + texture;
		}
		return getTextureColor(texture + ".png");
	}

	@SideOnly(Side.CLIENT)
	public static int getItemTextureColor(String texture) {

		int i = texture.indexOf(':');

		if (i > 0) {
			texture = texture.substring(0, i) + ":textures/items/" + texture.substring(i + 1, texture.length());
		} else {
			texture = "textures/items/" + texture;
		}
		return getTextureColor(texture + ".png");
	}

}
