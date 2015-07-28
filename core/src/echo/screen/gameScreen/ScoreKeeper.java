package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.utilities.ButtonBorder;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.TextRegion;

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
		int ms = (int) ((currentTime*1000)%1000)/10;
		int seconds =(int) (currentTime);
		int minutes = seconds/60;
		seconds %=60;
		String timeString = pad(minutes,2)+":"+pad(seconds,2)+":"+pad(ms,2);
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		float xOffset=15;
		float yOffset=10;
		float yTextOffset=yOffset+14;
		float xTextOffset=xOffset+25;
		float yGap=(getHeight()-yOffset*2)/3;
		Draw.draw(batch, time, getX()+xOffset, getY()+yOffset+yGap*2);
		Font.font.draw(batch, timeString, getX()+xTextOffset, getY()+yTextOffset+yGap*2);
		Draw.draw(batch, death, getX()+xOffset, getY()+yOffset+yGap*1);
		Font.font.draw(batch, pad(currentDeaths,2)+"", getX()+xTextOffset, getY()+yTextOffset+yGap*1);
		Draw.draw(batch, help, getX()+xOffset, getY()+yOffset+yGap*0);
		Font.font.draw(batch, pad(currentHelp,2)+"", getX()+xTextOffset, getY()+yTextOffset+yGap*0);
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

	public static String pad(int input, int digits){
		String output=input+"";
		while(output.length()<digits) output="0"+output;
		return output;
	}

}
