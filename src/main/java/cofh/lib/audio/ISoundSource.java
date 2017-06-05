package cofh.lib.audio;

public interface ISoundSource {

	/**
	 * Should actually return an ISound. The object return prevents server crashes.
	 */
	Object getSound();

	boolean shouldPlaySound();
}
