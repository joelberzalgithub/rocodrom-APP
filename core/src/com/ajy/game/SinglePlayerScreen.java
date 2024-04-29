package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerScreen extends ScreenAdapter {
    final Roscodrom game;
    final Skin skin;
    private Stage stage;
    OrthographicCamera camera;
    MainMenuScreen mainMenuScreen;
    SpriteBatch batch;
    Texture background;
    ArrayList<Vector2> letterPositions;
    Circle[] letterHitboxes;
    private Texture[] letterTextures;
    private Texture[] clickedTextures;
    List<String> palabra=new ArrayList<>();
    private static final int LETTER_FONT_SIZE = 30; // Tamaño en píxeles
    private static final int NUM_LETTERS = 10; // Cambiar el número de letras aquí
    private static final float HITBOX_RADIUS = 150f; // Radio del hitbox para agrandar el área de detección

    private List<Character> clickedLetters = new ArrayList<>();

    public SinglePlayerScreen(final Roscodrom game,Skin skin) {
        mainMenuScreen = new MainMenuScreen(game);
        mainMenuScreen.setListenersEnabled(false);
        this.game = game;
        this.skin = skin;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        letterPositions = new ArrayList<>();
        letterHitboxes = new Circle[NUM_LETTERS];
        generateAlphabetPositions();
        loadLetterTextures();
        loadClickedTextures();
    }

    private void generateAlphabetPositions() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * 0.8f;
        float angleIncrement = 360f / NUM_LETTERS;
        float currentAngle = 0f;

        for (int i = 0; i < NUM_LETTERS; i++) {
            float x = centerX + (float) Math.cos(Math.toRadians(currentAngle)) * radius;
            float y = centerY + (float) Math.sin(Math.toRadians(currentAngle)) * radius;
            letterPositions.add(new Vector2(x, y));

            letterHitboxes[i] = new Circle(x, y, HITBOX_RADIUS);

            currentAngle += angleIncrement;
        }
    }

    private void loadLetterTextures() {
        letterTextures = new Texture[NUM_LETTERS];
        for (int i = 0; i < NUM_LETTERS; i++) {
            char letter = (char) ('A' + i);
            String texturePath = "normal/letter_" + letter + ".png";
            letterTextures[i] = new Texture(Gdx.files.internal(texturePath));
        }
    }
    private Texture resizeTexture(Texture originalTexture) {
        // Calcula el nuevo ancho y altura reduciendo a la mitad
        int newWidth = originalTexture.getWidth() / 2+50;
        int newHeight = originalTexture.getHeight() / 2+50;

        // Crea una nueva textura con el nuevo tamaño
        Texture resizedTexture = new Texture(newWidth, newHeight, originalTexture.getTextureData().getFormat());

        // Configura los filtros de textura para mantener la calidad
        resizedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Dibuja la textura original en la nueva textura redimensionada
        Pixmap pixmap = new Pixmap(originalTexture.getWidth(), originalTexture.getHeight(), Pixmap.Format.RGBA8888);
        originalTexture.getTextureData().prepare();
        pixmap.drawPixmap(originalTexture.getTextureData().consumePixmap(), 0, 0);
        Pixmap resizedPixmap = new Pixmap(newWidth, newHeight, pixmap.getFormat());
        resizedPixmap.drawPixmap(pixmap, 0, 0, originalTexture.getWidth(), originalTexture.getHeight(), 0, 0, newWidth, newHeight);
        resizedTexture.draw(resizedPixmap, 0, 0);
        pixmap.dispose();
        resizedPixmap.dispose();

        return resizedTexture;
    }

    private void loadClickedTextures() {
        clickedTextures = new Texture[NUM_LETTERS];
        for (int i = 0; i < NUM_LETTERS; i++) {
            char letter = (char) ('A' + i);
            String texturePath = "clickeado/letter_" + letter + ".png";
            clickedTextures[i] = new Texture(Gdx.files.internal(texturePath));
        }
    }
    @Override
    public void show() {
        TextButton returnBtn = new TextButton("<", skin);
        returnBtn.setPosition(20, 500);

        returnBtn.setHeight(200);
        returnBtn.setWidth(200);

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
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);


        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (int i = 0; i < NUM_LETTERS; i++) {
            char letter = (char) ('A' + i);
            Vector2 position = letterPositions.get(i);
            Texture texture = clickedLetters.contains(letter) ? clickedTextures[i] : letterTextures[i];
            batch.draw(texture, position.x - texture.getWidth() / 2f, position.y - texture.getHeight() / 2f);
        }
        renderWord();
        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Check for clicks
        if (Gdx.input.justTouched()) {
            float clickX = Gdx.input.getX();
            float clickY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < NUM_LETTERS; i++) {
                char letter = (char) ('A' + i);
                Vector2 position = letterPositions.get(i);
                Circle hitbox = letterHitboxes[i];

                if (hitbox.contains(clickX, clickY)) {
                    clickedLetters.add(letter);
                    palabra.add(letter+"");
                    System.out.println("Clicked on: " + letter);
                    break;
                }
            }
        }
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private void renderWord() {
        float totalWordWidth = palabra.size() * 10; // Ancho total de la palabra
        float startX = (Gdx.graphics.getWidth() - totalWordWidth) / 100f; // Alineación centrada
        float y = Gdx.graphics.getHeight() / 7f + 2200;
        float letterSpacing = 180f; // Ajustar el espacio entre letras
        float rowPadding = -80f; // Espacio vertical entre filas
        int lettersInCurrentRow = 0; // Rastrea cuántas letras se han dibujado en la fila actual
        float scale = 0.5f;
        for (String letter : palabra) {
            char ch = letter.charAt(0);
            int index = ch - 'A';
            if (index >= 0 && index < NUM_LETTERS) {
                Texture texture = clickedLetters.contains(ch) ? clickedTextures[index] : letterTextures[index];
                batch.draw(resizeTexture(texture), startX, y);
                startX += letterSpacing; // Espacio entre letras

                // Verificar si se ha completado la fila actual
                lettersInCurrentRow++;
                if (lettersInCurrentRow >= 8) {
                    // Reiniciar startX para la siguiente fila
                    startX = (Gdx.graphics.getWidth() - totalWordWidth) / 2000f;
                    // Ajustar la posición y para la siguiente fila
                    y -= texture.getHeight() + rowPadding; // Espacio vertical entre filas
                    // Reiniciar el contador de letras en la fila actual
                    lettersInCurrentRow = 0;
                }
            }
        }
    }




    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        for (Texture texture : letterTextures) {
            texture.dispose();
        }
        for (Texture texture : clickedTextures) {
            texture.dispose();
        }
    }
}
