package org.golde.java.game.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.golde.java.game.common.packets.PacketRegister;
import org.golde.java.game.common.packets.player.PacketAddPlayer;
import org.golde.java.game.common.packets.player.PacketRemovePlayer;
import org.golde.java.game.common.packets.player.PacketUpdatePlayerLocation;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class TSRServer extends Listener{

	private static Server server;
	private static List<MPlayer> players = new ArrayList<MPlayer>();

	public static void main(String[] args) throws IOException {
		server = new Server();
		server.bind(22222, 22222);

		PacketRegister.registerAllPackets(server.getKryo());

		server.start();
		server.addListener(new TSRServer());
		System.out.println("Server ready on port " + 22222);
	}

	@Override
	public void connected(Connection c) {
		log("Connection from " + c.getRemoteAddressTCP().getHostString());
		MPlayer agent = new MPlayer();
		agent.connection = c;
		agent.x = 0;
		agent.y = 0;
		agent.z = 0;
		players.add(agent);

		PacketAddPlayer newCoPacket = new PacketAddPlayer();
		newCoPacket.id = c.getID();
		newCoPacket.x = 0;
		newCoPacket.y = 0;
		newCoPacket.z = 0;
		server.sendToAllExceptTCP(c.getID(), newCoPacket);
		log("Player " + newCoPacket.id + " joined.");

		for(MPlayer a : players){
			if(a.connection.getID() == c.getID()) continue;
			PacketAddPlayer thirdPacket = new PacketAddPlayer();
			thirdPacket.id = a.connection.getID();
			thirdPacket.x = a.x;
			thirdPacket.y = a.y;
			thirdPacket.z = a.z;
			c.sendTCP(thirdPacket);
		}
	}

	@Override
	public void disconnected(Connection c) {
		PacketRemovePlayer removePacket = new PacketRemovePlayer();
		removePacket.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), removePacket);
		log("MPlayer " + removePacket.id + " left.");
		players.remove(getPlayerByID(c.getID()));
	}

	@Override
	public void received(Connection c, Object o) {
		if(!(o instanceof PacketUpdatePlayerLocation)) {
			log("RECIEVED (" + c.getID() + "): " + o);
		}
		if(o instanceof PacketUpdatePlayerLocation){
			PacketUpdatePlayerLocation packet = (PacketUpdatePlayerLocation) o;
			getPlayerByID(c.getID()).x = packet.x;
			getPlayerByID(c.getID()).y = packet.y;
			getPlayerByID(c.getID()).z = packet.z;
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);	
		}
	}

	private static MPlayer getPlayerByID(int id){
		for(MPlayer agent : players){
			if(agent.connection.getID() == id){
				return agent;
			}
		}
		return null;
	}

	private static void log(String s) {
		System.out.println("[SERVER] " + s);
	}

}
