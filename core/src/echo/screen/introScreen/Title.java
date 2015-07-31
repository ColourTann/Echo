package echo.screen.introScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.Main;
import echo.utilities.Draw;

public class Title extends Actor{
	static TextureRegion tr = Main.atlas.findRegion("title");
	public Title() {
		setColor(1,1,1,0);
		addAction(Actions.fadeIn(.3f));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(getColor());
		Draw.draw(batch, tr, 0, 0);
		super.draw(batch, parentAlpha);
	}
}
