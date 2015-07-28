package echo.entity;

import echo.entity.Entity.CollisionResult;

public interface CollisionHandler {
	public boolean checkCollision(Player p);
	public CollisionResult handCollision(Player p);

}
