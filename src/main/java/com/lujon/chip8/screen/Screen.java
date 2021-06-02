package com.lujon.chip8.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Screen {

  private boolean[][] pixels = new boolean[32][64];

  public int getWidth() {
    return pixels[0].length;
  }

  public int getHeight() {
    return pixels.length;
  }

  public boolean getPixel(int x, int y) {
    return pixels[y][x];
  }

  public void setPixel(int x, int y, boolean pixelOn) {
    pixels[y][x] = pixelOn;
  }

  public void clear() {
    pixels = new boolean[32][64];
  }

  public void show() {

    BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        image.setRGB(x, y, pixels[y][x] ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
      }
    }

    BufferedImage after = new BufferedImage(getWidth()*10, getHeight()*10, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(10.0, 10.0);
    AffineTransformOp scaleOp =
        new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    after = scaleOp.filter(image, after);

    JFrame editorFrame = new JFrame("Chip-8");
    editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    ImageIcon imageIcon = new ImageIcon(after);
    JLabel jLabel = new JLabel();
    jLabel.setIcon(imageIcon);
    editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);

    editorFrame.pack();
    editorFrame.setLocationRelativeTo(null);
    editorFrame.setVisible(true);

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Screen screen = (Screen) o;
    return Arrays.equals(pixels, screen.pixels);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(pixels);
  }
}
