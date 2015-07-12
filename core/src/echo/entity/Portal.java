package echo.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.Main;
import echo.map.Tile;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Draw.BlendType;



public class Portal extends Entity{
	static final Sound punched = Gdx.audio.newSound(Gdx.files.internal("sfx/frogpunched.wav"));
	static final float animationSpeed=6;
	static TextureRegion[] animation;
	static{
		TextureRegion tr= Main.atlas.findRegion("entity/portal");
		animation = tr.split(tr.getRegionWidth()/4, tr.getRegionHeight())[0];
	}
	
	public Portal(int x, int y){
		setSize(animation[0].getRegionWidth(), animation[0].getRegionHeight());
		setPosition(x*Tile.tileWidth, y*Tile.tileHeight-Player.extraHeight);
		setupCollider();
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		Draw.draw(batch, animation[(int) (ticker*animationSpeed)%animation.length], getX(), getY());
	}

	@Override
	public CollisionResult collideWithPlayer(Player p) {
		punched.play();
		return CollisionResult.Glory;
	}

	@Override
	public void reset() {
	}

	@Override
	public void begin() {
	}

	
	
	@Override
	public void drawLights(Batch batch) {
		 Draw.drawCenteredScaled(batch, getMask(), getX()+getWidth()/2, Main.height-getY()-getHeight()/2,1,1);
	}


	
}
