package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MultiPlayerScreen implements Screen {
    final Roscodrom game;
    Socket socket;
    private BitmapFont font;
    final Skin skin;
    final OrthographicCamera camera;
    float elapsed;
    Animation<TextureRegion> loading;
    Texture background;
    Stage stage;

    public MultiPlayerScreen(final Roscodrom game, final Skin skin, final OrthographicCamera camera) {
        this.game = game;
        this.game.font.getData().setScale(1.25f);
        this.skin = skin;
        this.camera = camera;
        this.camera.setToOrtho(false, 389, 800);
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        font = new BitmapFont();
        stage = new Stage(new FitViewport(389, 800, camera));
        Gdx.input.setInputProcessor(stage);
        String URL="https://roscodrom5.ieti.site";
        String message="{ nickname: 'nick', apiKey: 'djhiasdhasd-asdasf-adgfagfas234'}";
        createCommunications(URL);
        socket.emit("Join",message );
        loading = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("loading2.gif").read());
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
                Map<String, Integer> ranking = new HashMap<>();
                ranking.put("Juan", 5);
                ranking.put("María", 3);
                ranking.put("Carlos", 7);
                ranking.put("Luisa", 2);

                game.setScreen(new Finish(game,skin,camera,ranking));
                //game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        stage.addActor(returnBtn);
    }

    @Override
    public void render(float delta) {
        elapsed += Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        //game.font.draw(game.batch, "Partida multijugador", 120, 400);
        game.batch.draw(loading.getKeyFrame(elapsed),100,450);

        font.draw(game.batch, "S'esta esperant...", 20, 200);
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

    private void createCommunications(String URL) {
        try {
            socket = IO.socket(URL);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, args -> Gdx.app.log("SocketIO", "Connected"));
            socket.on("update", args -> {
                JSONObject data = (JSONObject) args[0];
                System.out.println("SocketIO"+ " Message received: " + data.toString());
            });

            socket.on("Join", args -> {
                JSONObject data = (JSONObject) args[0];
                System.out.println("SocketIO"+ " Message received: " + data.toString());
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> Gdx.app.log("SocketIO", "Disconnected"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


}
/*
class WebsocketLSN implements WebSocketListener {

    @Override
    public boolean onOpen(WebSocket webSocket) {
        System.out.println("Opening...");
        return false;
    }

    @Override
    public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
        System.out.println("Closing...");
        return false;
    }

    @Override
    public boolean onMessage(WebSocket webSocket, String packet) {
        System.out.println("Message: "+packet);
        return false;
    }

    @Override
    public boolean onMessage(WebSocket webSocket, byte[] packet) {
        System.out.println("Message: "+packet.toString());
        return false;
    }

    @Override
    public boolean onError(WebSocket webSocket, Throwable error) {
        System.out.println("ERROR:"+error.toString());
        return false;
    }
}

*/