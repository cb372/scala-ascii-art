package com.github.cb372.asciiart

import javax.imageio._
import java.awt.image._

import scala.util.{Try, Success, Failure}

trait ImageLoaderModule {
  val imageLoader: ImageLoader
}

trait ImageLoader {
  def loadImage(url: String): Option[BufferedImage]
}

class FileLoader extends ImageLoader {
  
  import java.io.File

  def loadImage(url: String) =
    Try {
      Option(ImageIO.read(new File(url)))
    } getOrElse(None)
}

class HttpLoader extends ImageLoader {

  import dispatch._
  import java.io.ByteArrayInputStream

  def loadImage(inputUrl: String): Option[BufferedImage] =
    Try {
      val u = url(inputUrl)
      val bytes = Http(u OK as.Bytes)() // blocks
      Option(ImageIO.read(new ByteArrayInputStream(bytes)))
    } getOrElse(None)

}
