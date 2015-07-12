package echo.entity;

import com.badlogic.gdx.graphics.g2d.Batch;

import echo.map.Tile;
import echo.utilities.Draw;

public class Spike extends Entity{

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
		Draw.draw(batch,Tile.tileTextures[7], getX(), getY());
	}

	@Override
	public void begin() {
	}

	@Override
	public void drawLights(Batch batch) {
	}

}
