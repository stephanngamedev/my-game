package com.kw.mygame;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop implements ApplicationListener{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Input input;
	
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Vector3 touchPos;
	
	private long lastDropTime;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho( false, 800, 480 );
		
		batch = new SpriteBatch();
		
		input = Gdx.input;
		
		dropImage = new Texture(Gdx.files.internal("img/droplet.png"));
		bucketImage = new Texture( Gdx.files.internal("img/bucket.png"));
		dropSound = Gdx.audio.newSound( Gdx.files.internal("sounds/water-drop.wav"));
		rainMusic = Gdx.audio.newMusic( Gdx.files.internal("sounds/rain.mp3"));
		
		bucket = new Rectangle();
		bucket.setPosition( 800 / 2 - 64 / 2, 20 );
		bucket.setSize(64, 64 );
		
		raindrops = new Array<Rectangle>();
		
		touchPos = new Vector3();
		
		rainMusic.setLooping( true );
		rainMusic.play();
		
		spawnRainDrop();
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
	public void update(){
		if( input.isTouched() ){
			touchPos.set( input.getX(), input.getY(), 0 );
			camera.unproject( touchPos );
			bucket.x = touchPos.x - 64 / 2; 
		}
		
		if( TimeUtils.nanoTime() - lastDropTime > 1000000000 ){
			spawnRainDrop();
		}
		
		moveRainDrops();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor( 0, 0, 0.2f, 1 );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		
		camera.update();
		
		update();
		
		batch.setProjectionMatrix( camera.combined );
		batch.begin();
		batch.draw( bucketImage, bucket.x, bucket.y );
		for( Rectangle raindrop : raindrops ){
			batch.draw( dropImage, raindrop.x, raindrop.y );
		}
		batch.end();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
	
	private void spawnRainDrop(){
		Rectangle raindrop = new Rectangle();
		raindrop.setPosition( MathUtils.random( 0, 800 - 64), 480 );
		raindrop.setSize( 64, 64 );
		raindrops.add( raindrop );
		lastDropTime = TimeUtils.nanoTime();
	}
	
	private void moveRainDrops(){
		Iterator<Rectangle> it = raindrops.iterator();
		while( it.hasNext() ){
			Rectangle raindrop = it.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if( raindrop.y + 64 < 0){
				it.remove();
			} else if( raindrop.overlaps( bucket ) ){
				dropSound.play();
				it.remove();
			}
			
		}
	}
 
}
