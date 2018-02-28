import org.lwjgl.opengl.GL11;

public class PolyFace extends Poly {

	float x1, y1, z1;
	float x2, y2, z2;
	
	public PolyFace(float startX, float startY, float startZ, float endX, float endY, float endZ) {
		x1 = startX;
		y1 = startY;
		z1 = startZ;
		x2 = endX;
		y2 = endY;
		z2 = endZ;
	}

	public void render() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, 		y1, 				z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1+(x2-x1), y1, 				z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1+(x2-x1), y1+(y2-y1),			z1+(z2-z1));
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, 		y1+(y2-y1),			z1+(z2-z1));
		GL11.glEnd();
	}
}
