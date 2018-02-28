package org.golde.java.game.common.packets;

import org.golde.java.game.common.packets.player.PacketAddPlayer;
import org.golde.java.game.common.packets.player.PacketRemovePlayer;
import org.golde.java.game.common.packets.player.PacketUpdatePlayerLocation;

import com.esotericsoftware.kryo.Kryo;

public class PacketRegister {

	private static final Class<?>[] PACKET_REGISTER_ORDER = {
			PacketAddPlayer.class,
			PacketRemovePlayer.class,
			PacketUpdatePlayerLocation.class
	};
	
	public static void registerAllPackets(Kryo kryo) {
		for(Class<?> packet:PACKET_REGISTER_ORDER) {
			System.out.println("Registering: " + packet.getName());
			kryo.register(packet);
		}
	}
	
}
