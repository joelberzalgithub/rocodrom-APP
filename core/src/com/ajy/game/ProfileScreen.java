package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
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
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Base64;

public class ProfileScreen implements Screen {
    final Roscodrom game;
    final Skin skin;
    final OrthographicCamera camera;
    Texture background;
    Stage stage;
    Texture[] iconTextures;
    int currentIconIndex;
    String requestBody;

    public ProfileScreen(final Roscodrom game, final Skin skin, final OrthographicCamera camera) {
        this.game = game;
        this.game.font.getData().setScale(2.75f);
        this.skin = skin;
        this.camera = camera;
        this.camera.setToOrtho(false, 389, 800);
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        stage = new Stage(new FitViewport(389, 800, camera));
        Gdx.input.setInputProcessor(stage);
        currentIconIndex = 0;
    }

    private String avatarToBase64() {
        Texture icon = iconTextures[currentIconIndex];
        Pixmap pixmap = new Pixmap(icon.getWidth(), icon.getHeight(), Pixmap.Format.RGBA4444);
        FileHandle file = FileHandle.tempFile("tempAvatar");
        try {
            PixmapIO.writePNG(file, pixmap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        pixmap.dispose();
        byte[] imgBytes = file.readBytes();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(imgBytes);
    }

    @Override
    public void show() {
        // Carreguem les textures de les icones
        iconTextures = new Texture[] {
                new Texture(Gdx.files.internal("icon1.png")),
                new Texture(Gdx.files.internal("icon2.png")),
                new Texture(Gdx.files.internal("icon3.png")),
                new Texture(Gdx.files.internal("icon4.png"))
        };

        // Creem un IconButton a partir de la llista de textures de les icones
        Drawable iconDrawable = new TextureRegionDrawable(new TextureRegion(iconTextures[currentIconIndex]));
        ImageButton iconBtn = new ImageButton(iconDrawable);
        iconBtn.setPosition(105, 440);

        // Quan l'usuari prem l'IconButton, les icones es van alternant
        iconBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Augmentem l'índex i, si cal, l'enrotllem
                currentIconIndex = (currentIconIndex + 1) % iconTextures.length;
                // Actualitzem la textura de l'icona
                iconBtn.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(iconTextures[currentIconIndex]));
            }
        });

        TextField nameField = new TextField("Nickname", skin);
        nameField.setHeight(50);
        nameField.setWidth(222);
        nameField.setPosition(80, 340);

        TextField emailField = new TextField("Email", skin);
        emailField.setHeight(50);
        emailField.setWidth(222);
        emailField.setPosition(80, 260);

        TextField tfnField = new TextField("Telefon", skin);
        tfnField.setHeight(50);
        tfnField.setWidth(222);
        tfnField.setPosition(80, 180);

        TextButton saveBtn = new TextButton("Desar canvis", skin);
        saveBtn.setPosition(80, 45);
        saveBtn.setHeight(75);
        saveBtn.setWidth(222);

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

        // Comprovem que tots els TextFields continguin text
        if (!nameField.getText().isEmpty() && !emailField.getText().isEmpty()  && !tfnField.getText().isEmpty()) {
            saveBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Obtenim els valors del camps de text
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String tfn = tfnField.getText();
                    String avatar = avatarToBase64();

                    // Creem el cos de la petició
                    requestBody = "{" +
                            "\"nickname\": \"[NICKNAME]\", " +
                            "\"email\": \"[EMAIL]\", " +
                            "\"phoneNumber\": \"[PHONENUMBER]\", " +
                            "\"avatar\": \"[AVATAR]\"" +
                            "}";
                    requestBody = requestBody.replace("[NICKNAME]", name);
                    requestBody = requestBody.replace("[EMAIL]", email);
                    requestBody = requestBody.replace("[PHONENUMBER]", tfn);
                    requestBody = requestBody.replace("[AVATAR]", avatar);
                    System.out.println(requestBody);

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
                            System.out.println(statusCode);
                            if (statusCode == 200) {
                                Gdx.app.postRunnable(() -> {
                                    // Tornem al menú principal
                                    game.setScreen(new MainMenuScreen(game));
                                    dispose();
                                });

                                // Inserim el cos de la petició dins d'un arxiu JSON
                                try {
                                    String file = "profile.json";
                                    FileHandle fileHandle = Gdx.files.local(file);
                                    if (!fileHandle.exists()) {
                                        if (!fileHandle.file().createNewFile()) {
                                            System.out.println("Failed to create file: " + file);
                                            return;
                                        }
                                    }
                                    fileHandle.writeString(requestBody, false);
                                    System.out.println("Request Body successfully inserted into " + file);

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            } else {
                                System.out.println("Error registering user: " + httpResponse.getResultAsString());
                            }
                        }
                        @Override
                        public void failed(Throwable t) {
                            System.out.println("Request failed: " + t);
                        }
                        @Override
                        public void cancelled() {
                            System.out.println("Request cancelled");
                        }
                    });
                }
            });
        }

        stage.addActor(returnBtn);
        stage.addActor(iconBtn);
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
        game.font.draw(game.batch, "Perfil", 140, 685);
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
