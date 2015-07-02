package echo;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.utilities.Draw;


public class Square extends Actor{
	static AtlasRegion test = Main.atlas.findRegion("testimage");
	public Square() {
		setSize(test.getRegionWidth(), test.getRegionHeight());
		setX((float)Math.random()*(Main.width-getWidth()));
		setY((float)Math.random()*(Main.height-getHeight()));
		setOrigin(getWidth()/2, getHeight()/2);
//		setSize((float)Math.random()*50, (float)Math.random()*50);
		
		addListener(new InputListener() {
			
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setColor(Color.GREEN);
				addAction(Actions.rotateBy((float) (Math.random()-.5f)*400, 1, Interpolation.swingOut));
				return true;
			}
			
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				setColor(Color.WHITE);
				
			}
		});
	}
	float speed;
	public void act(float delta){
		super.act(delta);
		float xSpeed=(float) ((Math.random()-.5f)*100);
		float ySpeed=(float) ((Math.random()-.5f)*100);
		setX(getX()+delta*xSpeed);
		setY(getY()+delta*ySpeed);
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(getColor());
		Draw.drawCenteredRotatedScaled(batch, test, getX()+getWidth()/2, getY()+getHeight()/2, 1, 1, (float)Math.toRadians(getRotation()));
//		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
	}
}
