package org.wipgame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Texture implements Serializable {
	private static final long serialVersionUID = 2405172041955251807L;
	private int[] pixels;
	private String loc;
	private int size;
	private transient Logger logger;

	public int[] getPixels() {
		return this.pixels;
	}

	public int getSize() {
		return this.size;
	}

	public Texture(String location, int size) {
		loc = location;
		this.size = size;
		pixels = new int[this.size * this.size];
		load();
	}

	private void load() {
		try {
			BufferedImage image = ImageIO.read(new File(loc));
			int w = image.getWidth();
			int h = image.getHeight();
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			logger.warning("Could not load texture " + e.getMessage());
		}
	}

	public static final Texture wood = new Texture("res/wood.png", 64);
	public static final Texture brick = new Texture("res/redbrick.png", 64);
	public static final Texture bluestone = new Texture("res/bluestone.png", 64);
	public static final Texture stone = new Texture("res/greystone.png", 64);
}
