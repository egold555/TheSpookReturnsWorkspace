import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryonet.Client;


public class Game {

	static Player player;
	static Vector3f cameraPosition = new Vector3f(10, 0, 2);
	static Vector3f cameraRotation = new Vector3f();
	public static List<PolyVoxel> collidable = new ArrayList<PolyVoxel>();
	public static List<PolyFace> floor = new ArrayList<PolyFace>();
	
	public Game(){
		Mouse.setGrabbed(false);
		player = new Player();
		player.vector = new Vector3f(0,2.4f,0);
		
		collidable.add(new PolyVoxel(5, 0, 3, 7, 2, 5).setTexture( Main.textures.get("crate") ));
		
		for(int x=-20;x<20;x+=3){
			for(int z=-20;z<20;z+=3){
				floor.add(new PolyFace(x, 0, z, x+3, 0, z+3));
			}
		}
	}
	
	public void update() {
		player.input();
		player.mouseLook();
		player.updateMotion();
		player.collisions(0.4f);
		player.individualPhysics();
		
		cameraPosition.x = player.vector.x;
		cameraPosition.y = player.vector.y+2.4f;
		cameraPosition.z = player.vector.z;
		cameraRotation.x = player.rotation.x;
		cameraRotation.y = player.rotation.y;
		cameraRotation.z = player.rotation.z;
		
		if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (Mouse.isButtonDown(1)) {
            Mouse.setGrabbed(false);
        }
		
		sendPlayerData();
	}
	
	public void sendPlayerData(){
		Client client = Main.net.client;
		if(player.previous.x != player.vector.x){
			PacketUpdateX packet = new PacketUpdateX();
			packet.x = player.vector.x;
			client.sendUDP(packet);
		}
		if(player.previous.y != player.vector.y){
			PacketUpdateY packet = new PacketUpdateY();
			packet.y = player.vector.y;
			client.sendUDP(packet);
		}
		if(player.previous.z != player.vector.z){
			PacketUpdateZ packet = new PacketUpdateZ();
			packet.z = player.vector.z;
			client.sendUDP(packet);
		}
	}
	
	public void render() {
		translateToCamera();
		
		color(0f, 1f, 0f);
		Main.textures.get("grass").bind();
		for(PolyFace v : floor){
			v.render();
		}

		color(1,1,1);
		for(PolyVoxel v : collidable){
			v.getTexture().bind();
			v.render();
		}
		
		for(Agent agent : Network.agents){
			PolyVoxel voxel = new PolyVoxel(agent.x-0.5f, agent.y, agent.z-0.5f, agent.x+0.5f, agent.y+2.5f, agent.z+0.5f);
			voxel.render();
		}
	}

	public void color(float f, float g, float h) {
		GL11.glColor3f(f, g, h);
	}

	private void translateToCamera() {
		GL11.glRotatef(cameraRotation.x, 1, 0, 0);
		GL11.glRotatef(cameraRotation.y, 0, 1, 0);
		GL11.glRotatef(cameraRotation.z, 0, 0, 1);
		GL11.glTranslatef(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
	}

}
