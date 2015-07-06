package echo.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.map.Tile;
import echo.utilities.Draw;



public class Frog extends Entity{
	static final Sound punched = Gdx.audio.newSound(Gdx.files.internal("sfx/frogpunched.wav"));
	static TextureRegion[] animation;
	static{
		Texture t= new Texture(Gdx.files.internal("entity/frog.png"));
		animation = TextureRegion.split(t, t.getWidth()/2, t.getHeight())[0];
	}
	
	public Frog(int x, int y){
		setSize(animation[0].getRegionWidth(), animation[0].getRegionHeight());
		setPosition(x*Tile.tileWidth, y*Tile.tileHeight-Player.extraHeight);
		setupCollider();
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		Draw.draw(batch, animation[0], getX(), getY());
	}

	@Override
	public void collideWithPlayer(Player p) {
		punched.play();
		p.win();
	}

	@Override
	public void reset() {
	}
	
}
