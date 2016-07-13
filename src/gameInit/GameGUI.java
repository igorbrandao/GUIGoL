package gameInit;

import gameObjects.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;

import jplay.GameImage;
import jplay.Mouse;
import jplay.Sound;
import jplay.Window;

public class GameGUI {

    private Thread thread;
    private GameExec game;
    private static boolean run;
    private static boolean stop;
    private static boolean choosing;
    private final CellModel[][] printGrid = new CellModel[25][25];
    private final Button play = new Button("gameFiles/img/play.png");
    private final Button pause = new Button("gameFiles/img/pause.png");
    private final Button speedUp = new Button("gameFiles/img/speedUp.png");
    private final Button speedDown = new Button("gameFiles/img/speedDown.png");
    private final Button randomInit = new Button("gameFiles/img/random.png");
    private final Button defaultInit = new Button("gameFiles/img/default.png");
    private final Button restart = new Button("gameFiles/img/restart.png");
    private final Button exit = new Button("gameFiles/img/exit.png");
    private final Sound sound = new Sound("gameFiles/sound/highlight.wav");
    private final GameImage background = new GameImage("gameFiles/img/background.png");
    private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private final Font font2 = new Font(Font.MONOSPACED, Font.ITALIC, 20);
    private final Window window = new Window(800, 600);
    private final Mouse mouse = window.getMouse();

    private void launch() {
        run = true;
    }

    private void end() {
        run = false;
    }

    static boolean isRunning() {
        return run;
    }

    private void pauseGame() {
        stop = true;
    }

    private void unpauseGame() {
        synchronized (game.monitor) {
            stop = false;
            game.monitor.notify();
        }
    }

    static boolean isPaused() {
        return stop;
    }

    private void choosing() {
        choosing = true;
    }

    private void playing() {
        choosing = false;
    }

    private boolean isChoosing() {
        return choosing;
    }

    @SuppressWarnings("SleepWhileInLoop")
    public synchronized void begin() throws InterruptedException {

        int xAxis, yAxis = 0;
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setCursor(this.window.createCustomCursor("gameFiles/img/pointer.png"));
        double halfScreenRest = (this.window.getWidth() - 20 * this.printGrid.length) / 2;

        this.play.setX(20 * this.printGrid.length + halfScreenRest - this.play.width);
        this.play.setY(this.printGrid.length);
        this.pause.setX(20 * this.printGrid.length + halfScreenRest - this.pause.width);
        this.pause.setY(this.play.getY() + this.play.height * 2);
        this.speedUp.setX(20 * this.printGrid.length + halfScreenRest - this.speedUp.width);
        this.speedUp.setY(this.pause.getY() + this.pause.height * 2);
        this.speedDown.setX(20 * this.printGrid.length + halfScreenRest - this.speedDown.width);
        this.speedDown.setY(this.speedUp.getY() + this.speedUp.height * 2);
        this.randomInit.setX(20 * this.printGrid.length + halfScreenRest - this.randomInit.width);
        this.randomInit.setY(this.speedDown.getY() + this.speedDown.height * 2);
        this.defaultInit.setX(20 * this.printGrid.length + halfScreenRest - this.defaultInit.width);
        this.defaultInit.setY(this.randomInit.getY() + this.randomInit.height * 2);
        this.restart.setX(20 * this.printGrid.length + halfScreenRest - this.restart.width);
        this.restart.setY(this.defaultInit.getY() + this.defaultInit.height * 2);
        this.exit.setX(20 * this.printGrid.length + halfScreenRest - this.exit.width);
        this.exit.setY(this.restart.getY() + this.restart.height * 2);

        for (int i = 0; i < this.printGrid.length; ++i) {
            xAxis = 0;
            for (int j = 0; j < this.printGrid.length; ++j) {
                this.printGrid[i][j] = new CellModel(xAxis, yAxis);
                xAxis += 20;
            }
            yAxis += 20;
        }

        this.launch();
        this.choosing();
        boolean restarting = false;

        while (isRunning()) {

            this.background.draw();
            this.play.draw();
            this.pause.draw();
            this.speedUp.draw();
            this.speedDown.draw();
            this.randomInit.draw();
            this.defaultInit.draw();
            this.restart.draw();
            this.exit.draw();

            while (isChoosing()) {

                this.background.draw();
                this.play.draw();
                this.pause.draw();
                this.speedUp.draw();
                this.speedDown.draw();
                this.randomInit.draw();
                this.defaultInit.draw();
                this.restart.draw();
                this.exit.draw();

                this.hideButton(this.play);
                this.hideButton(this.pause);
                this.hideButton(this.speedUp);
                this.hideButton(this.speedDown);
                this.showButton(this.randomInit);
                this.showButton(this.defaultInit);
                this.hideButton(this.restart);
                this.showButton(this.exit);

                this.window.drawText("Conway's", (20 * this.printGrid.length) / 2, 100, Color.BLACK, this.font2);
                this.window.drawText("Game", (20 * this.printGrid.length) / 2 + 22, 125, Color.BLACK, this.font2);
                this.window.drawText("of", (20 * this.printGrid.length) / 2 + 30, 150, Color.BLACK, this.font2);
                this.window.drawText("Life", (20 * this.printGrid.length) / 2 + 20, 175, Color.BLACK, this.font2);
                this.window.drawText("TIP - Click any point on the grid to"
                        + " kill or spawn a cell", 10, this.window.getHeight() - 10, Color.BLACK, this.font);

                if (this.defaultInit.state(this.mouse)) {
                    this.window.drawText("Default Initialization", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                    this.menuSound_Highlight(this.defaultInit);
                    if (this.mouse.isLeftButtonPressed()) {
                        this.menuSound_Select();
                        game = new GameExec(1);
                        thread = new Thread(game);
                        thread.start();
                        this.playing();
                    }
                }
                if (this.randomInit.state(this.mouse)) {
                    this.window.drawText("Random Initialization", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                    this.menuSound_Highlight(this.randomInit);
                    if (this.mouse.isLeftButtonPressed()) {
                        this.menuSound_Select();
                        game = new GameExec(2);
                        thread = new Thread(game);
                        thread.start();
                        this.playing();
                    }
                }
                if (this.exit.state(this.mouse)) {
                    this.window.drawText("Exit the Game", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                    this.menuSound_Highlight(this.exit);
                    if (this.mouse.isLeftButtonPressed()) {
                        this.menuSound_Select();
                        this.playing();
                        this.end();
                        Thread.sleep(500);
                    }
                }
                if (this.mouse.isLeftButtonPressed() && invalidMouseArea(this.mouse)) {
                    this.menuSound_Deselect();
                }

                this.window.update();
                this.window.delay(10);

            }

            this.hideButton(this.play);
            this.showButton(this.pause);
            this.showButton(this.speedUp);
            this.showButton(this.speedDown);
            this.hideButton(this.randomInit);
            this.hideButton(this.defaultInit);
            this.showButton(this.restart);

            if (this.mouse.isOverArea(0, 0, 20 * this.printGrid.length, 20 * this.printGrid.length)
                    && this.mouse.isLeftButtonPressed()) {
                this.userManualGridEdit(game.getCurrentGrid(), this.mouse);
            }

            if (this.pause.state(this.mouse)) {
                this.window.drawText("Pause the Evolution on the Current Generation",
                        10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                this.menuSound_Highlight(this.pause);
                if (this.mouse.isLeftButtonPressed()) {
                    this.menuSound_Select();
                    this.pauseGame();
                    this.showButton(this.play);
                    this.hideButton(this.pause);
                    this.hideButton(this.speedUp);
                    this.hideButton(this.speedDown);
                    this.hideButton(this.randomInit);
                    this.hideButton(this.defaultInit);
                    this.showButton(this.restart);

                    while (isPaused()) {

                        this.background.draw();
                        this.play.draw();
                        this.pause.draw();
                        this.speedUp.draw();
                        this.speedDown.draw();
                        this.randomInit.draw();
                        this.defaultInit.draw();
                        this.restart.draw();
                        this.exit.draw();

                        this.window.drawText("Passed Generations: " + game.getGen(),
                                10, this.window.getHeight() - 30, Color.BLACK, this.font);

                        if (mouse.isOverArea(0, 0, 20 * this.printGrid.length, 20 * this.printGrid.length)
                                && mouse.isLeftButtonPressed()) {
                            this.userManualGridEdit(game.getCurrentGrid(), this.mouse);
                        }
                        this.printGrid(game.getCurrentGrid(), this.printGrid);
                        if (this.play.state(this.mouse)) {
                            this.window.drawText("Resume the Evolutionary Process",
                                    10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                            this.menuSound_Highlight(this.play);
                            if (this.mouse.isLeftButtonPressed()) {
                                this.menuSound_Select();
                                this.unpauseGame();
                            }
                        }
                        if (this.restart.state(this.mouse)) {
                            this.window.drawText("Restart the Game",
                                    10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                            this.menuSound_Highlight(this.restart);
                            if (this.mouse.isLeftButtonPressed()) {
                                this.menuSound_Select();
                                restarting = true;
                                this.unpauseGame();
                                this.end();
                                thread.join();
                            }
                        }
                        if (this.exit.state(this.mouse)) {
                            this.window.drawText("Exit the Game", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                            this.menuSound_Highlight(this.exit);
                            if (this.mouse.isLeftButtonPressed()) {
                                this.menuSound_Select();
                                this.unpauseGame();
                                this.end();
                                Thread.sleep(500);
                            }
                        }
                        if (this.mouse.isLeftButtonPressed() && invalidMouseArea(this.mouse)) {
                            this.menuSound_Deselect();
                        }

                        this.window.update();
                        this.window.delay(10);

                    }
                }
            }
            if (this.speedUp.state(this.mouse)) {
                this.window.drawText("Faster Evolution Rate", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                this.menuSound_Highlight(this.speedUp);
                if (this.mouse.isLeftButtonPressed()) {
                    if (game.getDelay() > 100) {
                        this.menuSound_Select();
                        game.lowerDelay();
                    } else {
                        this.menuSound_Deselect();
                    }
                }
            }

            if (this.speedDown.state(this.mouse)) {
                this.window.drawText("Slower Evolution Rate", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                this.menuSound_Highlight(this.speedDown);
                if (this.mouse.isLeftButtonPressed()) {
                    if (game.getDelay() < 1000) {
                        this.menuSound_Select();
                        game.raiseDelay();
                    } else {
                        this.menuSound_Deselect();
                    }
                }
            }

            if (this.restart.state(this.mouse)) {
                this.window.drawText("Restart the Game", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                this.menuSound_Highlight(this.restart);
                if (this.mouse.isLeftButtonPressed()) {
                    this.menuSound_Select();
                    restarting = true;
                    this.end();
                    thread.join();
                }
            }

            if (this.exit.state(this.mouse)) {
                this.window.drawText("Exit the Game", 10, 20 * this.printGrid.length + 15, Color.BLACK, this.font);
                this.menuSound_Highlight(this.exit);
                if (this.mouse.isLeftButtonPressed()) {
                    this.menuSound_Select();
                    this.end();
                    Thread.sleep(500);
                }
            }

            if (this.mouse.isLeftButtonPressed() && invalidMouseArea(this.mouse)) {
                this.menuSound_Deselect();
            }

            if (isRunning()) {

                printGrid(game.getCurrentGrid(), this.printGrid);

                this.window.drawText("Passed Generations: " + game.getGen(),
                        10, this.window.getHeight() - 30, Color.BLACK, this.font);
                this.window.drawText("Speed: " + game.getDelay() + " ms" + " (" + game.getDelay() / 1000 + " second)",
                        10, this.window.getHeight() - 10, Color.BLACK, this.font);

            }

            this.window.update();
            this.window.delay(10);

        }
        if (restarting) {
            this.begin();
        } else {
            this.window.exit();
        }
    }

    private void showButton(Button button) {

        button.unhide();
        button.setOnScreen(true);

    }

    private void hideButton(Button button) {

        button.hide();
        button.setOnScreen(false);

    }

    private void menuSound_Select() {

        new Sound("gameFiles/sound/select.wav").play();

    }

    private void menuSound_Deselect() {

        new Sound("gameFiles/sound/deselect.wav").play();

    }

    private void menuSound_Highlight(Button button) {

        if (!this.sound.isExecuting() && !button.hasPlayed()) {
            this.sound.load("gameFiles/sound/highlight.wav");
            this.sound.play();
            button.soundWasPlayed();
        }
    }

    private void printGrid(Cell[][] currentGrid, CellModel[][] printGrid) {

        for (int i = 0; i < printGrid.length; ++i) {
            for (int j = 0; j < printGrid.length; ++j) {
                printGrid[i][j].defineStatus(currentGrid[i][j].isAlive());
                printGrid[i][j].draw();
            }
        }
    }

    private int[] getCoordinates(Mouse mouse) {

        int[] coordinates = new int[2];

        coordinates[1] = (int) (mouse.getPosition().getX() / 20);
        coordinates[0] = (int) (mouse.getPosition().getY() / 20);

        return coordinates;
    }

    private void userManualGridEdit(Cell[][] currentGrid, Mouse mouse) {

        int[] coordinates = getCoordinates(mouse);

        if (currentGrid[coordinates[0]][coordinates[1]].isAlive()) {
            new Sound("gameFiles/sound/setDead.wav").play();
            currentGrid[coordinates[0]][coordinates[1]].setDead();
        } else {
            new Sound("gameFiles/sound/setAlive.wav").play();
            currentGrid[coordinates[0]][coordinates[1]].setAlive();
        }
    }

    private boolean invalidMouseArea(Mouse mouse) {

        return mouse.isOverArea(0, 0, 800, 600)
                && (isChoosing()
                || (!isChoosing()
                && !mouse.isOverArea(0, 0, 20 * this.printGrid.length, 20 * this.printGrid.length)))
                && !this.play.state(mouse)
                && !this.pause.state(mouse)
                && !this.speedUp.state(mouse)
                && !this.speedDown.state(mouse)
                && !this.randomInit.state(mouse)
                && !this.defaultInit.state(mouse)
                && !this.restart.state(mouse)
                && !this.exit.state(mouse);
    }
}
