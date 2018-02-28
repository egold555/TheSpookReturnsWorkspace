import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class Network extends Listener {

	Client client;
	String ip;
	int port;
	static List<Agent> agents = new ArrayList<Agent>();
	
	public Network(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public void connect() {
		try{
			System.out.println("Connecting...");
			client = new Client();
			client.getKryo().register(PacketUpdateX.class);
			client.getKryo().register(PacketUpdateY.class);
			client.getKryo().register(PacketUpdateZ.class);
			client.getKryo().register(PacketNewAgent.class);
			client.getKryo().register(PacketRemoveAgent.class);

			client.start();
			client.connect(5000, "localhost", 27960, 27960);
			client.addListener(this);
			System.out.println("Running networking client!");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdateX){
			PacketUpdateX packet = (PacketUpdateX) o;
			getAgentByID(packet.id).x = packet.x;
		}else if(o instanceof PacketUpdateY){
			PacketUpdateY packet = (PacketUpdateY) o;
			getAgentByID(packet.id).y = packet.y;
		}else if(o instanceof PacketUpdateZ){
			PacketUpdateZ packet = (PacketUpdateZ) o;
			getAgentByID(packet.id).z = packet.z;
		}
		
		if(o instanceof PacketNewAgent){
			PacketNewAgent packet = (PacketNewAgent) o;
			Agent agent = new Agent();
			agent.username = "unset";
			agent.id = packet.id;
			agent.x = packet.x;
			agent.y = packet.y;
			agent.z = packet.z;
			agents.add(agent);
			System.out.println("A new agent has joined the world. ID: "+agent.id);
			
		}else if(o instanceof PacketRemoveAgent){
			PacketRemoveAgent packet = (PacketRemoveAgent) o;
			agents.remove(getAgentByID(packet.id));
			System.out.println("An agent has left the world. ID: "+packet.id);
			
		}
	}
	
	public static Agent getAgentByID(int id){
		for(Agent agent : agents){
			if(agent.id == id) return agent;
		}
		return null;
	}
}
