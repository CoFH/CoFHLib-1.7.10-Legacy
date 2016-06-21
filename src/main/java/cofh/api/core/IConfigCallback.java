package cofh.api.core;

/**
 * Interface which can be put on just about anything to allow for callbacks when config options are changed, if set up properly.
 */
public interface IConfigCallback {

	public void configUpdate();

}
