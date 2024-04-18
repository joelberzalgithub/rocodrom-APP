package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class SinglePlayerScreen implements Screen {
    final Roscodrom game;
    OrthographicCamera camera;
    Texture background;

    public SinglePlayerScreen(final Roscodrom game) {
        this.game = game;
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 389, 800);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.font.draw(game.batch, "Partida individual", 140, 400);
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
        background.dispose();
    }
}
