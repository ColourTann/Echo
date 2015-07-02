package echo.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import echo.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.width=200;
//		config.height=160;
		config.resizable=false;
		Settings settings = new Settings();
		config.vSyncEnabled=false;
		config.foregroundFPS=60;
		settings.combineSubdirectories = true;
		TexturePacker.process(settings, "../images",
				"../core/assets", "atlas_image");
		
		//config.addIcon("shiptiny.png", FileType.Internal);

		
		new LwjglApplication(new Main(), config);
	}
}
