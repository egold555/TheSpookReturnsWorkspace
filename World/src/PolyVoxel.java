import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;


public class PolyVoxel extends Poly {

	float x1, y1, z1, x2, y2, z2, centerX, centerY, centerZ;
	float width, height, length;
	float yTop, yBottom;
	private Texture texture;
	
	public PolyVoxel(float startX, float startY, float startZ, float endX, float endY, float endZ){
		x1 = startX;
		y1 = startY;
		z1 = startZ;
		x2 = endX;
		y2 = endY;
		z2 = endZ;
		width = x2-x1;
		height = y2-y1;
		length = z2-z1;
		centerX = x1+(width/2);
		centerY = y2+(height/2);
		centerZ = z1+(length/2);
		yTop = Math.abs(y2-y1);
		yBottom = y1+yTop-height;
	}
	
	public PolyVoxel setTexture(Texture texture){
		this.texture = texture;
		return this;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public void render() {
		GL11.glBegin(GL11.GL_QUADS);
		//Top
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+width, y1, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+width, y1, z1+length);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y1, z1+length);
		//Bottom
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1+height, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+width, y1+height, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+width, y1+height, z1+length);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y1+height, z1+length);
		//Front
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+width, y1, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+width, y1+height, z1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y1+height, z1);
		//Back
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z1+length);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+width, y1, z1+length);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+width, y1+height, z1+length);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y1+height, z1+length);
		//Left side
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1, y1, z1+length);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1, y1+height, z1+length);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y1+height, z1);
		//Right side
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1+width, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+width, y1, z1+length);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+width, y1+height, z1+length);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1+width, y1+height, z1);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3f(centerX, yBottom, centerZ);
		GL11.glEnd();
	}

}
