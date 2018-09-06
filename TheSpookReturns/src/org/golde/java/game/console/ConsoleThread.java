package org.golde.java.game.console;

import java.util.Scanner;

import org.golde.java.game.GLog;
import org.golde.java.game.Main;
import org.golde.java.game.objects.player.EntityPlayer;
import org.lwjgl.util.vector.Vector3f;

public class ConsoleThread extends Thread {

	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		while(true) {

			try {
				String result = scan.nextLine();
				onCommand(result);
			}
			catch (Exception e) {
				GLog.error(e, "Failed to scan...");
			}

		}
	}

	private void onCommand(String cmd) {
		String[] args = cmd.split(" ");
		EntityPlayer player = Main.getPlayer();
		if(cmd.toLowerCase().startsWith("tp")) {
			player.setPosition(new Vector3f(Float.parseFloat(args[1]),Float.parseFloat(args[2]),Float.parseFloat(args[3])));
		}

		else if(cmd.toLowerCase().startsWith("noclip")) {
			player.setNoclip(!player.isNocliping());
			GLog.info("[Console] No Clip: " + player.isNocliping());
		}



		else {
			GLog.info("[Console] Unknown cmd");
		}
	}

}
