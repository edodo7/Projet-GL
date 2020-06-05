package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.GameLevel;
import be.ac.umons.util.User;
import be.ac.umons.util.UserManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import static be.ac.umons.HexaMaze.skin;

/**
 * This Screen show the Hall of fame
 */
public class HallOfFame extends MenuScreen {


    private SelectBox<GameLevel> selectBox;
    private Table levelTable;


    /**
     * @param game The game class that manage this Screen
     * @param title The title of this screen
     */
    public HallOfFame(Game game,String title) {
        super(game,title);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        selectBox = new SelectBox<>(skin);
        levelTable = new Table();
        ArrayList<GameLevel> levels = GameLevel.createLevelArray();
        Array<GameLevel> levelsGood = new Array<>();
        for (int i = 0; i < levels.size(); i++) {
            levelsGood.add(levels.get(i));
        }
        selectBox.setItems(levelsGood);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createTable(selectBox.getSelected());
            }
        });
        createTable(selectBox.getSelected());
        table.add(selectBox).height(50).fill().expandX().row();
        table.add(levelTable).fill().expand().row();
    }

    /**
     * This method create a Table filled with usernames and scores of 10 best users.
     * @param level The level from which we have to print the hall of fame.
     */
    private void createTable(GameLevel level){
        levelTable.clearChildren();
        ArrayList<User> bestUsers = UserManager.getUserManager().getBestUsers(level);
        Label usernameTitle = HexaMaze.widgetFactory.createLabel(HexaMaze.bundle.format("user"));
        usernameTitle.setAlignment(Align.center);
        Label scoreTitle = HexaMaze.widgetFactory.createLabel("Score");
        scoreTitle.setAlignment(Align.center);
        levelTable.add(usernameTitle).center().fill().expand();
        levelTable.add(scoreTitle).center().fill().expand();
        levelTable.row();
        for (User user : bestUsers){
            Label username = HexaMaze.widgetFactory.createLabel(user.getName());
            username.setAlignment(Align.center);
            Label score = HexaMaze.widgetFactory.createLabel(user.getScore().toString());
            score.setAlignment(Align.center);
            levelTable.add(username).fill().expand();
            levelTable.add(score).fill().expand();
            levelTable.row();
        }
    }



    @Override
    public void render(float delta) {
        super.render(delta);
    }

}
