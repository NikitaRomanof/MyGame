package info.andr.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {
    SpriteBatch batch;
    BitmapFont font;
    int level;

    public Drop(int level) {
        super();
        this.level = level;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this, 0, level));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }

    public void nextLvl(int numberAction) {
        ++level;
        this.setScreen(new MainMenuScreen(this, numberAction, level));
    }
}
