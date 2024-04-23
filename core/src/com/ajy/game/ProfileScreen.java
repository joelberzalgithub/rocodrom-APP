package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;

public class ProfileScreen implements Screen {
    final Roscodrom game;
    OrthographicCamera camera;
    Texture background;
    Stage stage;
    
    Skin skin;
    ImageButton iconButton;
    Texture[] iconTextures;
    int currentIconIndex;

    public ProfileScreen(final Roscodrom game) {
        this.game = game;
        this.game.font.getData().setScale(2.75f);
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 389, 800);
        currentIconIndex = 0;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    private void cycleIcon() {
        // Augmentem l'índex i, si cal, l'enrotllem
        currentIconIndex = (currentIconIndex + 1) % iconTextures.length;
        // Actualitzem la textura de l'icona
        iconButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(iconTextures[currentIconIndex]));
    }

    @Override
    public void show() {
        /*
        // Carreguem les textures de les icones
        iconTextures = new Texture[] {
                new Texture(Gdx.files.internal("icon1.png")),
                new Texture(Gdx.files.internal("icon2.png")),
                new Texture(Gdx.files.internal("icon3.png")),
                new Texture(Gdx.files.internal("icon4.png"))
        };
        // Creem un IconButton a partir de la llista de textures de les icones
        Drawable iconDrawable = new TextureRegionDrawable(new TextureRegion(iconTextures[currentIconIndex]));
        iconButton = new ImageButton(iconDrawable);
        iconButton.setPosition(50, 50);

        // Quan l'usuari prem l'IconButton, les icones es van alternant
        iconButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleIcon();
            }
        });
        */

        TextField nameField = new TextField("Nickname", skin);
        nameField.setHeight(150);
        nameField.setWidth(575);
        nameField.setPosition(425, 1200);
        nameField.getStyle().font.getData().setScale(4);

        TextField emailField = new TextField("Email", skin);
        emailField.setHeight(150);
        emailField.setWidth(575);
        emailField.setPosition(425, 950);
        emailField.getStyle().font.getData().setScale(4);

        TextField tfnField = new TextField("Telefon", skin);
        tfnField.setHeight(150);
        tfnField.setWidth(575);
        tfnField.setPosition(425, 700);
        tfnField.getStyle().font.getData().setScale(4);

        TextButton saveBtn = new TextButton("Desar canvis", skin);
        saveBtn.setPosition(425, 175);
        saveBtn.setHeight(250);
        saveBtn.setWidth(575);

        // Comprovem que tots els TextFields continguin text
        if ((!nameField.getText().isEmpty() || !nameField.getText().isBlank())
                && (!emailField.getText().isEmpty() || !emailField.getText().isBlank())
                && (!tfnField.getText().isEmpty() || !tfnField.getText().isBlank())) {
            saveBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Obtenim els valors del camps de text
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String tfn = tfnField.getText();

                    // Creem el cos de la petició
                    String requestBody = "{" +
                                            "\"nickname\": \"[NICKNAME]\", " +
                                            "\"email\": \"[EMAIL]\", " +
                                            "\"phoneNumber\": \"[PHONENUMBER]\"" +
                                         "}";
                    requestBody = requestBody.replace("[NICKNAME]", name);
                    requestBody = requestBody.replace("[EMAIL]", email);
                    requestBody = requestBody.replace("[PHONENUMBER]", tfn);

                    // Creem una petició HTTP
                    HttpRequest httpRequest = new HttpRequest(HttpMethods.POST);
                    httpRequest.setUrl("https://roscodrom5.ieti.site/api/user/register");
                    httpRequest.setHeader("Content-Type", "application/json");
                    httpRequest.setContent(requestBody);

                    // Enviem la petició
                    Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(HttpResponse httpResponse) {
                            // Comprovem si la petició s'ha enregistat amb éxit
                            int statusCode = httpResponse.getStatus().getStatusCode();
                            if (statusCode == 200) {
                                game.setScreen(new MainMenuScreen(game));
                                dispose();
                            } else {
                                System.out.println("Error registering user: " + httpResponse.getResultAsString());
                            }
                        }
                        @Override
                        public void failed(Throwable t) {
                            t.printStackTrace();
                        }
                        @Override
                        public void cancelled() {
                            System.out.println("Request cancelled");
                        }
                    });
                }
            });
        }

        // stage.addActor(iconButton);
        stage.addActor(nameField);
        stage.addActor(emailField);
        stage.addActor(tfnField);
        stage.addActor(saveBtn);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.font.draw(game.batch, "Perfil", 150, 700);
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
