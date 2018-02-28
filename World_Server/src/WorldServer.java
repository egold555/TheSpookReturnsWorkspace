import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class WorldServer extends Listener {

	static Server server;
	
	static List<Agent> agents = new ArrayList<Agent>();
	
	public static void main(String[] args) throws IOException{
		server = new Server();
		server.bind(27960, 27960);
		server.getKryo().register(PacketUpdateX.class);
		server.getKryo().register(PacketUpdateY.class);
		server.getKryo().register(PacketUpdateZ.class);
		server.getKryo().register(PacketNewAgent.class);
		server.getKryo().register(PacketRemoveAgent.class);
		server.start();
		server.addListener(new WorldServer());
		System.out.println("Server ready on port 27960");
	}
	
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdateX){
			PacketUpdateX packet = (PacketUpdateX) o;
			getAgentByID(c.getID()).x = packet.x;
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
		}else if(o instanceof PacketUpdateY){
			PacketUpdateY packet = (PacketUpdateY) o;
			getAgentByID(c.getID()).y = packet.y;
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
		}else if(o instanceof PacketUpdateZ){
			PacketUpdateZ packet = (PacketUpdateZ) o;
			getAgentByID(c.getID()).z = packet.z;
			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
			
		}
	}
	
	public void connected(Connection c){
		System.out.println("Connection from "+c.getRemoteAddressTCP().getHostString());
		Agent agent = new Agent();
		agent.connection = c;
		agent.username = "unset";
		agent.x = 0;
		agent.y = 0;
		agent.z = 0;
		agents.add(agent);
		
		PacketNewAgent newCoPacket = new PacketNewAgent();
		newCoPacket.id = c.getID();
		newCoPacket.x = 0;
		newCoPacket.y = 0;
		newCoPacket.z = 0;
		server.sendToAllExceptTCP(c.getID(), newCoPacket);
		
		for(Agent a : agents){
			if(a.connection.getID() == c.getID()) continue;
			PacketNewAgent thirdPacket = new PacketNewAgent();
			thirdPacket.id = a.connection.getID();
			thirdPacket.x = a.x;
			thirdPacket.y = a.y;
			thirdPacket.z = a.z;
			c.sendTCP(thirdPacket);
		}
	}
	
	public void disconnected(Connection c){
		System.out.println("Connection dropped.");
		PacketRemoveAgent removePacket = new PacketRemoveAgent();
		removePacket.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), removePacket);
		agents.remove(getAgentByID(c.getID()));
	}
	
	public static Agent getAgentByID(int id){
		for(Agent agent : agents){
			if(agent.connection.getID() == id){
				return agent;
			}
		}
		return null;
	}
}
