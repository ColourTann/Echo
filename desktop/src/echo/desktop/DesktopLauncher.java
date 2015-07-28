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
		config.resizable=false;
		config.vSyncEnabled=true;
		config.foregroundFPS=60;
		config.width=800;
		config.height=640;
		
		Settings settings = new Settings();
		settings.combineSubdirectories = true;
		TexturePacker.process(settings, "../images", "../core/assets", "atlas_image");
		config.title="Echo";
		config.addIcon("icon.png", FileType.Internal);

		
		new LwjglApplication(new Main(), config);
	}
}
