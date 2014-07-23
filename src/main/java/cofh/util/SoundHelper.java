package cofh.util;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

/**
 * Contains various helper functions to assist with Sound manipulation.
 * 
 * @author King Lemming
 * 
 */
public class SoundHelper {

	public static final SoundHandler soundManager = FMLClientHandler.instance().getClient().getSoundHandler();

	public static void playSound(String soundName, float x, float y, float z, float volume, float pitch) {

		soundManager.playSound(new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, x, y, z));
	}

}
