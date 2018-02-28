package org.golde.java.test;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.GameEngineOptions;
import org.golde.java.gameengine3d.helpers.GELog;
import org.golde.java.gameengine3d.objs.Light;
import org.golde.java.gameengine3d.objs.base.entities.Entity;
import org.golde.java.gameengine3d.objs.base.entities.EntityAbstractPlayer;
import org.golde.java.gameengine3d.objs.models.TexturedModel;
import org.golde.java.gameengine3d.objs.terrain.FlatTerrain;
import org.golde.java.gameengine3d.texturing.models.ModelTexture;
import org.golde.java.gameengine3d.texturing.terrain.TerrainTexturePack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
public class GameTest extends GameEngine {
	
	Player player;
	
	@Override
	public void preInit() {

	}
	
	@Override
	public void postInit() {
		player = new Player(getLoader(), new Vector3f(0, 10, 0), 0, 0, 0, 1);
		getEntities().add(new Entity(new TexturedModel(getOBJLoader().loadObjectModel("test"), new ModelTexture(getLoader().loadTexture("test"))), new Vector3f(0, 10, 0), 0, 0, 0, 10));
		getLights().add(new Light(new Vector3f(0, 10, 0), new Vector3f(10, 10, 10)));
		TerrainTexturePack ttp = new TerrainTexturePack("grass", "dirt", "grassFlowers", "path");
		getTerrains().add(new FlatTerrain(0, 0, ttp, getLoader().loadTexture("terrain/maps/blendMap")));
		getTerrains().add(new FlatTerrain(-1, -1, ttp, getLoader().loadTexture("terrain/maps/blendMap")));
		getTerrains().add(new FlatTerrain(0, -1, ttp, getLoader().loadTexture("terrain/maps/blendMap")));
		getTerrains().add(new FlatTerrain(-1, 0, ttp, getLoader().loadTexture("terrain/maps/blendMap")));
	}

	@Override
	public void loop() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			GELog.info("Exited Game");
			System.exit(0);
		}
		
	}

	@Override
	public void exit() {
	
	}

	@Override
	public EntityAbstractPlayer getPlayer() {
		return player;
	}

}
