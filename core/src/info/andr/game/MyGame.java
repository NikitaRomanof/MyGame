package info.andr.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

public class MyGame implements Screen {
	final Drop game;
	OrthographicCamera camera;
	Texture bucketImg;
	Texture dropImg;
	Sound dropSound;
	Music rainMusic;
	Rectangle bucket;
	Vector3 touchPos;
	Array<Rectangle> raindrops;
	long lastDropTime;
	long winsCount;
	long loseCount;
	int level;

	public MyGame (Drop gam, int level) {
		this.game = gam;
		this.level = level;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		touchPos = new Vector3();
		initTexture();
		initSound();
		initRectangle();
		raindrops = new Array<>();
		spawnRaindrop();
	}

	private void initTexture() {
		bucketImg = new Texture("bucket.png");
		dropImg = new Texture("droplet.png");
	}


	private void initSound() {
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));
		rainMusic.setLooping(true);
		rainMusic.play();
	}
	private void initRectangle() {
		bucket = new Rectangle();
		bucket.x = 800.0f / 2 - 64.0f / 2;
		bucket.y = 20.0f;
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
	public void render (float delta) {

			Gdx.gl.glClearColor(0, 0, 0.2f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			camera.update();
			game.batch.setProjectionMatrix(camera.combined);
		    game.batch.begin();
		    game.font.setColor(0, 1, 0, 1);
		    game.font.draw(game.batch, "Drops Collected: " + winsCount, 10, 460);
		    game.font.setColor(1, 0, 0, 1);
		    game.font.draw(game.batch, "Drops Missed : " + loseCount, 650, 460);
		    game.font.setColor(1, 0, 1, 1);
		    game.font.draw(game.batch, "LEVEL : " + level, 350, 460);
			game.batch.draw(bucketImg, bucket.x, bucket.y);

			for (Rectangle raindrop : raindrops) {
			    game.batch.draw(dropImg, raindrop.x, raindrop.y);
			}

		    game.batch.end();
			levelUp();
			rainDropLogic();
	}

	private void levelUp() {
		if (loseCount >= 5) {
			rainMusic.stop();
			game.nextLvl(3);
		}
		if (level >= 7 && winsCount == 30) {
			rainMusic.stop();
			game.nextLvl(2);
		} else if (winsCount == 30) {
			rainMusic.stop();
			game.nextLvl(1);
			++level;
			winsCount = 0;
			loseCount = 0;
		}
	}

	private void rainDropLogic() {
			controlTouchAndButton();
			if (TimeUtils.nanoTime() - lastDropTime > 600000000) spawnRaindrop();
			Iterator<Rectangle> iter = raindrops.iterator();
			while (iter.hasNext()) {
				Rectangle raindrop = iter.next();
				raindrop.y -= 200 * Gdx.graphics.getDeltaTime() * level;

				if(raindrop.overlaps(bucket)) {
					dropSound.play();
					iter.remove();
					++winsCount;
				} else if (!raindrop.overlaps(bucket) && raindrop.y < 0) {
					++loseCount;
					if(raindrops.size > 0) {
						iter.remove();
					}
				}

				if (loseCount == 5 || winsCount == 30) {
					break;
				}
			}
	}
	
	@Override
	public void dispose () {
		bucketImg.dispose();
		dropImg.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		game.batch.dispose();
		game.dispose();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {

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
}
