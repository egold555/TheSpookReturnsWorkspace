package org.golde.java.game.console;

import org.golde.java.game.GLog;
import org.golde.java.game.objects.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class CommandHandler {

	public static void onCommand(EntityPlayer player, String cmd) {
		String[] args = cmd.split("\\s+");
		
		if(cmd.toLowerCase().startsWith("tp")) {
			player.setPosition(new Vector3f(Float.parseFloat(args[1]),Float.parseFloat(args[2]),Float.parseFloat(args[3])));
		}
		
		if(cmd.toLowerCase().startsWith("wireframe")) {
			boolean value = Boolean.parseBoolean(args[1]);
			if(value) {
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			       GL11.glDisable(GL11.GL_TEXTURE_2D);
			} else {
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}
		
		
		
		else {
			GLog.info("Unknown cmd");
		}
	}
	
}
