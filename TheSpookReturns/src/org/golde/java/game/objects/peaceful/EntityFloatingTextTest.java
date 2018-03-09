package org.golde.java.game.objects.peaceful;

import org.golde.java.game.gui.base.GuiText;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.TextMaster;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class EntityFloatingTextTest extends Entity{

	GuiText text;
	
	public EntityFloatingTextTest(Loader loader, float x, float y, float z) {
		super(null, new Vector3f(x, y, z), 0, 0, 0, 1);

		text = new GuiText("Test", 3f, fromVec3f(getPosition()), 10f, true);
	}
	
	@Override
	public void onRender() {
		text.setPosition(fromVec3f(getPosition()));
		/*TextMaster.loadText(text);
		TextMaster.render();
		TextMaster.removeText(text);*/
	}
	
	private Vector2f fromVec3f(Vector3f in) {
		return new Vector2f(in.getX(), in.getY());
	}
	
}
