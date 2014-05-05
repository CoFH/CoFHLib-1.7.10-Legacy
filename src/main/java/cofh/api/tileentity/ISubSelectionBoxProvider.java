package cofh.api.tileentity;

import java.util.List;

import codechicken.lib.raytracer.IndexedCuboid6;

public interface ISubSelectionBoxProvider {

	public void addTraceableCuboids(List<IndexedCuboid6> cuboids);

}
