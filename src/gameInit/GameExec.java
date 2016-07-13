package gameInit;

import gameObjects.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jplay.Sound;

class GameExec implements Runnable {

    private int initMode;
    private int generationNumber;
    private long iterationDelay = 550;
    final Object monitor = new Object();
    private final Cell[][] nextGrid = new Cell[25][25];
    private final Cell[][] currentGrid = new Cell[25][25];

    int getGen() {
        return this.generationNumber;
    }

    float getDelay() {
        return this.iterationDelay;
    }

    void raiseDelay() {
        this.iterationDelay += 225;
    }

    void lowerDelay() {
        this.iterationDelay -= 225;
    }

    Cell[][] getCurrentGrid() {
        return this.currentGrid;
    }

    GameExec(int initMode) {
        this.initMode = 0;
        this.initMode = initMode;
        this.generationNumber = 0;
        for (int i = 0; i < this.currentGrid.length; ++i) {
            for (int j = 0; j < this.currentGrid.length; ++j) {
                this.currentGrid[i][j] = new Cell();
            }
        }
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {

        if (this.initMode == 1) {
            this.DefaultInitialize();
        }

        if (this.initMode == 2) {
            this.RandomInitialize();
        }

        while (GameGUI.isRunning()) {

            synchronized (monitor) {
                while (GameGUI.isPaused()) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            for (int i = 0; i < this.currentGrid.length; ++i) {
                for (int j = 0; j < this.currentGrid.length; ++j) {
                    this.scanNeighbourhood(i, j);
                }
            }

            this.Evolution();
            this.Transfer();
            this.generationNumber++;

            if (this.iterationDelay == 100) {
                new Sound("gameFiles/sound/gen4.wav").play();
            }
            if (this.iterationDelay == 325) {
                new Sound("gameFiles/sound/gen3.wav").play();
            }
            if (this.iterationDelay == 550) {
                new Sound("gameFiles/sound/gen2.wav").play();
            }
            if (this.iterationDelay == 775) {
                new Sound("gameFiles/sound/gen1.wav").play();
            }
            if (this.iterationDelay == 1000) {
                new Sound("gameFiles/sound/gen0.wav").play();
            }

            try {
                Thread.sleep(this.iterationDelay);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameExec.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void DefaultInitialize() {

        for (int i = 0; i < this.currentGrid.length; ++i) {
            for (int j = 0; j < this.currentGrid.length; ++j) {
                if (i == 2 && (j == 1 || j == 2 || j == 3)) {
                    this.currentGrid[i][j].setAlive();
                } else {
                    this.currentGrid[i][j].setDead();
                }
            }
        }
    }

    private void RandomInitialize() {

        java.util.Random rand = new java.util.Random();

        for (Cell[] currentGridRow : this.currentGrid) {
            for (int j = 0; j < this.currentGrid.length; ++j) {
                if (rand.nextBoolean()) {
                    currentGridRow[j].setAlive();
                } else {
                    currentGridRow[j].setDead();
                }
            }
        }
    }

    private void scanNeighbourhood(int i, int j) {

        int counter = 0;
        boolean[] vet = new boolean[8];

        if (i > 0 && j > 0) {
            vet[0] = this.currentGrid[i - 1][j - 1].isAlive();
        }
        if (i > 0) {
            vet[1] = this.currentGrid[i - 1][j].isAlive();
        }
        if ((i > 0 && j != this.currentGrid.length - 1) || (i != 0 && j < this.currentGrid.length - 1)) {
            vet[2] = this.currentGrid[i - 1][j + 1].isAlive();
        }
        if (j > 0) {
            vet[3] = this.currentGrid[i][j - 1].isAlive();
        }
        if (j < this.currentGrid.length - 1) {
            vet[4] = this.currentGrid[i][j + 1].isAlive();
        }
        if ((i < this.currentGrid.length - 1 && j != 0) || (i != this.currentGrid.length - 1 && j > 0)) {
            vet[5] = this.currentGrid[i + 1][j - 1].isAlive();
        }
        if (i < this.currentGrid.length - 1) {
            vet[6] = this.currentGrid[i + 1][j].isAlive();
        }
        if (i < this.currentGrid.length - 1 && j < this.currentGrid.length - 1) {
            vet[7] = this.currentGrid[i + 1][j + 1].isAlive();
        }

        for (int a = 0; a < 8; ++a) {
            if (vet[a]) {
                counter++;
            }
        }
        this.currentGrid[i][j].setNeighbourhood(counter);
    }

    private boolean Undercrowd(int i, int j) {

        return this.currentGrid[i][j].getNeighbourhood() < 2;
    }

    private boolean Overcrowd(int i, int j) {

        return this.currentGrid[i][j].getNeighbourhood() > 3;
    }

    private boolean Survival(int i, int j) {

        return (this.currentGrid[i][j].getNeighbourhood() == 2 || this.currentGrid[i][j].getNeighbourhood() == 3)
                && currentGrid[i][j].isAlive();
    }

    private boolean Birth(int i, int j) {

        return this.currentGrid[i][j].getNeighbourhood() == 3 && !this.currentGrid[i][j].isAlive();
    }

    private void Evolution() {

        for (int i = 0; i < this.currentGrid.length; ++i) {
            for (int j = 0; j < this.currentGrid.length; j++) {
                this.nextGrid[i][j] = new Cell();
                if (this.Undercrowd(i, j)) {
                    this.nextGrid[i][j].setDead();
                } else if (this.Overcrowd(i, j)) {
                    this.nextGrid[i][j].setDead();
                } else if (this.Survival(i, j)) {
                    this.nextGrid[i][j].setAlive();
                } else if (this.Birth(i, j)) {
                    this.nextGrid[i][j].setAlive();
                }
            }
        }
    }

    private void Transfer() {

        for (int i = 0; i < this.currentGrid.length; ++i) {
            for (int j = 0; j < this.currentGrid.length; ++j) {
                if (this.nextGrid[i][j].isAlive()) {
                    this.currentGrid[i][j].setAlive();
                } else {
                    this.currentGrid[i][j].setDead();
                }
                this.currentGrid[i][j].setNeighbourhood(this.nextGrid[i][j].getNeighbourhood());
            }
        }
    }
}
