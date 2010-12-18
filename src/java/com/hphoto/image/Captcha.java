package com.hphoto.image;

import java.awt.image.BufferedImage;

public interface Captcha {
	void setRange(float width);
	BufferedImage getDistortedImage(BufferedImage image);
}
