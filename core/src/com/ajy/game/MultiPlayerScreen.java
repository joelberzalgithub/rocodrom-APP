package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

import java.io.IOException;

public class MultiPlayerScreen implements Screen {
    final Roscodrom game;
    Socket socket;

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
        stage = new Stage(new FitViewport(389, 800, camera));
        Gdx.input.setInputProcessor(stage);
        String URL="ws://roscodrom5.ieti.site/";
        socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "roscodrom5.ieti.site", 80,new SocketHints());
        String message="{\n" +
                "  \"nickname\": \"usuarioEjemplo\",\n" +
                "  \"apiKey\": \"c9f2ae7e-3e5d-4f08-a290-5e9f547e3aeb\"\n" +
                "}";
        try {
            socket.getOutputStream().write(message.getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

/*
        socket.setSendGracefully(false);
        socket.addListener((WebSocketListener) new WebsocketLSN());


        socket.connect();
        socket.send("{\n" +
                "  \"nickname\": \"usuarioEjemplo\",\n" +
                "  \"apiKey\": \"c9f2ae7e-3e5d-4f08-a290-5e9f547e3aeb\"\n" +
                "}");
*/
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
                game.setScreen(new MainMenuScreen(game));
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