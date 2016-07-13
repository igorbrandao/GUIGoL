package gameObjects;

public class Cell {

    private boolean alive;
    private int neighbourhood;

    public Cell() {
        this.alive = false;
        this.neighbourhood = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive() {
        this.alive = true;
    }

    public void setDead() {
        this.alive = false;
    }

    public int getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(int neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

}
