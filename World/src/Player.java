import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;


public class Player {

	public static float speed = 0.18f;
	public static final float gravity = 0.028f;
	
	private boolean moveForward;
	private boolean moveBackward;
	private boolean strafeLeft;
	private boolean strafeRight;
	public Vector3f vector = new Vector3f(2,3,-5);
	public Vector3f previous = new Vector3f();
	public Vector3f rotation = new Vector3f();
	
	float yBottom=0;

	private boolean onGround, jumping, falling;
	private float yMotion;

	public void input(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			moveForward = true;
		}else{
			moveForward = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			moveBackward = true;
		}else{
			moveBackward = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			strafeLeft = true;
		}else{
			strafeLeft = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			strafeRight = true;
		}else{
			strafeRight = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround){
			yMotion = 0.26f;
		}
	}
	
	public void updateMotion(){
		previous.x = vector.x;
		previous.y = vector.y;
		previous.z = vector.z;
		if(moveForward){
			vector.x += Math.sin(rotation.y*Math.PI/180)*speed;
			vector.z += -Math.cos(rotation.y*Math.PI/180)*speed;
		}
		if(moveBackward){
			vector.x -= Math.sin(rotation.y*Math.PI/180)*speed;
			vector.z -= -Math.cos(rotation.y*Math.PI/180)*speed;
		}
		if(strafeLeft){
			vector.x += Math.sin((rotation.y-90)*Math.PI/180)*speed;
			vector.z += -Math.cos((rotation.y-90)*Math.PI/180)*speed;
		}
		if(strafeRight){
			vector.x += Math.sin((rotation.y+90)*Math.PI/180)*speed;
			vector.z += -Math.cos((rotation.y+90)*Math.PI/180)*speed;
		}
	}
	
	public void mouseLook(){
		if(Mouse.isGrabbed()){
			float mouseDX = Mouse.getDX() * 0.8f * 0.16f;
			float mouseDY = Mouse.getDY() * 0.8f * 0.16f;
			if (rotation.y + mouseDX >= 360) {
				rotation.y = rotation.y + mouseDX - 360;
			} else if (rotation.y + mouseDX < 0) {
				rotation.y = 360 - rotation.y + mouseDX;
			} else {
				rotation.y += mouseDX;
			}
			if (rotation.x - mouseDY >= -89 && rotation.x - mouseDY <= 89) {
				rotation.x += -mouseDY;
			} else if (rotation.x - mouseDY < -89) {
				rotation.x = -89;
			} else if (rotation.x - mouseDY > 89) {
				rotation.x = 89;
			}
		}
	}

	public void collisions(float offset){
		if(!ontopOfCollidablePoly(offset)){
			yBottom = 0;
		}
		for(PolyVoxel v : Game.collidable){
			if(Math.abs(vector.x - v.centerX) < v.width/2 && Math.abs(vector.z - v.centerZ) < v.length/2){
				if( ( Math.abs(vector.y - v.yTop*2) < v.height+(offset)) || vector.y > v.yBottom+v.height-0.5f){
					yBottom = v.y1+v.yTop;
				}else if(v.yBottom > vector.y+2.4f){
					
				}else{
					vector.x = previous.x;
					vector.z = previous.z;
				}
			}
		}
	}

	public void individualPhysics(){
		onGround = (vector.y == yBottom && !jumping && !falling);
		jumping = (yMotion > 0);
		falling = (yMotion < 0);
		vector.y += yMotion;
		if(vector.y > yBottom){
			yMotion -= gravity;
		}
		if(vector.y <= yBottom){
			vector.y = yBottom;
			yMotion = 0;
		}
	}
	
	private boolean ontopOfCollidablePoly(float offset) {
		for(PolyVoxel v : Game.collidable){
			if(Math.abs(vector.x - v.centerX) < v.width/2 && Math.abs(vector.z - v.centerZ) < v.length/2){
				return true;
			}
		}
		return false;
	}
}
