package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.util.BackgroundActor;
import be.ac.umons.util.UserManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * This class show the login screen if login extension is active
 */
public class Login extends ScreenAdapter {

    private Stage stage;
    private TextField userName;
    private TextField password;
    private Game game;
    private Label title;
    private TextButton loginButton;
    private TextButton createAccount;
    private Table table;
    private Dialog loginError;
    private Dialog createAccountError;
    private TextButton validAccount;
    private TextButton getBack;
    private TextButton anonymousLogin;

    public Login(Game game) {
        BackgroundActor bg = new BackgroundActor(new Texture("img/MainBackground.png"));
        this.game = game;
        stage = new Stage(new ExtendViewport(HexaMaze.WIDTH, HexaMaze.HEIGHT));
        stage.addActor(bg);
        Gdx.input.setInputProcessor(stage);// Make the stage consume events


        table = new Table();
        userName = new TextField("",HexaMaze.skin);
        userName.setMessageText(HexaMaze.bundle.format("username"));
        password = new TextField("",HexaMaze.skin);
        password.setMessageText(HexaMaze.bundle.format("password"));
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

        validAccount = HexaMaze.widgetFactory.createLargeTextButton("OK");
        getBack = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("getBack"));

        loginButton = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("login"));
        createAccount = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("createAccount"));

        anonymousLogin = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("anonymousLogin"));

        validAccount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UserManager userManager = UserManager.getUserManager();
                if (password.getText().replaceAll(" ","").equals("")){
                    Dialog wrongPassword = new Dialog("",HexaMaze.skin);
                    wrongPassword.text(HexaMaze.bundle.format("emptyPassword"));
                    wrongPassword.button(HexaMaze.bundle.format("tryAgain"));
                    wrongPassword.show(stage);
                }
                else if (userName.getText().replaceAll(" ","").equals("") || !userManager.addUser(userName.getText(), password.getText()))
                    createAccountError.show(stage);
                else
                    game.setScreen(new MainMenuScreen(game));
            }
        });

        getBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                title.setText("LOGIN");
                createTable(loginButton,createAccount);
            }
        });

        anonymousLogin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UserManager.getUserManager().addUser("Anonymous","");
                game.setScreen(new MainMenuScreen(game));
            }
        });

        loginError = new Dialog("",HexaMaze.skin);
        loginError.text(HexaMaze.bundle.format("wrongUsername"));
        loginError.button("OK");

        createAccountError = new Dialog("",HexaMaze.skin);
        createAccountError.text("This username is not valid");
        createAccountError.button(HexaMaze.bundle.format("tryAgain"));

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                UserManager userManager = UserManager.getUserManager();
                if (userManager.login(Login.this.userName.getText(),Login.this.password.getText())){
                    game.setScreen(new MainMenuScreen(game));
                }
                else {
                    loginError.show(stage);
                }
            }
        });

        createAccount.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                title.setText(HexaMaze.bundle.format("createAccount"));
                createTable(validAccount,getBack);
                table.removeActor(anonymousLogin);
            }
        });

        title = HexaMaze.widgetFactory.createTitleLabel(HexaMaze.bundle.format("login"));
        title.setColor(Color.BLACK);
        table.setFillParent(true);
        createTable(loginButton,createAccount);
        stage.addActor(table);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    /**
     * This method a table that show two text fields and two buttons in center of the screen
     * @param bottomLeft The button at the bottom left of the two fields
     * @param bottomRight The button at the bottom right of the two fields
     */
    private void createTable(TextButton bottomLeft, TextButton bottomRight){
        table.clearChildren();
        table.add(title).padBottom(100);
        table.row();
        table.add(userName);
        table.row();
        table.add(password).padTop(10);
        table.row();
        table.add(bottomLeft).padTop(20).left();
        table.add(bottomRight).padTop(20).padLeft(10);
        table.row();
        table.add(anonymousLogin).padTop(20);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }
}
