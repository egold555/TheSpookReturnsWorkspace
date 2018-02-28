package org.golde.java.gameengine3d.audio;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.golde.java.gameengine3d.helpers.GELog;
import org.golde.java.gameengine3d.objs.Camera;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

/**
 * A class to load and manage audio files for the sources.
 * @author Eric
 *
 */
public class AudioMaster {

	private List<Integer> buffers = new ArrayList<Integer>();
	public List<Speaker> sources = new ArrayList<Speaker>();
	
	public void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			GELog.error(e, "Failed to create AudioMaster");
		}
	}
	
	/*
	 * Positional audio stuff.
	 * Called every frame
	 */
	public void setListenerData(Camera camera) {
		Vector3f cameraPos = camera.getPosition();
		AL10.alListener3f(AL10.AL_POSITION, cameraPos.x, cameraPos.y, cameraPos.z);
		
		ByteBuffer bb = ByteBuffer.allocateDirect(6 * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer orientation = bb.asFloatBuffer();
		
		// These seem reversed from what I would expect!
		float lookAtX = -(float) Math.sin(Math.toRadians(camera.getYaw()));
		float lookAtZ = (float) Math.cos(Math.toRadians(camera.getYaw()));
		orientation.put(0, lookAtX);
		orientation.put(1, 0);
		orientation.put(2, lookAtZ);
		orientation.put(3, 0);
		orientation.put(4, 1);
		orientation.put(5, 0);
		
		AL10.alListener(AL10.AL_ORIENTATION, orientation);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	@SuppressWarnings("deprecation")
	public int loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		File f = new File("res/audio/" + file + ".wav");
		if(!f.exists()) {
			GELog.error("res/audio/" + file + ".wav does not exist!");
			return -1;
		}
		
		WaveData waveFile = null;
		try {
			waveFile = WaveData.create(f.toURL());
		} catch (MalformedURLException e) {
			GELog.error(e, "Failed to load sound file: " + file);
		}
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	public void cleanUp() {
		for(int buffer:buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}
	
}
