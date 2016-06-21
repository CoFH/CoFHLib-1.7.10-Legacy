package cofh.lib.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public class CompressedStreamUtils {

	private CompressedStreamUtils() {

	}

	public static NBTTagCompound read(byte[] bytes, NBTSizeTracker tracker) throws IOException {

		DataInputStream input = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
		NBTTagCompound tag;

		try {
			tag = CompressedStreamTools.read(input, tracker);
		} finally {
			input.close();
		}
		return tag;
	}

	public static byte[] compress(NBTTagCompound tag) throws IOException {

		ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
		DataOutputStream dataOStream = new DataOutputStream(new GZIPOutputStream(byteOStream));
		try {
			CompressedStreamTools.write(tag, dataOStream);
			dataOStream.close();
		} finally {
			dataOStream.close();
		}
		return byteOStream.toByteArray();
	}

}
