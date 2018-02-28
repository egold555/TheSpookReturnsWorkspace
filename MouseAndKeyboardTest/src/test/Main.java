package test;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {

	private final static int INT_WIDTH = 800;
	private final static int INT_HEIGHT = 600;
	
	public static void main(String[] args) {
		start();
	}


	private final static void render() {
		while(!Display.isCloseRequested()) {
			pollMouse();
			Display.update();
		}                 
	}

	private final static void destroy() {
		if(Display.isCreated()){
			Display.destroy();
		}
	}


	private final static void pollMouse() {  
		while(Mouse.next()) {

			if(Mouse.getEventButtonState()) {                
				if(Mouse.getEventButton() == 0 ) {
					int x = Mouse.getX();
					int y = Mouse.getY();            
					System.out.println("Left button pressed at X:"+x+" Y:"+y);
				}

				if(Mouse.getEventButton() == 1) {
					int x = Mouse.getX();
					int y = Mouse.getY();
					System.out.println("Right button pressed at X:"+x+" Y:"+y);
				}

				if(Mouse.getEventButton() == 2) {
					int x = Mouse.getX();
					int y = Mouse.getY();
					System.out.println("Scrollwheel button pressed at X:"+x+" Y:"+y);
				}                
			} else {               
				if(Mouse.getEventButton() == 0 ) {
					int x = Mouse.getX();
					int y = Mouse.getY();            
					System.out.println("Left button released at X:"+x+" Y:"+y);
				}

				if(Mouse.getEventButton() == 1) {
					int x = Mouse.getX();
					int y = Mouse.getY();
					System.out.println("Right button released at X:"+x+" Y:"+y);
				}

				if(Mouse.getEventButton() == 2) {
					int x = Mouse.getX();
					int y = Mouse.getY();
					System.out.println("Scrollwheel button released at X:"+x+" Y:"+y);
				}                
			}

			if(Mouse.hasWheel()) {
				int intMouseMovement = Mouse.getDWheel();

				if(intMouseMovement > 0)
					System.out.println("Mousewheel has been scrolled up.");

				if(intMouseMovement < 0) 
					System.out.println("Mousewheel has been scrolled down.");
			}

		}        
	}

	private final static void start() {
		try{
			Display.setDisplayMode(new DisplayMode(INT_WIDTH, INT_HEIGHT));
			Display.setTitle("Test");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		render();
		destroy();

	}

	

}
