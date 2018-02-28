package org.golde.java.gameengine3d;

import java.util.ArrayList;
import java.util.List;

import org.golde.java.gameengine3d.audio.AudioMaster;
import org.golde.java.gameengine3d.audio.Speaker;
import org.golde.java.gameengine3d.helpers.BlankLogger;
import org.golde.java.gameengine3d.objs.Camera;
import org.golde.java.gameengine3d.objs.Light;
import org.golde.java.gameengine3d.objs.base.entities.Entity;
import org.golde.java.gameengine3d.objs.base.entities.EntityAbstractPlayer;
import org.golde.java.gameengine3d.objs.base.entities.EntityMoveable;
import org.golde.java.gameengine3d.rendering.DisplayManager;
import org.golde.java.gameengine3d.rendering.gui.Gui;
import org.golde.java.gameengine3d.rendering.gui.GuiText;
import org.golde.java.gameengine3d.rendering.gui.font.FontType;
import org.golde.java.gameengine3d.rendering.gui.font.TextMaster;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.golde.java.gameengine3d.rendering.loader.OBJLoader;
import org.golde.java.gameengine3d.rendering.renderers.GuiRenderer;
import org.golde.java.gameengine3d.rendering.renderers.MasterRenderer;
import org.golde.java.gameengine3d.objs.terrain.Terrain;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

public abstract class GameEngine {
	
	private static GameEngine instance;
	private DisplayManager displayManager = new DisplayManager();
	private Loader loader;
	private MasterRenderer masterRenderer;
	private AudioMaster audioMaster = new AudioMaster();
	private Camera camera;
	private GuiRenderer guiRenderer;
	private TextMaster textMaster;
	private FontType font;
	private OBJLoader objLoader;
	
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Gui> guis = new ArrayList<Gui>();
	private List<Light> lights = new ArrayList<Light>();
	
	/**
	 * Called before game engine is setup. 
	 * Used to set game settings for example
	 */
	public void preInit() {}
	
	/**
	 * Used to register entities, terrain etc
	 */
	public void postInit() {};
	
	/**
	 * Called before everything is rendered
	 */
	public void preLoop() {};
	
	/**
	 * Called in the middle
	 * After camera movement
	 * Before scene render
	 */
	public void loop() {};
	
	/**
	 * Called after everything is rendered
	 */
	public void postLoop() {};
	
	/**
	 * Called for cleaning up everything
	 */
	public abstract void exit();
	
	public abstract EntityAbstractPlayer getPlayer();
	
	
	
	public final DisplayManager getDisplayManager() {
		return displayManager;
	}
	
	public final MasterRenderer getMasterRenderer() {
		return masterRenderer;
	}
	
	public final Loader getLoader() {
		return loader;
	}
	
	public final AudioMaster getAudioMaster() {
		return audioMaster;
	}
	
	public static GameEngine getInstance() {
		return instance;
	}
	
	public final List<Entity> getEntities(){
		return entities;
	}
	
	public final Camera getCamera() {
		return camera;
	}
	
	public final GuiRenderer getGuiRenderer() {
		return guiRenderer;
	}
	
	public final List<Gui> getGuis() {
		return guis;
	}
	
	public final List<Terrain> getTerrains() {
		return terrains;
	}
	
	public final TextMaster getTextMaster() {
		return textMaster;
	}
	
	public final FontType getFont() {
		return font;
	}
	
	public final void setFont(FontType font) {
		this.font = font;
	}
	
	public final OBJLoader getOBJLoader() {
		return objLoader;
	}
	
	public final List<Light> getLights() {
		return lights;
	}
	
	public final OBJLoader getObjLoader() {
		return objLoader;
	}
	
	public final void start() {
		instance = this;
		preInit();
		
		displayManager.createDisplay();
		loader = new Loader();
		objLoader = new OBJLoader(loader);
		
		guiRenderer = new GuiRenderer(loader);
		masterRenderer = new MasterRenderer(loader);
		textMaster = new TextMaster();
		
		textMaster.init(loader);
		font = new FontType(loader, "Verdana");
		
		

		Log.setLogSystem(new BlankLogger()); //Stop SlickUtil from logging pointless errors
		
		audioMaster.init();
		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE);

		//ParticleMaster.init(loader, renderer.getProjectionMatrix());
		//main game loop gets called, makes window etc
		
		displayManager.aboutToStartGameLoop();
		
		postInit();
		camera = new Camera(getPlayer(), masterRenderer.getProjectionMatrix());
		entities.add(getPlayer());
		
		while(!Display.isCloseRequested()) {
			preLoop();
			
			
			getPlayer().onScrollWheel(Mouse.getDWheel());
			//scheduler.update();
			camera.movement();
			//ParticleMaster.update(camera);
			
			loop();
			for(Terrain terrain : terrains) {
				for (int i = 0; i < entities.size(); ++i) {
					Entity entity = entities.get(i);
					if (entity instanceof EntityMoveable) {
						if(terrain.getX() <= entity.getPosition().x) { 
							if(terrain.getX() + terrain.getSize() > entity.getPosition().x) {
								if(terrain.getZ() <= entity.getPosition().z) {
									if(terrain.getZ() + terrain.getSize() > entity.getPosition().z) {
										((EntityMoveable)entity).move(terrain);
									}
								}
							}
						}
					}
				}
			}

			

			audioMaster.setListenerData(camera);

			for(Terrain terrain:terrains) {
				masterRenderer.prossessTerrain(terrain);
			}

			for(Entity entity:entities) {
				masterRenderer.processEntity(entity);
			}

			for(Speaker source:audioMaster.sources) {
				source.tick();
			}
			
			masterRenderer.render(lights, camera);
			
			postLoop();
			
			//ParticleMaster.renderParticles(camera);
			
			guiRenderer.render(guis);
			for(Gui gui:guis) {
				if(gui.isVisible()) {
					for(GuiText text:gui.getTextsToBeRendered()) {
						textMaster.loadText(text);
					}
					textMaster.render();
					for(GuiText text:gui.getTextsToBeRendered()) {
						textMaster.removeText(text);
					}
				}
			}
			
			
			
			displayManager.updateDisplay();
		}
		cleanUp();
	}
	
	public final void cleanUp() {
		audioMaster.cleanUp();
		guiRenderer.cleanUp();
		textMaster.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		exit();
	}
	
}
