package org.golde.java.game.multiplayer;

import java.util.ArrayList;
import java.util.List;

import org.golde.java.game.Main;
import org.golde.java.game.common.packets.PacketRegister;
import org.golde.java.game.common.packets.player.PacketAddPlayer;
import org.golde.java.game.common.packets.player.PacketRemovePlayer;
import org.golde.java.game.common.packets.player.PacketUpdatePlayerLocation;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

public class Multiplayer extends Listener {

	private Client client;
	private List<MPlayer> players = new ArrayList<MPlayer>();

	public void connect() {
		if(isConnected()) {
			log("tried to connect another time, but I am already connected!");
			return;
		}
		try{
			System.out.println("Connecting...");
			client = new Client();

			PacketRegister.registerAllPackets(client.getKryo());

			client.start();
			client.connect(5000, "localhost", 22222, 22222);
			client.addListener(this);
			log("Running networking client!");
		}catch (Exception e) {
			e.printStackTrace();
			Main.exit();
		}
	}

	public boolean isConnected() {
		return client != null && client.isConnected();
	}

	@Override
	public void received(Connection c, Object o){
		
		if(o instanceof KeepAlive) {
			return;
		}
		
		/*if(c.getID() == client.getID()) {
			log("Its me!");
			return;
		}*/
		
		log("RECIEVED (" + c.getID() + "): " + o);
		
		if(o instanceof PacketAddPlayer){
			PacketAddPlayer packet = (PacketAddPlayer) o;
			MPlayer newPlayer = new MPlayer();
			newPlayer.id = packet.id;
			newPlayer.x = packet.x;
			newPlayer.y = packet.y;
			newPlayer.z = packet.z;
			players.add(newPlayer);
			log("A new player has joined the world. ID: "+newPlayer.id);

		}

		else if(o instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			players.remove(getPlayerByID(packet.id));
			log("A player has left the world. ID: "+packet.id);

		}

		else if(o instanceof PacketUpdatePlayerLocation){
			PacketUpdatePlayerLocation packet = (PacketUpdatePlayerLocation) o;
			getPlayerByID(packet.id).x = packet.x;
			getPlayerByID(packet.id).y = packet.y;
			getPlayerByID(packet.id).z = packet.z;
		}

	}
	
	@Override
	public void disconnected(Connection arg0) {
		log("Disconnected.");
		Main.exit();
	}

	public List<MPlayer> getPlayers() {
		return players;
	}

	public Client getClient() {
		return client;
	}

	private MPlayer getPlayerByID(int id){
		for(MPlayer agent : players){
			if(agent.id == id) return agent;
		}
		return null;
	}

	private static void log(String s) {
		System.out.println("[MP - CLIENT] " + s);
	}

}
