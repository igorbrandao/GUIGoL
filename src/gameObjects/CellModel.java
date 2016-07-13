package gameObjects;

import jplay.Sprite;

public class CellModel extends Sprite {

    public CellModel(double x, double y) {
        super("gameFiles/img/cell.png", 2);
        setX(x);
        setY(y);
    }

    public void defineStatus(boolean alive) {
        if (alive) {
            setCurrFrame(0);
        } else {
            setCurrFrame(1);
        }
    }
}
