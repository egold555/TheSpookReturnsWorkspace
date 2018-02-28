package org.golde.java.gameengine3d.rendering.gui.button;

public interface IButton {
    void onClick();
 
    void whileHover();
 
    void startHover();
 
    void stopHover();
 
    void checkHover();
 
    void playHoverAnimation(float scaleFactor);
 
    void playerClickAnimation(float scaleFactor);
 
    void hide();
 
    void show();
 
    void reopen();
}