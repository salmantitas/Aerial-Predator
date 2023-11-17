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

//    public static boolean moved = false;
//    public static boolean shot = false;

    MessageBox tutorial;

    public MenuPlay() {
        super(GameState.Game);
        MAXBUTTON = 0;
        options = new Button[MAXBUTTON];

        tutorial = new MessageBox(500,150);
        tutorial.addText("You can use WASD or the Arrow Keys");
        tutorial.addText("for movement.");
        tutorial.addText("");
        tutorial.addText("SPACEBAR can be used for shooting.");
//        a.addText("CTRL is used to switch to ground bullets.");
        tutorial.addText("");
        tutorial.addText("Click the red square to dismiss this");
        tutorial.addText("message and starts the game.");
        tutorial.addText("");
        tutorial.addText("You can disable tutorial in Settings.");
        tutorial.addText("");
        tutorial.addText("Enjoy!");
        addMessageBox(tutorial);

        Panel topPane = new Panel(0,0, Engine.WIDTH, Utility.percHeight(12), GameState.Game);
        topPane.setTransparency(1);
        menuItems.add(topPane);
    }

//    public static void moved() {
//
//    }
//
//    public static void shot() {
//
//    }

    @Override
    public void render(Graphics g) {

        super.render(g);

        VariableHandler.renderHUD(g);
//        VariableHandler.renderLevel(g); // todo: disabled unless levels are reimplemented
//        if (VariableHandler.isTutorial() && activeMessageBoxes > 0)
//            renderTutorials(g);

        super.postRender(g);
    }

    @Override
    public void onSwitch() {
        super.onSwitch();
        SoundHandler.playBGMPlay();
    }

//    private void renderTutorials(Graphics g) {
//        int textX = 200, moveY = 200, shootY = 300;
//        Color c = Color.black;
//        if (moved) c = Color.green;
//
//        g.setColor(c);
//        g.drawString("You can use WASD or the Arrow Keys for movement", textX, moveY);
//
//        c = Color.black;
//        if (shot) c = Color.GREEN;
//
//        g.setColor(c);
//        g.drawString("SPACEBAR can be used for shooting.", textX, shootY);
//    }
}
