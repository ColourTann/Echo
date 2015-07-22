package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class InputBlocker extends Actor{
	private static InputBlocker self;
	public static InputBlocker get(){
		if(self==null)self=new InputBlocker();		
		return self;
	}
	private InputBlocker() {
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				event.stop();				
				return true;
			}
		});
	}

}
