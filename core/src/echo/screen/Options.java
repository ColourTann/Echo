package echo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.utilities.Draw;

public class Options extends Group{
	static int w=300, h=200;
	public Options() {
		setSize(w, h);
		setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Align.center);
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(.1f,.5f,.5f,1);
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
	}
	
}
