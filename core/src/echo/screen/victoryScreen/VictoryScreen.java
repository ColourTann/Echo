package echo.screen.victoryScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;

import echo.screen.gameScreen.GameScreen;
import echo.screen.gameScreen.ScoreKeeper;
import echo.screen.victoryScreen.ScorePart.PartType;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.TannScreen;

public class VictoryScreen extends TannScreen{
	public static VictoryScreen self;
	ScoreTotal total;
	ScorePart[] parts = new ScorePart[3];
	float totalScore;
	public VictoryScreen() {
		init();
		self=this;
		ScoreKeeper s = GameScreen.scoreKeeper;
		parts[0]=new ScorePart((int)(s.currentTime*1000+150000), 1, ScoreKeeper.time, PartType.Time);
		parts[1]=new ScorePart(s.currentDeaths+15, 1, ScoreKeeper.death, PartType.Death);
		parts[2]=new ScorePart(s.currentHelp+2, 1, ScoreKeeper.help, PartType.Help);
		int partY=400;
		int totalWidth=0;
		for(int i=0;i<3;i++){	
			totalWidth+=parts[i].getWidth();
		}
		int gap = (Gdx.graphics.getWidth()-totalWidth)/4;
		int currentX=gap;
		for(int i=0;i<3;i++){
			ScorePart sp = parts[i];
			stage.addActor(sp);
			sp.setPosition(currentX, partY);
			currentX+=gap+sp.getWidth();
			totalScore+=sp.getValue();
			System.out.println(totalScore);
		}
		total=new ScoreTotal(totalScore);
		stage.addActor(total);
	}
	
	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public boolean handleEsc() {
		return false;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	protected void draw(float delta) {
		stage.draw();
	}

	@Override
	public void update(float delta) {
		batch.begin();
		batch.setColor(Colours.darkBlueGreen);
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		stage.act();
	}

	public void addRank(Rank r) {
		r.setPosition(Gdx.graphics.getWidth()/3*2, total.getY()+total.getHeight()/2, Align.center);
		stage.addActor(r);
	}
}
