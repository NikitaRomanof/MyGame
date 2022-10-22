package info.andr.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

public class MyGame extends ApplicationAdapter {
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture bucketImg;
	Texture dropImg;
	Sound dropSound;
	Music rainMusic;
	Rectangle bucket;
	Vector3 touchPos;
	Array<Rectangle> raindrops;
	long lastDropTime;
	BitmapFont win;
	BitmapFont lose;
	long winsCount;
	long loseCount;
	int level = 1;
	int stop = 0;
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		touchPos = new Vector3();
		initTexture();
		initSound();
		initRectangle();
		initTextCountWinLose();
		raindrops = new Array<>();
		spawnRaindrop();
	}
	private void initTexture() {
		bucketImg = new Texture("bucket.png");
		dropImg = new Texture("droplet.png");
	}

	private void initTextCountWinLose() {
		win = new BitmapFont();
		lose = new BitmapFont();
		win.setColor(Color.GREEN);
		lose.setColor(Color.RED);
	}

	private void initSound() {
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));
		rainMusic.setLooping(true);
		rainMusic.play();
	}
	private void initRectangle() {
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void controlTouchAndButton() {
		if(Gdx.input.isTouched()) {

			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = (int) (touchPos.x - 64 / 2);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime() * 4;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime() * 4;

		if(bucket.x <= 0) bucket.x = 0;
		if(bucket.x >= 800 - 64) bucket.x = 800 - 64;
	}

	@Override
	public void render () {
		if (stop == 0) {
			Gdx.gl.glClearColor(0, 0, 0.2f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			camera.update();
			batch.setProjectionMatrix(camera.combined);

			batch.begin();
			batch.draw(bucketImg, bucket.x, bucket.y);

			for (Rectangle raindrop : raindrops) {
				batch.draw(dropImg, raindrop.x, raindrop.y);
			}

			if (loseCount <= 10) {
				if (winsCount == 30) {
					win.draw(batch, "Congratulations!\n Level " + level + " completed!", 350, 240);
					stop = 150;
				} else {
					win.draw(batch, Long.toString(winsCount), 20, 470);
					lose.draw(batch, Long.toString(loseCount), 780, 470);
				}

			} else {
				lose.draw(batch, "GAME OVER!", 350, 240);
				rainMusic.stop();
			}

			if (level == 5) {
				win.draw(batch, "Congratulations! Yor WIN!", 350, 240);
				rainMusic.stop();
			}

			batch.end();
			levelUp();
			rainDropLogic();
		} else {
			--stop;
		}

	}

	private void levelUp() {
		if (winsCount == 30) {
			++level;
			winsCount = 0;
			loseCount = 0;
		}
	}

	private void rainDropLogic() {
		if (loseCount <= 10 && level < 5) {
			controlTouchAndButton();
			if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
			Iterator<Rectangle> iter = raindrops.iterator();
			while (iter.hasNext()) {
				Rectangle raindrop = iter.next();
				raindrop.y -= 200 * Gdx.graphics.getDeltaTime() * level;
				if (raindrop.y + 64 < 0) iter.remove();
				if(raindrop.overlaps(bucket)) {
					dropSound.play();
					iter.remove();
					++winsCount;
				}
				if(raindrop.y <= 0) {
					++loseCount;
					iter.remove();
				}
				if (loseCount == 10 || winsCount == 30) {
					break;
				}
			}
		}
	}
	
	@Override
	public void dispose () {
		super.dispose();
		bucketImg.dispose();
		dropImg.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		win.dispose();
		lose.dispose();
		batch.dispose();

	}
}
