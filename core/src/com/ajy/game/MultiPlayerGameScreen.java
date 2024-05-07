package com.ajy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MultiPlayerGameScreen extends ScreenAdapter {
    final Roscodrom game;
    final Skin skin;
    private Stage stage;
    OrthographicCamera camera;
    MainMenuScreen mainMenuScreen;
    SpriteBatch batch;
    Texture background;
    Map<String[],Integer> puntuajeReglas = new HashMap<>();
    ArrayList<Vector2> letterPositions;
    Circle[] letterHitboxes;
    private Texture[] letterTextures;
    private Sound goodSound;
    private Sound badSound;
    private BitmapFont font;
    TextButton returnBtn,confirmBtn,CleanBtn;
  
    List<String>  alphabetFinal=new ArrayList<>();
    int Puntuaje=0;
    private Texture[] clickedTextures;
    String []dicc;
    private static final int LETTER_FONT_SIZE = 30; // Tamaño en píxeles
    private static final int NUM_LETTERS = 10; // Cambiar el número de letras aquí
    private static final float HITBOX_RADIUS = 150f; // Radio del hitbox para agrandar el área de detección

    private List<String> clickedLetters = new ArrayList<>();
     int countdownSeconds = -1;
    boolean listener_activo=true;
    public MultiPlayerGameScreen(final Roscodrom game,Skin skin) {
        this.game = game;
        this.skin = skin;

        mainMenuScreen = new MainMenuScreen(game);
        mainMenuScreen.setListenersEnabled(false);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("backgroundGame.jpg"));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        letterPositions = new ArrayList<>();
        letterHitboxes = new Circle[NUM_LETTERS];
        generateAlphabetPositions();
        
        FileHandle fileHandle = Gdx.files.internal("diccs/dicc.txt");
        dicc= fileHandle.readString().split("\n");
        goodSound = Gdx.audio.newSound(Gdx.files.internal("sounds/correct.mp3"));
        badSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wrong.mp3"));
        // Agregar elementos al mapa
        puntuajeReglas.put(new String[]{"E", "A", "I", "O", "S", "N", "R", "L"},5);
        puntuajeReglas.put(new String[]{"T", "U", "D", "C"},7);
        puntuajeReglas.put( new String[]{"M", "P", "B", "V", "G"},10);
        puntuajeReglas.put( new String[]{"Y", "H", "Q"},13);
        puntuajeReglas.put( new String[]{"F", "J", "Z"},17);
        puntuajeReglas.put( new String[]{"X", "K", "W"},20);
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        Set<String> selectedLetters = new HashSet<>();
        Random random = new Random();

        //Conseguir alphabet del servidor
        for (String letter : selectedLetters) {
            alphabetFinal.add(letter);
        }
        ///
        loadLetterTextures();
        loadClickedTextures();
        //System.out.println(dicc);

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
        for (int i = 0; i < alphabetFinal.size(); i++) {
            String letter = alphabetFinal.get(i);
            String texturePath = "normal/letter_" + letter + ".png";
            letterTextures[i] = new Texture(Gdx.files.internal(texturePath));
        }
    }


    private void loadClickedTextures() {
        clickedTextures = new Texture[NUM_LETTERS];
        for (int i = 0; i < alphabetFinal.size(); i++) {
            String letter = alphabetFinal.get(i);
            String texturePath = "clickeado/letter_" + letter + ".png";
            clickedTextures[i] = new Texture(Gdx.files.internal(texturePath));
        }
    }
    @Override
    public void show() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        BitmapFont font = new BitmapFont(); // Esto crea un BitmapFont por defecto

        textButtonStyle.font = font;
        textButtonStyle.font.getData().setScale(7f, 7f);


        returnBtn = new TextButton("<",textButtonStyle);
        returnBtn.setPosition(20, 500);
        returnBtn.setHeight(200);
        returnBtn.setWidth(200);

        returnBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( listener_activo){
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }

            }
        });
        stage.addActor(returnBtn);

        CleanBtn = new TextButton("X", textButtonStyle);
        CleanBtn.setPosition(1200, 500);

        CleanBtn.setHeight(200);
        CleanBtn.setWidth(200);

        CleanBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener_activo){
                    clickedLetters = new ArrayList<>();
                }

            }
        });
        stage.addActor(CleanBtn);

        confirmBtn = new TextButton("Confirmar", textButtonStyle);
        confirmBtn.setPosition(500, 500);

        confirmBtn.setHeight(200);
        confirmBtn.setWidth(500);

        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener_activo){
                    String palabraMontado="";
                    for (String i : clickedLetters){
                        palabraMontado+=i+"";
                    }
                    Boolean encontrado=binarySearch(palabraMontado);
                    if (encontrado){
                        //WEBSOCKET MANDAR PUNTOS
                        goodSound.play();

                        for (String i : clickedLetters){
                            for (String[] j:puntuajeReglas.keySet()){
                                String iniciales="";
                                for (String s:j){
                                    iniciales+=s;
                                }
                                if ( iniciales.contains(i)){
                                    Puntuaje+=puntuajeReglas.get(j);
                                }
                            }
                        }

                        clickedLetters = new ArrayList<>();
                    }else{
                        badSound.play();
                    }

                }

            }

        });
        stage.addActor(confirmBtn);
    }

    @Override
    public void render(float delta) {
        //WEBSOCKET CONSEGUIR TIEMPO RESTANTE
        //........
        ///
        
        ///WEBSOCKET RECIBIR PALABRAS CORRECTAS DE OTRO USUARIO:
        //ToastNotification notification = new ToastNotification("Mensaje de notificación", 2);
        ///
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);


        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (int i = 0; i < alphabetFinal.size(); i++) {
            String letter = alphabetFinal.get(i);
            Vector2 position = letterPositions.get(i);
            Texture texture = clickedLetters.contains(letter) ? clickedTextures[i] : letterTextures[i];
            batch.draw(texture, position.x - texture.getWidth() / 2f, position.y - texture.getHeight() / 2f);
        }
        renderWord();
        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        batch.begin();
        font.draw(batch, "Punts: " + Puntuaje, 20, Gdx.graphics.getHeight() - 10);
        font.draw(batch,   ""+countdownSeconds, 1200, Gdx.graphics.getHeight() - 10);
        batch.end();
        // Check for clicks
        if (Gdx.input.justTouched() && listener_activo) {
            float clickX = Gdx.input.getX();
            float clickY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < alphabetFinal.size(); i++) {
                String letter = alphabetFinal.get(i);
                Vector2 position = letterPositions.get(i);
                Circle hitbox = letterHitboxes[i];

                if (hitbox.contains(clickX, clickY)) {
                    clickedLetters.add(letter);
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
        float totalWordWidth = clickedLetters.size() * 10; // Ancho total de la palabra
        float startX = (Gdx.graphics.getWidth() - totalWordWidth) / 100f; // Alineación centrada
        float y = Gdx.graphics.getHeight() / 7f + 2200;
        float letterSpacing = 250f; // Ajustar el espacio entre letras
        float rowPadding = 0f; // Espacio vertical entre filas
        int lettersInCurrentRow = 0; // Rastrea cuántas letras se han dibujado en la fila actual
        for (String letter : clickedLetters) {
            int index=-1;
            for (int l = 0; l < alphabetFinal.size(); l++){
                if (alphabetFinal.get(l).equals(letter)){
                    index=l;
                    break;
                }
            }
            char ch = (letter+"").charAt(0);
            if (index >= 0 && index < NUM_LETTERS) {
                Texture texture = clickedLetters.contains(ch) ? clickedTextures[index] : letterTextures[index];
                //batch.draw(resizeTexture(texture), startX, y);
                batch.draw((texture), startX, y);
                startX += letterSpacing; // Espacio entre letras

                // Verificar si se ha completado la fila actual
                lettersInCurrentRow++;
                if (lettersInCurrentRow >= 6) {
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
    public boolean binarySearch(String word) {
        int low = 0;
        int high = dicc.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int comparison = word.compareTo(dicc[mid]);

            if (comparison == 0) {
                // La palabra fue encontrada en el diccionario
                return true;
            } else if (comparison < 0) {
                // La palabra podría estar en la mitad izquierda
                high = mid - 1;
            } else {
                // La palabra podría estar en la mitad derecha
                low = mid + 1;
            }
        }

        // La palabra no fue encontrada en el diccionario
        return false;
    }

    private void showTimeoutDialog() {
        setListenersEnabled(false);
        Dialog timeoutDialog = new Dialog("", skin);

        // Configura el estilo del texto del diálogo con un tamaño de fuente más grande
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font"); // Usa la fuente por defecto
        labelStyle.font.getData().setScale(5); // Ajusta el tamaño de la fuente (2 veces más grande)

        // Crea un nuevo objeto Label con el estilo configurado
        Label messageLabel = new Label("S'ha terminat el temps !!\nTens total de "+Puntuaje+" punts", labelStyle);

        // Agrega el objeto Label al contenido del diálogo
        timeoutDialog.getContentTable().add(messageLabel).center().pad(20);

        // Agrega un botón para cerrar el diálogo
        timeoutDialog.button("Aceptar", true);

        // Ajusta el tamaño del diálogo
        timeoutDialog.setSize(1200, 2000);
        // Obtener el botón "Aceptar" del diálogo

        // Establece la posición del diálogo
        timeoutDialog.setPosition(Gdx.graphics.getWidth() / 2 - timeoutDialog.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - timeoutDialog.getHeight() / 2);

        // Agrega el diálogo al escenario para que se muestre
        timeoutDialog.getButtonTable().getCells().get(0).getActor().addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                setListenersEnabled(true);
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        stage.addActor(timeoutDialog);
    }

    @Override
    public void dispose() {
        batch.dispose();
        goodSound.dispose();
        badSound.dispose();
        background.dispose();
        for (Texture texture : letterTextures) {
            texture.dispose();
        }
        for (Texture texture : clickedTextures) {
            texture.dispose();
        }
    }

    public void setListenersEnabled(boolean enabled) {
        listener_activo = enabled;
    }
}
class ToastNotification extends Actor {
    private String message;
    private BitmapFont font;
    private float duration;
    private float timer;

    public ToastNotification(String message, float duration) {
        this.message = message;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.duration = duration;
        this.timer = 0;
        // Obtener el tamaño del texto usando getData().getBounds()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer += delta;
        if (timer >= duration) {
            remove(); // Remove the notification from the stage
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        font.draw(batch, message, getX(), getY() + getHeight());
    }
}
