package com.kw.mygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game{
	public SpriteBatch batch;
	public BitmapFont font;
	public Input input;
	
	@Override
	public void create() {
		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
		this.input = Gdx.input;
		
		this.setScreen( new MainMenuScreen( this ) );
	}

	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose(){
		batch.dispose();
		font.dispose();
	}
}
