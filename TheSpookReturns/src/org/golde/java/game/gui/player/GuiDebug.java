package org.golde.java.game.gui.player;

import org.golde.java.game.Main;
import org.golde.java.game.gui.base.Gui;
import org.golde.java.game.gui.base.GuiText;
import org.golde.java.game.helpers.BetterKeyboard;
import org.golde.java.game.objects.base.item.Item;
import org.golde.java.game.objects.player.EntityPlayer;
import org.golde.java.game.renderEngine.DisplayManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class GuiDebug extends Gui{

	EntityPlayer player;
	private int renderTicks = 0;
	
	boolean isDebugEnabled = false;
	boolean isHitboxesEnabled = false;
	private final int TEXT_COUNT = 7;
	
	public GuiDebug(EntityPlayer player) {
		this.player = player;
		float value = -0.03f;
		for(int i = 0; i < TEXT_COUNT; i++) {
			addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		}
		/*addText(new GuiText("", 1,  new Vector2f(0, value), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));
		addText(new GuiText("", 1, new Vector2f(0, value += 0.03f), 1, false));*/
		
		setVisible(false);
		Main.getRenderer().setRenderColliders(false);
		
	}
	
	
	@Override
	public void onRender() {
		getTextsToBeRendered().get(0).setTextString("X: " + player.getPosition().x + " Y:" + player.getPosition().y + " Z: " + player.getPosition().z);
		getTextsToBeRendered().get(1).setTextString("RX: " + player.getRotX() + " RY: " + player.getRotY() + " RZ: " + player.getRotZ());
		getTextsToBeRendered().get(2).setTextString("Is Moving: " + player.isMoving());
		
		getTextsToBeRendered().get(3).setTextString("Inventory: " + toStringInventory());
		getTextsToBeRendered().get(5).setTextString("FadeValue: " + player.getFader().getBrightness());
		getTextsToBeRendered().get(6).setTextString("Terrain: " + player.getTerrainCurrentlyStandingOn().toString().replace("org.golde.java.game.terrains.", ""));
		
	}
	
	@Override
	public void onTick() {
		
		if(BetterKeyboard.wasKeyPressed(Keyboard.KEY_F3)) {
			isDebugEnabled = !isDebugEnabled;
			setVisible(isDebugEnabled);
			
		}
		
		if(BetterKeyboard.wasKeyPressed(Keyboard.KEY_F2)) {
			isHitboxesEnabled = !isHitboxesEnabled;
			Main.getRenderer().setRenderColliders(isHitboxesEnabled);
		}
		
		if(renderTicks > 30) {
			everySoOften();
			renderTicks = 0;
		}
		renderTicks++;
		
	}
	
	private void everySoOften() {
		getTextsToBeRendered().get(4).setTextString("FPS: " + Math.round(1 / DisplayManager.getFrameTimeSeconds()));
	}
	
	private String toStringInventory() {
		String s = "{";
		for(Item i: player.getInventory()) {
			if(i == null) {
				s = s + "null, ";
			}else {
				s = s + i.toString() + ", ";
			}
			
		}
		return s + "}";
	}
	
	@Override
	public int getZIndex() {
		return 4;
	}
	
}
