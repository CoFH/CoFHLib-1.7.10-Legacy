package cofh.lib.transport;

import cofh.api.transport.IEnderAttuned;
import cofh.api.transport.IEnderDestination;
import cofh.api.transport.IEnderEnergyHandler;
import cofh.api.transport.IEnderFluidHandler;
import cofh.api.transport.IEnderItemHandler;
import cofh.lib.util.ArrayHashList;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class EnderRegistry {

	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>> inputItem;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>> inputFluid;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>> inputEnergy;

	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>> outputItem;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>> outputFluid;
	private HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>> outputEnergy;

	private HashMap<String, TIntObjectHashMap<EnderDestination>> outputTeleport;

	private Configuration linkConf;

	public EnderRegistry(Configuration config) {

		inputItem = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>>();
		inputFluid = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>>();
		inputEnergy = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>>();

		outputItem = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderItemHandler>>>();
		outputFluid = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderFluidHandler>>>();
		outputEnergy = new HashMap<String, TIntObjectHashMap<ArrayHashList<IEnderEnergyHandler>>>();

		outputTeleport = new HashMap<String, TIntObjectHashMap<EnderDestination>>();

		linkConf = config;
		load();
	}

	private void load() {

		for (String channel : linkConf.getCategoryNames()) {
			ConfigCategory category = linkConf.getCategory(channel);
			TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
			if (map == null) {
				outputTeleport.put(channel, map = new TIntObjectHashMap<EnderDestination>());
			}
			for (Property prop : category.values()) try {
				int freq = Integer.parseInt(prop.getName());
				String[] data = prop.getString().split("@");
				if (data.length != 2)
					continue;
				int dimension = Integer.parseInt(data[0]);
				data = data[1].split(",");
				if (data.length != 3)
					continue;
				int x, y, z;
				x = Integer.parseInt(data[0]);
				y = Integer.parseInt(data[1]);
				z = Integer.parseInt(data[2]);
				EnderDestination dest = new EnderDestination(x, y, z, dimension);
				map.put(freq, dest);
			} catch (Throwable p) {}
		}
	}

	private <T> T getElement(HashMap<String, TIntObjectHashMap<T>> map, IEnderAttuned theAttuned) {

		TIntObjectHashMap<T> list = map.get(theAttuned.getChannelString());
		if (list != null)
			return list.get(theAttuned.getFrequency());
		return null;
	}

	public List<IEnderItemHandler> getLinkedItemInputs(IEnderItemHandler theAttuned) {

		return getElement(inputItem, theAttuned);
	}

	public List<IEnderItemHandler> getLinkedItemOutputs(IEnderItemHandler theAttuned) {

		return getElement(outputItem, theAttuned);
	}

	public List<IEnderFluidHandler> getLinkedFluidInputs(IEnderFluidHandler theAttuned) {

		return getElement(inputFluid, theAttuned);
	}

	public List<IEnderFluidHandler> getLinkedFluidOutputs(IEnderFluidHandler theAttuned) {

		return getElement(outputFluid, theAttuned);
	}

	public List<IEnderEnergyHandler> getLinkedEnergyInputs(IEnderEnergyHandler theAttuned) {

		return getElement(inputEnergy, theAttuned);
	}

	public List<IEnderEnergyHandler> getLinkedEnergyOutputs(IEnderEnergyHandler theAttuned) {

		return getElement(outputEnergy, theAttuned);
	}

	public boolean hasDestination(IEnderDestination theAttuned) {

		return hasDestination(theAttuned, true);
	}

	public boolean hasDestination(IEnderDestination theAttuned, boolean to) {

		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(theAttuned.getChannelString());
		if (map == null) {
			return false;
		}
		EnderDestination dest = map.get(to ? theAttuned.getDestination() : theAttuned.getFrequency());
		return dest == null ? false : dest.hasOutput();
	}

	public IEnderDestination getDestination(IEnderDestination theAttuned) {

		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(theAttuned.getChannelString());
		if (map == null) {
			return null;
		}
		EnderDestination dest = map.get(theAttuned.getDestination());
		return dest == null ? null : dest.getOutput();
	}

	/* HELPER FUNCTIONS */
	private <T extends IEnderAttuned> boolean addHandler(HashMap<String, TIntObjectHashMap<ArrayHashList<T>>> map, T theAttuned) {

		String channel = theAttuned.getChannelString();
		TIntObjectHashMap<ArrayHashList<T>> list = map.get(channel);
		if (list == null) {
			map.put(channel, list = new TIntObjectHashMap<ArrayHashList<T>>());
		}
		int freq = theAttuned.getFrequency();
		ArrayHashList<T> array = list.get(freq);
		if (array == null) {
			list.put(freq, array = new ArrayHashList<T>());
		}
		return array.add(theAttuned);
	}

	public void addItemHandler(IEnderItemHandler theAttuned) {

		if (theAttuned.canSendItems()) {
			addHandler(inputItem, theAttuned);
		}
		if (theAttuned.canReceiveItems()) {
			addHandler(outputItem, theAttuned);
		}
	}

	public void addFluidHandler(IEnderFluidHandler theAttuned) {

		if (theAttuned.canSendFluid()) {
			addHandler(inputFluid, theAttuned);
		}
		if (theAttuned.canReceiveFluid()) {
			addHandler(outputFluid, theAttuned);
		}
	}

	public void addEnergyHandler(IEnderEnergyHandler theAttuned) {

		if (theAttuned.canSendEnergy()) {
			addHandler(inputEnergy, theAttuned);
		}
		if (theAttuned.canReceiveEnergy()) {
			addHandler(outputEnergy, theAttuned);
		}
	}

	public void addDestination(IEnderDestination theAttuned) {

		if (!hasDestination(theAttuned, false)) {
			String channel = theAttuned.getChannelString();
			TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
			if (map == null) {
				outputTeleport.put(channel, map = new TIntObjectHashMap<EnderDestination>());
			}
			int freq = theAttuned.getFrequency();
			EnderDestination dest = new EnderDestination(theAttuned);
			map.put(freq, dest);
			linkConf.get(channel, String.valueOf(freq), "").set(dest.toString());
		}
	}

	private <T> boolean removeHandler(HashMap<String, TIntObjectHashMap<ArrayHashList<T>>> map, IEnderAttuned theAttuned) {

		TIntObjectHashMap<ArrayHashList<T>> list = map.get(theAttuned.getChannelString());
		if (list == null) {
			return false;
		}
		ArrayHashList<T> array = list.get(theAttuned.getFrequency());
		if (array == null) {
			return false;
		}
		return array.remove(theAttuned);
	}

	public void removeItemHandler(IEnderItemHandler theAttuned) {

		removeHandler(inputItem, theAttuned);
		removeHandler(outputItem, theAttuned);
	}

	public void removeFluidHandler(IEnderFluidHandler theAttuned) {

		removeHandler(inputFluid, theAttuned);
		removeHandler(outputFluid, theAttuned);
	}

	public void removeEnergyHandler(IEnderEnergyHandler theAttuned) {

		removeHandler(inputEnergy, theAttuned);
		removeHandler(outputEnergy, theAttuned);
	}

	public void removeDestination(IEnderDestination theAttuned) {

		String channel = theAttuned.getChannelString();
		TIntObjectHashMap<EnderDestination> map = outputTeleport.get(channel);
		if (map == null) {
			return;
		}
		EnderDestination dest = map.get(theAttuned.getFrequency());
		if (dest == null) {
			return;
		}
		if (dest.dimension == theAttuned.dimension()) {
			if (dest.x == theAttuned.x() && dest.y == theAttuned.x() && dest.z == theAttuned.x()) {
				int freq = theAttuned.getFrequency();
				map.remove(freq);
				linkConf.getCategory(channel).remove(String.valueOf(freq));
			}
		}
	}

	public void add(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			addItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			addFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			addEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderDestination) {
			addDestination((IEnderDestination) theAttuned);
		}
	}

	public void remove(IEnderAttuned theAttuned) {

		if (theAttuned instanceof IEnderItemHandler) {
			removeItemHandler((IEnderItemHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderFluidHandler) {
			removeFluidHandler((IEnderFluidHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderEnergyHandler) {
			removeEnergyHandler((IEnderEnergyHandler) theAttuned);
		}
		if (theAttuned instanceof IEnderDestination) {
			removeDestination((IEnderDestination) theAttuned);
		}
	}

	/*
	public void sortClientNames() {

		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(clientFrequencyNames.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {

			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {

				int int1 = Integer.valueOf(clientFrequencyNamesReversed.get(o1.getValue()));
				int int2 = Integer.valueOf(clientFrequencyNamesReversed.get(o2.getValue()));
				return int1 > int2 ? 1 : int1 == int2 ? 0 : -1;
			}
		});
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		clientFrequencyNames = result;
	}

	public void clearClientNames() {

		clientFrequencyNames.clear();
		clientFrequencyNamesReversed.clear();
	}

	public void addClientNames(String owner, String name) {

		if (!owner.isEmpty()) {
			clientFrequencyNames.put(owner, name);
			clientFrequencyNamesReversed.put(name, owner);
		}
	}//*/

	private static class EnderDestination {

		private final int dimension;
		private final int x, y, z;
		private IEnderDestination output;
		private boolean isInvalid;

		public EnderDestination(IEnderDestination output) {

			x = output.x();
			y = output.y();
			z = output.z();
			dimension = output.dimension();
			this.output = output;
		}

		private EnderDestination(int x, int y, int z, int dimension) {

			this.x = x;
			this.y = y;
			this.z = z;
			this.dimension = dimension;
		}

		public boolean hasOutput() {

			return !isInvalid && DimensionManager.isDimensionRegistered(dimension);
		}

		public IEnderDestination getOutput() {

			if (output == null || output.isNotValid()) {
				output = null;
				if (!DimensionManager.isDimensionRegistered(dimension)) {
					return null;
				}
				WorldServer world = DimensionManager.getWorld(dimension);
				if (world == null) {
					DimensionManager.initDimension(dimension);
					world = DimensionManager.getWorld(dimension);
				}
				if (world.blockExists(x, y, z)) {
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof IEnderDestination) {
						output = (IEnderDestination) te;
					} else {
						isInvalid = true;
					}
				}
			}
			return output;
		}

		@Override
		public String toString() {

			return String.format("%s@%s,%s,%s", dimension, x, y, z);
		}

		@Override
		public int hashCode() {

			return dimension ^ (y + x * (z * 100));
		}

		@Override
		public boolean equals(Object o) {

			if (o == null || o.getClass() != EnderDestination.class)
				return false;
			EnderDestination other = (EnderDestination) o;
			return other.x == x && other.y == y && other.z == z && other.dimension == dimension;
		}

	}

}
