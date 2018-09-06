package org.golde.java.game.gui.player;

import org.golde.java.game.GLog;
import org.golde.java.game.gui.base.Gui;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.textures.gui.GuiStaticTexture;
import org.lwjgl.util.vector.Vector2f;

public class GuiFade extends Gui {

	private float brightness = 1;
	private boolean out;
	public static final float SPEED_DEFAULT = 0.01f;
	private float speed = SPEED_DEFAULT;
	private boolean started = false;
	
	public GuiFade(Loader loader) {
		GuiStaticTexture black = new GuiStaticTexture(loader, "black", new Vector2f(0, 0), new Vector2f(1, 1));
		addGuiTexture(black);
		
		setVisible(false);
	}
	
	@Override
	public void onTick() {
		
		if(!started) {return;}
		
		if(brightness > 1) {
			brightness = 1;
			stop();
			return;
		}
		
		if(brightness < 0) {
			brightness = 0;
			stop();
			return;
		}
		
		if(out) {
			brightness += speed;
		} 
		else {
			brightness -= speed;
		}
		GLog.info("Brightness: " + brightness);
		super.onTick();
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void resetSpeed() {
		this.speed = SPEED_DEFAULT;
	}
	
	public void fadeFromBlack() {
		brightness = 1;
		setVisible(true);
		started = true;
		out = false;
	}
	
	public void fadeToBlack() {
		brightness = 0;
		setVisible(true);
		started = true;
		out = true;
	}
	
	public void stop() {
		started = false;
		setVisible(out);
	}
	
	public boolean isFinished() {
		return !started;
	}
	
	@Override
	public float getBrightness() {
		return brightness;
	}
	
	@Override
	public int getZIndex() {
		return 5;
	}
	
}
