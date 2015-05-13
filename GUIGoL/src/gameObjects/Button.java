package gameObjects;

import jplay.Mouse;
import jplay.Sprite;

public class Button extends Sprite {

    private boolean soundPlayed;
    private boolean visibleOnScreen;

    public Button(String fileName) {
        super(fileName, 2);
        setCurrFrame(0);
    }

    public boolean isOnScreen() {
        return visibleOnScreen;
    }

    public void setOnScreen(boolean onScreen) {
        this.visibleOnScreen = onScreen;
    }

    public boolean hasPlayed() {
        return soundPlayed;
    }

    public void soundWasPlayed() {
        this.soundPlayed = true;
    }

    public boolean state(Mouse mouse) {

        if (mouse.isOverObject(this) && visibleOnScreen) {
            setCurrFrame(1);
            return true;
        } else {
            setCurrFrame(0);
            soundPlayed = false;
            return false;
        }
    }
}
