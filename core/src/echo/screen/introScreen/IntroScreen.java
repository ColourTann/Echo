package echo.screen.introScreen;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import echo.Main;
import echo.entity.Entity;
import echo.entity.Fairy;
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Slider;
import echo.utilities.Sounds;
import echo.utilities.TannScreen;

public class IntroScreen extends TannScreen{
	public static Music intro;
	static TextureRegion logoPic=Main.atlas.findRegion("sagdclogo");
	static Random r = new Random(1);
	ArrayList<Fairy> entities = new ArrayList<Fairy>();
	public IntroScreen() {
		intro = Sounds.am.get("sfx/intro.ogg", Music.class);
		init();
		intro.play();
		intro.setVolume(Slider.music.getValue());
	}

	@Override
	public void keyPressed(int keyCode) {
		switch(keyCode){
		case Keys.SPACE:
			
			break;
		}
		skip();
	}

	private void lightsIntoBuffer(Batch batch){
		Draw.beginBuffer(batch);
		batch.setColor(0,0,0,1);
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		Gdx.gl20.glBlendEquation(GL30.GL_FUNC_REVERSE_SUBTRACT);
		batch.setColor(0,0,0,1);
		for(Entity e:entities){
			e.drawLights(batch);
		}
		Draw.endBuffer(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glBlendEquation(GL30.GL_FUNC_ADD);
	}
	
	@Override
	public void draw(float delta) {
		batch.begin();
		batch.setColor(Colours.darkBlueGreen);
		
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setColor(1,1,1,1);
		Draw.drawCentered(batch, logoPic, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		batch.end();
		lightsIntoBuffer(batch);
		batch.begin();
		batch.setColor(1,1,1,1);
		Draw.draw(batch, Draw.getBuffer().getColorBufferTexture(), 0, 0);
		batch.end();
		stage.draw();
	}

	static float fairyRate=1.2f;
	float fairyTicks =0;
	@Override
	public void update(float delta) {
		stage.act(delta);
		if(titleScreen)return;
		if(fairyRate<-.5f){
			skip();
		}
		fairyTicks-=delta;
		fairyRate-=delta*.2f;
		if(fairyRate<=0)return;
		if(fairyTicks<=0){
			addFairy();
		}
		Fairy.setBrightness(3-fairyRate*2);
	}

	private void addFairy(){
		Fairy f = new Fairy();
		boolean offHorizontal=r.nextFloat()>.5;
		float startX, startY;
		if(offHorizontal){
			startX=(r.nextFloat()>.5)?-150:Gdx.graphics.getWidth()+150;
			startY=(float) (r.nextFloat()*Gdx.graphics.getHeight());
		}
		else{
			startX=(float) (r.nextFloat()*Gdx.graphics.getWidth());
			startY=(r.nextFloat()>.5)?-150:Gdx.graphics.getHeight()+150;
		}
		f.setStart(startX, startY);
		
		f.flyTo(Gdx.graphics.getWidth()-startX, Gdx.graphics.getHeight()-startY);
		entities.add(f);
		stage.addActor(f);
		
		for(int i = entities.size()-1;i>=0;i--){
			f= entities.get(i);
			if(f.dead){
				f.remove();
				entities.remove(f);
			}
		}
		fairyTicks=(float) (r.nextFloat()*fairyRate);
	}
	
	
	boolean skipped;
	boolean titleScreen;
	public void skip(){
		if(skipped)return;
		if(!titleScreen){
			intro.stop();
			Main.startMusic();
			for(Fairy f:entities)f.remove();
			titleScreen=true;
			stage.addActor(new Title());
			return;
		}
		
		skipped=true;
		Main.self.setScreen(GameScreen.get(), TransitionType.SlideLeft);
		GameScreen.scoreKeeper.activate();
	}

	@Override
	public void activate() {
	}

	@Override
	public boolean handleEsc() {
		skip();
		return true;
	}

	@Override
	public void deactivate() {
	}
}



