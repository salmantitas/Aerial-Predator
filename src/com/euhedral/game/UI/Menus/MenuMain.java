package com.euhedral.game.UI.Menus;

import com.euhedral.engine.UI.Button;
import com.euhedral.engine.UI.ButtonNav;
import com.euhedral.engine.UI.Menu;
import com.euhedral.engine.Panel;
import com.euhedral.engine.*;
import com.euhedral.game.UI.MessageBox;

import java.awt.*;

public class MenuMain extends Menu {

    public MenuMain() {
        super(GameState.Menu);
        MAXBUTTON = 3;
        options = new Button[MAXBUTTON];

        Panel sidePanel = new Panel(0, 0, Utility.percWidth(40), Engine.HEIGHT, GameState.Menu);
        menuItems.add(sidePanel);

        ButtonNav play = new ButtonNav(leftButtonX, lowestButtonY, buttonSize, "Play", GameState.Transition);

        play.addOtherState(GameState.GameOver);
        play.setFill();

        ButtonNav help = new ButtonNav(midButtonX, lowestButtonY, buttonSize, "Help", GameState.Help);

        ButtonNav quit = new ButtonNav(rightButtonX, lowestButtonY, buttonSize, "Quit", GameState.Quit);
        quit.setFill();
        quit.addOtherState(GameState.Transition);
        quit.addOtherState(GameState.Pause);
        quit.addOtherState(GameState.GameOver);

        options[0] = play;
        options[1] = help;
        options[2] = quit;

    }

    @Override
    public void render(Graphics g) {
        super.render(g);

//        drawTitle(g);

        super.postRender(g);
    }

    /*
    *
    * */

    private void drawTitle(Graphics g) {
        Font font = new Font("arial", 1, titleSize);
        g.setFont(font);
        g.setColor(titleColor);
        g.drawString(Engine.TITLE, titleX, titleY);
    }
}
