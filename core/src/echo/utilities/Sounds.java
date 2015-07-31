package echo.utilities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import echo.map.Map.TerrainType;

public class Sounds {
	
	public static AssetManager am= new AssetManager();
	
	
	/*
	 * public enum TerrainType{background, player, goal, base, snow, stone, grass, metal, water, beeRight, beeDown, spike;
	Sound[] foot = new Sound[2];
	TerrainType(){
		for(int i=0;i<2;i++){
			String s ="sfx/"+this+"foot"+i+".wav";
			FileHandle f = Gdx.files.internal(s);
			if(!f.exists()) break;
			foot[i]=Sounds.makeSound(s);
		}
	}
	 */
	
	public static void setup(){
		// sfx //
		makeSound("sfx/ticker.wav", Sound.class);
		makeSound("sfx/bell.wav", Sound.class);
		makeSound("sfx/win.wav", Sound.class);
		makeSound("sfx/dead.wav", Sound.class);
		makeSound("sfx/bramble.wav", Sound.class);
		makeSound("sfx/wall.wav",Sound.class);
		for(int i=0;i<=1;i++){makeSound("sfx/beemove"+i+".wav",Sound.class);}
		for(int i=0;i<=2;i++){makeSound("sfx/land"+i+".wav",Sound.class);}
		for(int i=0;i<=3;i++){makeSound("sfx/jump"+i+".wav",Sound.class);}
		for(TerrainType t:TerrainType.values()){
			for(int i=0;i<=1;i++){
			
				String s ="sfx/"+t+"foot"+i+".wav";
				FileHandle f = Gdx.files.internal(s);
				if(!f.exists()) continue;
				makeSound(s, Sound.class);
			}
		}

		
		// music //
		makeSound("sfx/intro.ogg", Music.class);
		makeSound("sfx/ambience.ogg", Music.class);
		
		am.finishLoading();
		Array<Sound> sounds = new Array<Sound>();
		am.getAll(Sound.class, sounds);
		for(Sound s:sounds)s.play(0);
		
		Array<Music> musics = new Array<Music>();
		am.getAll(Music.class, musics);
		for(Music m:musics){
			m.play();
			m.setVolume(0);
		}
		
		
	}
	
	public static void makeSound(String path, Class type){
		am.load(path, type);
		
	}
}
