package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ColiseumScreen implements Screen {
    final Roscodrom game;
    final Skin skin;
    final OrthographicCamera camera;
    Texture background;
    Stage stage;

    public ColiseumScreen(final Roscodrom game, final Skin skin, final OrthographicCamera camera) {
        this.game = game;
        this.game.font.getData().setScale(1.25f);
        this.skin = skin;
        this.camera = camera;
        this.camera.setToOrtho(false, 389, 800);
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        stage = new Stage(new FitViewport(389, 800, camera));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        TextButton returnBtn = new TextButton("<", skin);
        returnBtn.setPosition(20, 740);
        returnBtn.setHeight(40);
        returnBtn.setWidth(40);

        returnBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        stage.addActor(returnBtn);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.font.draw(game.batch, "Colisseu", 165, 400);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
