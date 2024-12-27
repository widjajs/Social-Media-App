import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontLoader {
    public static Font antonFont;

    static {
        try {
            antonFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/images/Anton-Regular.ttf")).deriveFont(40f);
            antonFont = antonFont.deriveFont(Font.ITALIC);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(antonFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            antonFont = new Font("SansSerif", Font.PLAIN, 40); // Fallback
        }
    }
}