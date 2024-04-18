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

public class MainMenuScreen implements Screen {
	final Roscodrom game;
	OrthographicCamera camera;
	Texture background;
	Stage stage;
	Skin skin;

	public MainMenuScreen(final Roscodrom game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("backgroundMenu.jpg"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 450, 800);
		stage = new Stage(new FitViewport(450, 800, camera));
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("uiskin.json"));
	}

	@Override
	public void show() {
		TextButton singleplayerBtn = new TextButton("Partida Individual", skin);
		singleplayerBtn.setPosition(125, 650);
		singleplayerBtn.setHeight(75);
		singleplayerBtn.setWidth(200);

		TextButton multiplayerBtn = new TextButton("Partida Multijugador", skin);
		multiplayerBtn.setPosition(125, 500);
		multiplayerBtn.setHeight(75);
		multiplayerBtn.setWidth(200);

		TextButton coliseumBtn = new TextButton("Colisseu", skin);
		coliseumBtn.setPosition(125, 350);
		coliseumBtn.setHeight(75);
		coliseumBtn.setWidth(200);

		TextButton optionsBtn = new TextButton("Opcions", skin);
		optionsBtn.setPosition(125, 200);
		optionsBtn.setHeight(75);
		optionsBtn.setWidth(200);

		TextButton profileBtn = new TextButton("Perfil", skin);
		profileBtn.setPosition(125, 50);
		profileBtn.setHeight(75);
		profileBtn.setWidth(200);

		singleplayerBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new SinglePlayerScreen(game));
            	dispose();
			}
		});

		multiplayerBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MultiPlayerScreen(game));
				dispose();
			}
		});

		coliseumBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new ColiseumScreen(game));
				dispose();
			}
		});

		optionsBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new OptionsScreen(game));
				dispose();
			}
		});

		profileBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new ProfileScreen(game));
				dispose();
			}
		});

		stage.addActor(singleplayerBtn);
		stage.addActor(multiplayerBtn);
		stage.addActor(coliseumBtn);
		stage.addActor(optionsBtn);
		stage.addActor(profileBtn);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(background, 0, 0);
		game.batch.end();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
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
