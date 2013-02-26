package com.github.cb372.asciiart

import javax.imageio._
import java.awt.{Color, RenderingHints}
import java.awt.image._

trait ImageScalerModule {
  self: AsciifierModule =>

  def scale(image: BufferedImage, widthSetting: Option[Int]): BufferedImage = {
    val (width, height) = chooseDimensions(image, widthSetting)
		val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val gfx = scaledImage.createGraphics()
		gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		gfx.drawImage(image, 0, 0, width, height, null)
		gfx.dispose
		scaledImage
	}

  private def chooseDimensions(image: BufferedImage, widthSetting: Option[Int]): (Int, Int) = {
    widthSetting match {
      case Some(width) => {
        // use specified width, keep aspect ratio the same
		    val height = calcResizedHeight(image, width)
        (width, height)
      }
      case None => {
        if (image.getWidth <= asciifier.maxSensibleWidth && image.getHeight <= asciifier.maxSensibleHeight) {
          // never enlarge the original image
          (image.getWidth, image.getHeight)
        } else {
          // image is too tall and/or wide
          // try reducing the width, see if height is now ok
          val tryHeight = calcResizedHeight(image, asciifier.maxSensibleWidth)
          if (tryHeight <= asciifier.maxSensibleHeight) {
            (asciifier.maxSensibleWidth, tryHeight)
          } else {
            // reduce height, see if width is now ok
            val tryWidth = calcResizedWidth(image, asciifier.maxSensibleHeight)
            if (tryWidth <= asciifier.maxSensibleWidth) {
              (tryWidth, asciifier.maxSensibleHeight)
            } else {
              // give up, just make image as big as possible
              (asciifier.maxSensibleWidth, asciifier.maxSensibleHeight)
            }
          }
        }
      }
    }
  }

  private def calcResizedHeight(image: BufferedImage, resizedWidth: Int): Int =
    ((resizedWidth.toDouble / image.getWidth) * image.getHeight).toInt
  
  private def calcResizedWidth(image: BufferedImage, resizedHeight: Int): Int =
    ((resizedHeight.toDouble / image.getHeight) * image.getWidth).toInt
}

