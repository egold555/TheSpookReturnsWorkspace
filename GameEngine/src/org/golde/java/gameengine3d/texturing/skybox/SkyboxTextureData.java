package org.golde.java.gameengine3d.texturing.skybox;

import java.nio.ByteBuffer;

public class SkyboxTextureData {

	private int width;
    private int height;
    private ByteBuffer buffer;
     
    public SkyboxTextureData(ByteBuffer buffer, int width, int height){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }
     
    public int getWidth(){
        return width;
    }
     
    public int getHeight(){
        return height;
    }
     
    public ByteBuffer getBuffer(){
        return buffer;
    }
	
}
