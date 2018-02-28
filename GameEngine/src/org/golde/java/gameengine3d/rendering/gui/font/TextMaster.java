package org.golde.java.gameengine3d.rendering.gui.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.golde.java.gameengine3d.rendering.gui.GuiText;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.golde.java.gameengine3d.rendering.renderers.FontRenderer;


public class TextMaster {

	private Loader loader;
	private Map<FontType, List<GuiText>> texts = new HashMap<FontType, List<GuiText>>();
	private FontRenderer renderer;

	public void init(Loader theLoader){
		renderer = new FontRenderer();
		loader = theLoader;
	}

	public void render(){
		renderer.render(texts);
	}

	public void loadText(GuiText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GuiText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GuiText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public void removeText(GuiText text){
		List<GuiText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(text.getFont());
		}
	}

	public void cleanUp(){
		renderer.cleanUp();
	}

}
