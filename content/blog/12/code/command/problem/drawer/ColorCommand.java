package drawer;

import java.awt.Color;
import java.awt.Point;

import command.*;

public class ColorCommand implements Command {
    protected Drawable drawable;
    private Color color;

    public ColorCommand(Drawable drawable, boolean colorToggle) {
        this.drawable = drawable;
        color = (colorToggle) ? Color.red : Color.blue;
    }

    public void execute() {
        drawable.setColor(color);
    }
}