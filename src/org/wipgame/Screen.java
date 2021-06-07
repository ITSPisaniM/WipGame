package org.wipgame;

import java.util.List;
import java.awt.Color;
import java.io.Serializable;

public class Screen implements Serializable {
	private static final long serialVersionUID = 2705172041950251807L;

	private int[][] map;
	private int width;
	private int height;
	private List<Texture> textures;

	public Screen(int[][] map, List<Texture> textures, int width, int height) {
		this.map = map;
		this.textures = textures;
		this.width = width;
		this.height = height;
	}

	public int[] update(Camera camera, int[] pixels) {
		for (int n = 0; n < pixels.length / 2; n++) {
			if (pixels[n] != Color.DARK_GRAY.getRGB())
				pixels[n] = Color.DARK_GRAY.getRGB();
		}
		for (int i = pixels.length / 2; i < pixels.length; i++) {
			if (pixels[i] != Color.gray.getRGB())
				pixels[i] = Color.gray.getRGB();
		}
		for (int x = 0; x < width; x = x + 1) {
			double cameraX = 2 * x / (double) (width) - 1;
			double rayDirX = camera.getXDir() + camera.getXPlane() * cameraX;
			double rayDirY = camera.getYDir() + camera.getYPlane() * cameraX;
			// Posizione mappa
			int mapX = (int) camera.getXPos();
			int mapY = (int) camera.getYPos();
			// Lunghezza del raggio al lato x o y
			double sideDistX;
			double sideDistY;
			// Lunghezza del ragio da un lato all'altro della mappa
			double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
			double perpWallDist;
			// Direzione da andare per x e y
			int stepX;
			int stepY;
			boolean hit = false;// muro colpito
			int side = 0;// muro colpito in verticale o diagonale
			// trovare la direzione dei passi
			if (rayDirX < 0) {
				stepX = -1;
				sideDistX = (camera.getXPos() - mapX) * deltaDistX;
			} else {
				stepX = 1;
				sideDistX = (mapX + 1.0 - camera.getXPos()) * deltaDistX;
			}
			if (rayDirY < 0) {
				stepY = -1;
				sideDistY = (camera.getYPos() - mapY) * deltaDistY;
			} else {
				stepY = 1;
				sideDistY = (mapY + 1.0 - camera.getYPos()) * deltaDistY;
			}
			// Loop per trovare dove il raggio colpisce il muro
			while (!hit) {
				// Prossimo quadrante
				if (sideDistX < sideDistY) {
					sideDistX += deltaDistX;
					mapX += stepX;
					side = 0;
				} else {
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1;
				}
				// Controlla se il raggio colpisce un muro
				if (map[mapX][mapY] > 0)
					hit = true;
			}
			// Calcola la distanza dal punto di impatto
			if (side == 0)
				perpWallDist = Math.abs((mapX - camera.getXPos() + (1 - (double) stepX) / 2) / rayDirX);
			else
				perpWallDist = Math.abs((mapY - camera.getYPos() + (1 - (double) stepY) / 2) / rayDirY);
			// Calcola l'altezza del muro colpito
			int lineHeight;
			if (perpWallDist > 0)
				lineHeight = Math.abs((int) (height / perpWallDist));
			else
				lineHeight = height;
			// Calcola il pi� basso � il pi� alto pixel da riempire
			int drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0)
				drawStart = 0;
			int drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height)
				drawEnd = height - 1;
			// aggiungi texture
			int texNum = map[mapX][mapY] - 1;
			double wallX;// posizione esatta colpita
			if (side == 1) {// se � in muro sull'y
				wallX = (camera.getXPos() + ((mapY - camera.getYPos() + (1 - (double) stepY) / 2) / rayDirY) * rayDirX);
			} else {// muro sulla x
				wallX = (camera.getYPos() + ((mapX - camera.getXPos() + (1 - (double) stepX) / 2) / rayDirX) * rayDirY);
			}
			wallX -= Math.floor(wallX);
			// cordinate x della texture
			int texX = (int) (wallX * (textures.get(texNum).getSize()));
			if (side == 0 && rayDirX > 0)
				texX = textures.get(texNum).getSize() - texX - 1;
			if (side == 1 && rayDirY < 0)
				texX = textures.get(texNum).getSize() - texX - 1;
			// cordinate y della texture
			for (int y = drawStart; y < drawEnd; y++) {
				int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
				int color;
				if (side == 0)
					color = textures.get(texNum).getPixels()[texX + (texY * textures.get(texNum).getSize())];
				else
					color = (textures.get(texNum).getPixels()[texX + (texY * textures.get(texNum).getSize())] >> 1)
							& 8355711;// Make
				// y
				// sides
				// darker
				pixels[x + y * (width)] = color;
			}
		}
		return pixels;
	}
}
