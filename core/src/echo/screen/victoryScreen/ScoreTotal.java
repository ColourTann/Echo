package echo.screen.victoryScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

import echo.screen.gameScreen.ScoreKeeper;
import echo.screen.victoryScreen.Rank.RankName;
import echo.utilities.ButtonBorder;
import echo.utilities.Font;
import echo.utilities.Slider;

public class ScoreTotal extends Group{
	Sound ticker = Gdx.audio.newSound(Gdx.files.internal("sfx/ticker.wav"));
	float current;
	float total;
	String text;
	public ScoreTotal(float total) {
		setSize(220, 80);
		setPosition(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3, Align.center);
		this.total=total;
		System.out.println(total);
		ticker.play(Slider.SFX.getValue());
	}
	static int textX=20, textY=50;
	Interpolation terp = new Interpolation.SwingOut(20);
	float time=5f;
	float ticks;
	boolean ticked;
	@Override
	public void act(float delta) {
		super.act(delta);
		if(ticked)return;
		ticks+=delta/time;
		if(ticks>1){
			ticks=1;
			ticked=true;
			Rank r = new Rank(RankName.GM);
			VictoryScreen.self.addRank(r);
		}
		current=terp.apply(0, total, ticks);
		int ms = (int)((current)%1000)/10;
		int seconds = ((int)current/1000);
		int minutes = seconds/60;
		text=ScoreKeeper.pad(minutes, 2)+":"+ScoreKeeper.pad(seconds%60,2)+":"+ScoreKeeper.pad(ms, 2);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		Font.largeFont.draw(batch, "Total: "+text, getX()+textX, getY()+textY);
		super.draw(batch, parentAlpha);
	}
}
