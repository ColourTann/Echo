package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
	
	//ENEMIES//
	public static final Sound[] beeMove = new Sound[]{makeSound("sfx/beemove0.wav"), makeSound("sfx/beemove1.wav")};
	
	//PLAYER//
	public static final Sound dead = makeSound("sfx/dead.wav");
	public static final Sound win = makeSound("sfx/win.wav");
	public static final Sound[] jumpSound = new Sound[4];
	public static final Sound[] landSound = new Sound[3];
	public static final Sound[] wallSound = new Sound[1];
	static{
		for(int i=0;i<=0;i++){wallSound[i]=makeSound("sfx/wall"+i+".wav");}
		for(int i=0;i<=2;i++){landSound[i]=makeSound("sfx/land"+i+".wav");}
		for(int i=0;i<=3;i++){jumpSound[i]=makeSound("sfx/jump"+i+".wav");}
	}

	
	public static Sound spikeSound = makeSound("sfx/bramble.wav");
	
	
	public static Sound makeSound(String path){
		Sound s = Gdx.audio.newSound(Gdx.files.internal(path));
		s.play(0);
		return s;
	}
}
