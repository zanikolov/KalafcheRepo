package com.kalafche.service.fileutil;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class ImageResizeService {

	private static final int IMG_WIDTH = 1280;
	
	private static final int IMG_HEIGHT = 960;
	
	public File resizeImage(File image) {
		BufferedImage origImage;
		try {

			origImage = ImageIO.read(image);
			int type = origImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : origImage.getType();

			int fHeight = IMG_HEIGHT;
			int fWidth = IMG_WIDTH;

			//Work out the resized width/height
			if (origImage.getHeight() > IMG_HEIGHT || origImage.getWidth() > IMG_WIDTH) {
				float ratio;
				if (origImage.getHeight() > origImage.getWidth()) {
					ratio = (float)origImage.getWidth() / (float)origImage.getHeight();
					fHeight = IMG_HEIGHT;
					fWidth = Math.round(fHeight * ratio);
				} else {
					ratio = (float)origImage.getHeight() / (float)origImage.getWidth();
					fWidth = IMG_WIDTH;
					fHeight = Math.round(fWidth * ratio);
				}
			}

			
			BufferedImage resizedImage = new BufferedImage(fWidth, fHeight, type);
			Graphics2D g = resizedImage.createGraphics();
			g.setComposite(AlphaComposite.Src);

			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawImage(origImage, 0, 0, fWidth, fHeight, null);
			g.dispose();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "jpg", baos);
			baos.flush();
			File out = new File("test");
			FileUtils.writeByteArrayToFile(out, baos.toByteArray());
			baos.close();
			return out;

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
}
