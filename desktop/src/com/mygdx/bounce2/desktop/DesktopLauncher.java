package com.mygdx.bounce2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.bounce2.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Bounce";
		config.resizable = false;
		config.width = 300;
		config.height = 600;
		new LwjglApplication(new Main(), config);
	}
}
