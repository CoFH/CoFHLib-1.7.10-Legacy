package cofh.api.transport;

public interface IEnderAttuned {

	public enum EnderTypes {
		ITEM, FLUID, REDSTONE_FLUX
	}

	String getOwnerString();

	int getFrequency();

	boolean setFrequency(int frequency);

	boolean clearFrequency();

}
