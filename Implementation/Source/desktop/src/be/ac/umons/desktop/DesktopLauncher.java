package be.ac.umons.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.Random;

import be.ac.umons.HexaMaze;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 720;
		config.height = 480;
		config.title = HexaMaze.TITLE;
		config.samples = 4;
		new LwjglApplication(new HexaMaze(), config);
	}
}
