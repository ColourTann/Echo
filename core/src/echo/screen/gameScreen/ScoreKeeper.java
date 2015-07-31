package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import echo.Main;
import echo.utilities.ButtonBorder;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.TimeStuff;

public class ScoreKeeper extends Group{
	public float currentTime;
	public int currentDeaths;
	public int currentHelp;
	public boolean active;

	public static final int w=110;
	public static final int h=90;
	
	public static TextureRegion time = Main.atlas.findRegion("icon/time");
	public static TextureRegion death = Main.atlas.findRegion("icon/deaths");
	public static TextureRegion help = Main.atlas.findRegion("icon/help");
	public ScoreKeeper() {
		setSize(w, h);
		setPosition(Gdx.graphics.getWidth()-getWidth(), Gdx.graphics.getHeight()-getHeight());
	}



	public void activate(){
		active=true;
		reset();
		toFront();
	}

	public void deactivate(){
		active=false;
		reset();
	}

	private void reset() {
		oldTime=0;oldDeaths=0;complete=false;
		currentTime=0; currentDeaths=0; currentHelp=0;
	}

	public void addTimer(float f){
		currentTime+=f;
	}

	public void addDeath(){
		currentDeaths++;
	}

	public void addHelp(){
		currentHelp++;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		float xOffset=15;
		float yOffset=10;
		float yTextOffset=yOffset+14;
		float xTextOffset=xOffset+25;
		float yGap=(getHeight()-yOffset*2)/3;
		
		Draw.draw(batch, time, getX()+xOffset, getY()+yOffset+yGap*2);
		Font.font.draw(batch, TimeStuff.timeString(currentTime), getX()+xTextOffset, getY()+yTextOffset+yGap*2);
		Draw.draw(batch, death, getX()+xOffset, getY()+yOffset+yGap*1);
		Font.font.draw(batch, TimeStuff.pad(currentDeaths,2)+"", getX()+xTextOffset, getY()+yTextOffset+yGap*1);
		Draw.draw(batch, help, getX()+xOffset, getY()+yOffset+yGap*0);
		Font.font.draw(batch, TimeStuff.pad(currentHelp,2)+"", getX()+xTextOffset, getY()+yTextOffset+yGap*0);
		if(!active)return;
		super.draw(batch, parentAlpha);
	}



	private float oldTime;
	private int oldDeaths;
	private boolean complete;
	public void nextLevel(){
		complete=false;
		oldTime = currentTime;
		oldDeaths=currentDeaths;
	}

	public void complete(){
		complete=true;
	}

	

}
