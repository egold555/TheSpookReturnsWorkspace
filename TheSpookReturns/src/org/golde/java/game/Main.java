package org.golde.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.golde.java.game.audio.AudioMaster;
import org.golde.java.game.audio.Source;
import org.golde.java.game.common.packets.player.PacketUpdatePlayerLocation;
import org.golde.java.game.font.FontType;
import org.golde.java.game.gui.base.Gui;
import org.golde.java.game.gui.base.GuiText;
import org.golde.java.game.gui.mainMenu.GuiMainMenu;
import org.golde.java.game.gui.mainMenu.GuiOptions;
import org.golde.java.game.gui.player.GuiDebug;
import org.golde.java.game.gui.player.GuiFade;
import org.golde.java.game.helpers.BetterKeyboard;
import org.golde.java.game.helpers.BlankLogger;
import org.golde.java.game.multiplayer.MPlayer;
import org.golde.java.game.multiplayer.Multiplayer;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.objects.base.entities.EntityMoveable;
import org.golde.java.game.objects.enemy.EntityDog;
import org.golde.java.game.objects.enemy.EntitySkeleton;
import org.golde.java.game.objects.enemy.EntitySlasher;
import org.golde.java.game.objects.light.EntityLamp;
import org.golde.java.game.objects.light.Light;
import org.golde.java.game.objects.peaceful.EntityCat;
import org.golde.java.game.objects.peaceful.EntityHorse;
import org.golde.java.game.objects.player.Camera;
import org.golde.java.game.objects.player.EntityPlayer;
import org.golde.java.game.objects.terrain.decoration.EntityFirepit;
import org.golde.java.game.objects.terrain.decoration.EntityMagicCircle;
import org.golde.java.game.objects.terrain.decoration.EntityOilDrum;
import org.golde.java.game.objects.terrain.decoration.EntityPiano;
import org.golde.java.game.objects.terrain.decoration.EntityPiano.EnumSongs;
import org.golde.java.game.objects.terrain.decoration.EntityTV;
import org.golde.java.game.objects.terrain.decoration.WaterTile;
import org.golde.java.game.objects.terrain.plants.EntityTree;
import org.golde.java.game.objects.terrain.structures.EntityChurch;
import org.golde.java.game.objects.terrain.structures.EntityFarmHouse;
import org.golde.java.game.renderEngine.DisplayManager;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.TextMaster;
import org.golde.java.game.renderEngine.WaterFrameBuffers;
import org.golde.java.game.renderEngine.particles.ParticleMaster;
import org.golde.java.game.renderEngine.particles.RainMaker;
import org.golde.java.game.renderEngine.renderers.GuiRenderer;
import org.golde.java.game.renderEngine.renderers.MasterRenderer;
import org.golde.java.game.scheduler.Scheduler;
import org.golde.java.game.shaders.WaterShader;
import org.golde.java.game.terrains.HeightMapTerrain;
import org.golde.java.game.terrains.Terrain;
import org.golde.java.game.textures.gui.GuiStaticTexture;
import org.golde.java.game.textures.particles.ParticleTexture;
import org.golde.java.game.textures.terrain.TerrainTexture;
import org.golde.java.game.textures.terrain.TerrainTexturePack;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.util.Log;

import com.esotericsoftware.kryonet.Client;

/**
 * Main class of the game.
 * @author Eric
 *
 */
public class Main {

	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<Light> lights = new ArrayList<Light>();
	private static List<Terrain> terrains = new ArrayList<Terrain>();
	private static List<Gui> guis = new ArrayList<Gui>();
	private static GameState gameState = GameState.TITLE_SCREEN;
	private static GameState lastGameState = null;
	private static GuiMainMenu guiMainMenu;
	private static GuiOptions guiOptions;
	private static GuiDebug guiDebug;
	private static FontType FONT;
	private static EntityPlayer player;
	private static Loader loader;
	private static GuiRenderer guiRenderer;
	private static MasterRenderer renderer;
	private static Scheduler scheduler = new Scheduler();
	private static Source rainSound = null;
	private static Camera camera;
	private static Source titleScreenMusic = null;
	private static Multiplayer multiplayer = null;

	//Getters for everything
	public static List<Entity> getEntities()
	{
		return entities;
	}

	public static Camera getCamera() {
		return camera;
	}

	public static MasterRenderer getRenderer() {
		return renderer;
	}

	public static List<Light> getLights() {
		return lights;
	}

	public static GameState getGameState() {
		return gameState;
	}

	public static void setGameState(GameState gameState) {
		Main.gameState = gameState;
	}

	public static FontType getFont() {
		return FONT;
	}

	public static EntityPlayer getPlayer() {
		return player;
	}

	public static Multiplayer getMultiplayer() {
		return multiplayer;
	}

	public static float getHeightOfTerrain(float x, float z) 
	{
		for(Terrain terrain : terrains) {
			if(terrain.getX() <= x) { 
				if(terrain.getX() + terrain.getSize() > x) {
					if(terrain.getZ() <= z) {
						if(terrain.getZ() + terrain.getSize() > z) {
							return terrain.getHeightOfTerrain(x,  z);
						}
					}
				}
			}
		}

		return 0;
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}
	//End

	static boolean wireframe = false;

	public static void main(String[] args) {
		//**********Basic Setup***************
		DisplayManager.createDisplay();
		loader = new Loader();
		TextMaster.init(loader);
		FONT = new FontType(loader, "Verdana");
		guiRenderer = new GuiRenderer(loader);
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		renderer = new MasterRenderer(loader, fbos);

		Log.setLogSystem(new BlankLogger()); //Stop SlickUtil from logging pointless errors

		AudioMaster.init();
		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE);

		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		//**********PLAYER***********
		GuiFade guiFade = new GuiFade(loader);
		guis.add(guiFade);
		player = new EntityPlayer(loader, new Vector3f(0, 3, -34), 181.7f, 4, 0, 1, guiFade);
		camera = new Camera(player, renderer.getProjectionMatrix());
		entities.add(player);
		//***************************

		multiplayer = new Multiplayer();

		RainMaker rainParticles = new RainMaker(new ParticleTexture(loader.loadTexture("particles/cosmic"), 4), 0.5f, 1000, 150, 5, 200, 100, -10);

		//********TERRAIN TEXTURE STUFF*******

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/maps/blendMap"));

		//************************************


		//**********TERRAINS******************
		final String heightMap = "heightMap";
		Terrain terrain1 = new HeightMapTerrain(0, 0, loader, texturePack, blendMap, heightMap);
		Terrain terrain2 = new HeightMapTerrain(-1, -1, loader, texturePack, blendMap, heightMap);
		Terrain terrain3 = new HeightMapTerrain(0, -1, loader, texturePack, blendMap, heightMap);
		Terrain terrain4 = new HeightMapTerrain(-1, 0, loader, texturePack, blendMap, heightMap);

		terrains.add(terrain1);
		terrains.add(terrain2);
		terrains.add(terrain3);
		terrains.add(terrain4);
		//*************************************

		//*****GUI's*****
		guiMainMenu = new GuiMainMenu(loader);
		guis.add(guiMainMenu);

		guiOptions = new GuiOptions(loader);
		guiOptions.setVisible(false);
		guis.add(guiOptions);

		guiDebug = new GuiDebug(player);
		guis.add(guiDebug);

		guis.add(player.getGuiOverlay());
		player.getGuiOverlay().setVisible(false);



		//*********************

		entities.add(new EntityTree(loader, 90, 90, terrain1, 10f));

		entities.add(new EntitySkeleton(loader, 100, 100, terrain1, 1f));
		entities.add(new EntitySlasher(loader, 40, 0, terrain1, 10));
		entities.add(new EntityChurch(loader, 300, 300, terrain1, 40));
		entities.add(new EntityFarmHouse(loader, 0, 0, terrain1, 2));
		entities.add(new EntityPiano(loader, 40, 40, terrain1, 2.5f, EnumSongs._RANDOM));

		entities.add(new EntityDog(loader, 1, -50, 50, terrain1, 0.1f));
		entities.add(new EntityCat(loader, 60, 40, terrain1, 10));
		entities.add(new EntityHorse(loader, 70, 40, terrain1, 0.1f));

		lights.add(new Light(new Vector3f(2000, 2000, 0), new Vector3f(0.2f, 0.2f, 0.2f))); //Sun
		entities.add(new EntityLamp(loader, 100, 10, terrain1, 1)); //.setAttenuation(0.5f, 0.003f, 0.0005f).setSpotLight(new Vector3f(-1, -0.1F, -0.15F), 10, 30)
		entities.add(new EntityLamp(loader, -100, 10, terrain1, 1).setColor(new Vector3f(2, 0, 0)));

		entities.add(new EntityFirepit(loader, -60, 60, terrain1, 1));
		entities.add(new EntityOilDrum(loader, 0, 80, terrain1, 10));

		entities.add(new EntityMagicCircle(loader, 0, -100, terrain1, 10));

		entities.add(new EntityTV(loader, 50, -50, terrain1, 0.8f, "Noice.mp4")); // 0.8f Wrecked VHS.mp4



		//Sort all registered Guis by Z index
		Collections.sort(guis);

		//entities.add(new EntityTV(loader, 50, -50, terrain1, 0.8f)); // 0.8f
		entities.add(new EntityTV(loader, 50, -50, terrain1, 0.8f, "Wrecked VHS.mp4")); // 0.8f Wrecked VHS.mp4

		//Water
		List<WaterTile> water = new ArrayList<WaterTile>();
		WaterTile waterTile = new WaterTile(90, -117, 0);
		water.add(waterTile);


		//		GuiStaticTexture waterTestTextureReflection = new GuiStaticTexture(fbos.getReflectionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//		GuiStaticTexture waterTestTextureRefraction = new GuiStaticTexture(fbos.getRefractionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//		
		//		player.getGuiOverlay().addGuiTexture(waterTestTextureReflection);
		//		player.getGuiOverlay().addGuiTexture(waterTestTextureRefraction);

		//player.setHasGravity(false);

		//Final
		DisplayManager.aboutToStartGameLoop();

		//Main Loop
		while(!Display.isCloseRequested()) {

			try {

				if(gameState != lastGameState) {
					initState(gameState);
					lastGameState = gameState;
				}

				int dWheel = Mouse.getDWheel();
				player.onScrollWheel(dWheel);

				scheduler.update();
				camera.movement();

				for(MPlayer mplayer : multiplayer.getPlayers()) {

					if(mplayer.entity == null) {
						mplayer.entity = new EntityTree(loader, mplayer.x, mplayer.z, terrain1, 10);
						GLog.info("Added Entity to mplayer.entity (" + mplayer.id + "");
					}
					mplayer.entity.setPosition(mplayer.x, mplayer.y, mplayer.z);
					//mplayer.entity.setRotation(mplayer.rx, mplayer.ry, mplayer.rz);
				}

				if(gameState == GameState.PLAYING && player != null && multiplayer.isConnected()) {
					sendPlayerPosition();
				}


				ParticleMaster.update(camera);

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





				AudioMaster.setListenerData(camera);

				for(Source source:AudioMaster.sources) {
					source.tick();
				}

				//rainParticles.generateParticles(player.getPosition().x, player.getPosition().z);

				//Game renderer
				GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

				//TODO: Animated textures are 3x faster
				//Render the scene once and store it in the water frame buffer - reflection
				fbos.bindReflectionFrameBuffer();
				float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
				camera.getPosition().y -= distance;
				camera.invertPitch();
				renderer.renderScene(lights, camera, terrains, entities, multiplayer.getPlayers(), new Vector4f(0, 1, 0, -waterTile.getHeight()));
				camera.getPosition().y += distance;
				camera.invertPitch();

				//Render the scene once and store it in the water frame buffer - refraction
				fbos.bindRefractionFrameBuffer();
				renderer.renderScene(lights, camera, terrains, entities, multiplayer.getPlayers(), new Vector4f(0, -1, 0, waterTile.getHeight()));

				//Actually render the game to the screen
				GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
				fbos.unbindCurrentFrameBuffer();
				renderer.renderScene(lights, camera, terrains, entities, multiplayer.getPlayers(), new Vector4f(0, 1, 0, 1000000)); //1000000 so nothing will be clipped, hacky workaround because some drivers ignore GL11.glDisable(GL30.GL_CLIP_DISTANCE0); 

				renderer.renderWater(water, camera);


				//render last


				ParticleMaster.renderParticles(camera);

				guiRenderer.render(guis);
				for(Gui gui:guis) {
					if(gui.isVisible()) { //Need to find a way to find all overlayed guis, and if the z index is over, then remove the text
						for(GuiText text:gui.getTextsToBeRendered()) {
							TextMaster.loadText(text);
						}
						TextMaster.render();
						for(GuiText text:gui.getTextsToBeRendered()) {
							TextMaster.removeText(text);
						}
					}
				}

				if(BetterKeyboard.wasKeyPressed(Keyboard.KEY_F5)) {
					wireframe = !wireframe;
					GLog.info("Wireframe: + wireframe");
					if(wireframe) {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
					} else {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
					}
				}

				//Random keyboard stuff
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					exit();
				}

				DisplayManager.updateDisplay();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

		}

		//*****Clean up*********
		exit();
	}

	private static void sendPlayerPosition() {
		Client client = multiplayer.getClient();
		if(player.getOldPosition().x != player.getPosition().x || 
				player.getOldPosition().y != player.getPosition().y ||
				player.getOldPosition().z != player.getPosition().z) {
			PacketUpdatePlayerLocation updatePos = new PacketUpdatePlayerLocation();
			updatePos.x = player.getPosition().x;
			updatePos.y = player.getPosition().y;
			updatePos.z = player.getPosition().z;
			client.sendUDP(updatePos);
		}
	}

	public static void exit() {
		GLog.info("Exiting Game");
		ParticleMaster.cleanUp();
		GLog.info("Cleanup: ParticleMaster");

		AudioMaster.cleanUp();
		GLog.info("Cleanup: AudioMaster");

		TextMaster.cleanUp();
		GLog.info("Cleanup: TextMaster");

		guiRenderer.cleanUp();
		GLog.info("Cleanup: guiRenderer");

		renderer.cleanUp();
		GLog.info("Cleanup: renderer");

		loader.cleanUp();
		GLog.info("Cleanup: loader");

		DisplayManager.closeDisplay();

	}

	//Called when ever you set the state
	static void initState(GameState state) throws LWJGLException {
		GLog.info("Switched Game State: " + state.name());

		if(state == GameState.TITLE_SCREEN) {
			guiMainMenu.setVisible(true);
			guiOptions.setVisible(false);
			if(titleScreenMusic == null) {
				titleScreenMusic = new Source();
				titleScreenMusic.setPosition(0, 0, 0);
				titleScreenMusic.play(AudioMaster.loadSound("homescreen"));
			}
			Mouse.setGrabbed(false);
		}

		else if(state== GameState.OPTIONS) {
			guiOptions.setVisible(true);
			guiMainMenu.setVisible(false);
			Mouse.setGrabbed(false);
		}

		else if(state == GameState.PLAYING) {
			titleScreenMusic.fadeOut(0.4f);
			guiMainMenu.setVisible(false);
			guiOptions.setVisible(false);
			player.getGuiOverlay().setVisible(true);
			Mouse.setGrabbed(true);
			if(rainSound == null) {
				rainSound = new Source();
				rainSound.setPosition(0, 0, 0);
				rainSound.setLooping(true);
				rainSound.play(AudioMaster.loadSound("weather/rain"));
			}

		}
	}



}
