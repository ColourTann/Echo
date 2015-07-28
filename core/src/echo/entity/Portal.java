package echo.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import echo.Main;
import echo.map.Tile;
import echo.utilities.Draw;



public class Portal extends Entity{
	
	static final float animationSpeed=6;
	static TextureRegion[] animation;
	static{
		TextureRegion tr= Main.atlas.findRegion("entity/portal");
		animation = tr.split(tr.getRegionWidth()/4, tr.getRegionHeight())[0];
	}
	
	public Portal(int x, int y){
		setSize(Tile.tileWidth, animation[0].getRegionHeight());
		setPosition(x*Tile.tileWidth, y*Tile.tileHeight-Player.extraHeight);
		setupCollider();
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		Draw.drawCentered(batch, animation[(int) (ticker*animationSpeed)%animation.length], getX()+getWidth()/2, getY()+getHeight()/2);
	}

	@Override
	public CollisionResult collideWithPlayer(Player p) {
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
		 Draw.drawCenteredScaled(batch, getMask(), getX()+getWidth()/2, Gdx.graphics.getHeight()-getY()-getHeight()/2,1,1);
	}

	@Override
	public boolean checkCollision(Player p) {
		return false;
	}

	@Override
	public CollisionResult handCollision(Player p) {
		return CollisionResult.Glory;
	}


	
}
