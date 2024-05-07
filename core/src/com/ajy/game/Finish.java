package com.ajy.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ajy.game.MainMenuScreen;
import com.ajy.game.Roscodrom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
    
public class Finish implements Screen {
    final Roscodrom game;
    final Skin skin;
    final OrthographicCamera camera;
    Texture background;
    Stage stage;
    MyListView listViewScore;
    Map<String,Integer> Ranking;

    public Finish(final Roscodrom game, final Skin skin, final OrthographicCamera camera, Map<String,Integer> ranking) {
        this.game = game;
        this.game.font.getData().setScale(1.25f);
        this.skin = skin;
        this.camera = camera;
        this.camera.setToOrtho(false, 389, 800);
        this.Ranking = ranking;
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        stage = new Stage(new FitViewport(389, 800, camera));
        Gdx.input.setInputProcessor(stage);
        //WEBSOCKET RECIBIR LA PUNTUACION
        //Ranking
        List<Map.Entry<String, Integer>> list = new ArrayList<>(ranking.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Ahora 'list' contiene las entradas del mapa ordenadas según los valores en orden descendente

        listViewScore = new MyListView(skin);
        for (Map.Entry<String, Integer> entry : list) {
            listViewScore.addItem(entry.getKey() + ": " + entry.getValue());
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        listViewScore = new MyListView(skin);
        for (Map.Entry<String, Integer> entry : ranking.entrySet()) {
            listViewScore.addItem(entry.getKey() + ": " + entry.getValue());
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        stage.addActor(listViewScore.getScrollPane());
        listViewScore.getScrollPane().setPosition(150, 500);
    }

    @Override
    public void show() {
        //Cambiar el return Centrado
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
        game.font.draw(game.batch, "Partida ha terminado", 130, 400);
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

class MyListView {
    private Skin skin;
    private Table table;
    private ScrollPane scrollPane;
    private ArrayList<String> items;

    public MyListView(Skin skin) {
        this.skin = skin;
        this.items = new ArrayList<>();
        table = new Table();
        scrollPane = new ScrollPane(table, skin);
    }

    public void addItem(String item) {
        items.add(item);
        updateListView();
    }

    public void removeItem(String item) {
        items.remove(item);
        updateListView();
    }

    private void updateListView() {
        table.clear();
        for (String item : items) {
            table.add(new Label(item, skin)).expandX().fillX().pad(10);
            table.row();
        }
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
