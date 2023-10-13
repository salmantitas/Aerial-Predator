package com.euhedral.game.UI.Menus;

import com.euhedral.engine.Engine;
import com.euhedral.engine.GameState;
import com.euhedral.engine.UI.Button;
import com.euhedral.engine.UI.Menu;
import com.euhedral.engine.UI.Panel;
import com.euhedral.engine.Utility;
import com.euhedral.game.GameController;
import com.euhedral.game.SoundHandler;
import com.euhedral.game.UI.MessageBox;
import com.euhedral.game.VariableHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuPlay extends Menu {

    public MenuPlay() {
        super(GameState.Game);
        MAXBUTTON = 0;
        options = new Button[MAXBUTTON];

        MessageBox a = new MessageBox(500,150);
        a.addText("You can use WASD or the Arrow Keys");
        a.addText("for movement.");
        a.addText("");
        a.addText("SPACEBAR can be used for shooting.");
//        a.addText("CTRL is used to switch to ground bullets.");
        a.addText("");
        a.addText("Click the red square to dismiss this");
        a.addText("message and starts the game.");
        a.addText("");
        a.addText("You can disable tutorial in Settings.");
        a.addText("");
        a.addText("Enjoy!");
        addMessageBox(a);

        Panel topPane = new Panel(0,0, Engine.WIDTH, Utility.percHeight(12), GameState.Game);
        topPane.setTransparency(1);
        menuItems.add(topPane);
    }

    @Override
    public void render(Graphics g) {

        super.render(g);

        VariableHandler.renderHUD(g);
//        VariableHandler.renderLevel(g); // todo: disabled unless levels are reimplemented

        super.postRender(g);
    }

    @Override
    public void onSwitch() {
//        GameController.getSound().bgm_Main.stop();
        SoundHandler.playBGMPlay();
    }
}
