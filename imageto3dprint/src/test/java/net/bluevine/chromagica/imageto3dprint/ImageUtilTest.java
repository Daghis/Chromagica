package net.bluevine.chromagica.imageto3dprint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class ImageUtilTest {

  @Test
  void resizeImage_widthGreaterThanHeight() {
    BufferedImage image = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
    BufferedImage resized = ImageUtil.resizeImage(image, 20);

    assertEquals(20, resized.getWidth());
    assertEquals(10, resized.getHeight());
  }

  @Test
  void resizeImage_heightGreaterThanWidth() {
    BufferedImage image = new BufferedImage(50, 100, BufferedImage.TYPE_INT_ARGB);
    BufferedImage resized = ImageUtil.resizeImage(image, 20);

    assertEquals(10, resized.getWidth());
    assertEquals(20, resized.getHeight());
  }

  @Test
  void resizeImage_preservesAspectRatio() {
    BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
    BufferedImage resized = ImageUtil.resizeImage(image, 100);

    double originalAspect = (double) image.getWidth() / image.getHeight();
    double resizedAspect = (double) resized.getWidth() / resized.getHeight();

    assertEquals(originalAspect, resizedAspect, 0.01);
    assertTrue(resized.getWidth() == 100 || resized.getHeight() == 100);
  }
}
