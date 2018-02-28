import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Main {

	public static final String version = "1";
	public static Game game;
	public static Map<String,Texture> textures = new HashMap<String,Texture>();
	public static Network net = new Network("localhost", 27960);
	
	public static void main(String[] args) throws Exception {
		System.out.println("Running World, version: "+version);
		
		Display.setDisplayMode(new DisplayMode(512,512));
		Display.setVSyncEnabled(true);
		Display.create();
		
		loadTexture("grass", "PNG", new File("grass.png"));
		loadTexture("crate", "JPG", new File("crate.jpg"));
		
		game = new Game();
		net.connect(); // Connect to the server
		
		while(!Display.isCloseRequested()){
			clearGL();
			initGL3();

			game.update();
			game.render();
			
			Display.update();
			Display.sync(60);
		}
	}

	private static void clearGL() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
	}

	private static void initGL3() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(90, Display.getWidth()/1f/Display.getHeight(), 0.1f, 500);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}
	
	public static void loadTexture(String name, String format, File file){
		try {
			textures.put(name, TextureLoader.getTexture(format, ResourceLoader.getResourceAsStream(file.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
