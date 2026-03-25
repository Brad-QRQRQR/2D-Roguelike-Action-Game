package app.data.scripts.engine.tools;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class TransparentImage {
    public static Image convert(Image inputImage, Color targetColor) {
        int width = (int)inputImage.getWidth();
        int height = (int)inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                if (color.equals(targetColor)) {
                    writer.setColor(x, y, Color.TRANSPARENT);
                }else {
                    writer.setColor(x, y, color);
                }
            }
        }

        return outputImage;
    }
}
