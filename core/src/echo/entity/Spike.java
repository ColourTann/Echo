package echo.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import echo.Main;
import echo.map.Tile;
import echo.utilities.Draw;

public class Spike extends Entity{
	TextureRegion tr= Main.atlas.findRegion("entity/spike");
	public Spike(int x, int y) {
		setSize(Tile.tileWidth, Tile.visibleHeight);
		setPosition(x*Tile.tileWidth, y*Tile.tileHeight);
		setupCollider();
	}

	@Override
	public CollisionResult collideWithPlayer(Player p) {
		return CollisionResult.Death;
	}

	@Override
	public void reset() {
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		Draw.draw(batch,tr, getX(), getY());
	}

	@Override
	public void begin() {
	}

	@Override
	public void drawLights(Batch batch) {
	}

}
