package info.andr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {

    final Drop game;
    OrthographicCamera camera;
    int status;
    int level;

    public MainMenuScreen(Drop gam, int status, int level) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        this.status = status;
        this.level = level;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if(status == 0) {
            game.font.setColor(0, 1, 0, 1);
            game.font.draw(game.batch, "Catch all the drops", 340, 240);
            game.font.setColor(0, 1, 1, 1);
            game.font.draw(game.batch, "Touch the screen", 350, 200);
        } else if(status == 1) {
            game.font.setColor(1, 0, 1, 1);
            game.font.draw(game.batch, "Congratulations level completed!", 300, 240);
            game.font.setColor(0, 1, 1, 1);
            game.font.draw(game.batch, "Touch the screen", 350, 200);
        } else if(status == 2) {
            game.font.setColor(1, 0, 1, 1);
            game.font.draw(game.batch, "WIN THE GAME!", 350, 220);
        } else if(status == 3) {
            game.font.setColor(1, 0, 0, 1);
            game.font.draw(game.batch, "GAME OVER!", 350, 220);
        }

        if(Gdx.input.isTouched() && status < 2) {
            game.setScreen(new MyGame(game, level));
            dispose();
        }
        game.batch.end();

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

    @Override
    public void dispose() {

    }
}
