package com.github.cb372.asciiart

import javax.imageio._
import java.awt.{Color, RenderingHints}
import java.awt.image._

trait ImageScaler {

  def scale(image: BufferedImage, width: Int): BufferedImage = {
		val height = ((width.toDouble / image.getWidth) * image.getHeight).toInt
		val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val gfx = scaledImage.createGraphics()
		gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		gfx.drawImage(image, 0, 0, width, height, null)
		gfx.dispose
		scaledImage
	}
  
}

