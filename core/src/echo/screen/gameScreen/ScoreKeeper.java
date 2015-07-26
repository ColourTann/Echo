package echo.screen.gameScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import echo.Main;
import echo.utilities.ButtonBorder;
import echo.utilities.Font;
import echo.utilities.TextRegion;

public class ScoreKeeper extends Group{
	private float currentTime;
	private int currentDeaths;
	private int currentHelp;
	private boolean active;
	
	public void activate(){
		active=true;
		reset();
		toFront();
		setSize(200, 150);
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
	public void draw(Batch batch, float parentAlpha) {
		if(!active)return;
		super.draw(batch, parentAlpha);
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		int ms = (int) ((currentTime*100)%100);
		int seconds =(int) (currentTime);
		int minutes = seconds/60;
		seconds %=60;
		String time = pad(minutes,2)+":"+pad(seconds,2)+":"+pad(ms,2);
		Font.font.draw(batch, "Time: "+time, getX()+10, getY()+50);
		Font.font.draw(batch, "Deaths: "+pad(currentDeaths,2), getX()+10, getY()+35);
		Font.font.draw(batch, "Fairy help: "+pad(currentHelp,2), getX()+10, getY()+20);
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
	
	private static String pad(int input, int digits){
		String output=input+"";
		while(output.length()<digits) output="0"+output;
		return output;
	}
	
}
