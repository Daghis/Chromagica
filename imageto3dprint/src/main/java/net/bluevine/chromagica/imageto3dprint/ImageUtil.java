package net.bluevine.chromagica.imageto3dprint;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtil {
  public static BufferedImage resizeImage(BufferedImage image, int maxSize) {
    // Get the original dimensions of the image
    int originalWidth = image.getWidth();
    int originalHeight = image.getHeight();
    double aspectRatio = (double) originalWidth / originalHeight;

    int newWidth;
    int newHeight;

    // Determine whether to scale by width or height
    if (originalWidth > originalHeight) {
      // Width is the larger dimension
      newWidth = maxSize;
      newHeight = (int) (maxSize / aspectRatio);
    } else {
      // Height is the larger dimension (or they are equal)
      newHeight = maxSize;
      newWidth = (int) (maxSize * aspectRatio);
    }

    // Create a new BufferedImage with transparency support
    BufferedImage resizedImage =
        new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

    // Create graphics with transparency support
    Graphics2D g = resizedImage.createGraphics();

    // Set up the graphics object for better quality
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw the original image onto the new image
    g.drawImage(image, 0, 0, newWidth, newHeight, null);
    g.dispose();

    return resizedImage;
  }
}
