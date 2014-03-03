package com.kw.mygame;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen{
	private final Drop game;
	private OrthographicCamera camera;
	
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Vector3 touchPos;
	
	private long lastDropTime;
	private int dropsGathered;
	
	public GameScreen( final Drop game ) {
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho( false, 800, 480 );
		
		dropImage = new Texture(Gdx.files.internal("img/droplet.png"));
		bucketImage = new Texture( Gdx.files.internal("img/bucket.png"));
		dropSound = Gdx.audio.newSound( Gdx.files.internal("sounds/water-drop.wav"));
		rainMusic = Gdx.audio.newMusic( Gdx.files.internal("sounds/rain.mp3"));
		
		bucket = new Rectangle();
		bucket.setPosition( 800 / 2 - 64 / 2, 20 );
		bucket.setSize(64, 64 );
		
		raindrops = new Array<Rectangle>();
		
		touchPos = new Vector3();
		
		spawnRainDrop();
	}
	
	@Override
	public void show() {
		rainMusic.setLooping( true );
		rainMusic.play();
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
	public void update( float delta ){
		if( game.input.isTouched() ){
			touchPos.set( game.input.getX(), game.input.getY(), 0 );
			camera.unproject( touchPos );
			bucket.x = touchPos.x - 64 / 2; 
		}
		
		if( TimeUtils.nanoTime() - lastDropTime > 1000000000 ){
			spawnRainDrop();
		}
		
		moveRainDrops();
	}

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor( 0, 0, 0.2f, 1 );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		
		camera.update();
		
		update( delta );
		
		game.batch.setProjectionMatrix( camera.combined );
		game.batch.begin();
		game.batch.draw( bucketImage, bucket.x, bucket.y );
		for( Rectangle raindrop : raindrops ){
			game.batch.draw( dropImage, raindrop.x, raindrop.y );
		}
		game.font.draw( game.batch, "Drops gathered:" + dropsGathered, 0, 480 );
		game.batch.end();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}
	
	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
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
				dropsGathered++;
			}
			
		}
	}
}
