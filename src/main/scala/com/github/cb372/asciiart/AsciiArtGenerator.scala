package com.github.cb372.asciiart

import java.io._
import java.awt.image._

sealed trait OutputFormat
case object PrintToScreen extends OutputFormat
case class WriteToFile(file: File) extends OutputFormat

trait AsciiArtGenerator extends ImageScaler {
  self: ImageLoaderModule with AsciifierModule =>

  def run(inputUrl: String, outputWidth: Int, outputFormat: OutputFormat) {
    for {
      originalImage <- imageLoader.loadImage(inputUrl)
      scaledImage = scale(originalImage, outputWidth)
      ascii = asciifier.asciify(scaledImage)
    } yield
      outputFormat match {
        case PrintToScreen => println(ascii)
        case WriteToFile(file) => {
          val writer = new FileWriter(file)
          try {
            writer.write(ascii)
          } finally {
            writer.close()
          }
          println(s"Ascii art written to ${file.getAbsolutePath}")
        }
      }
  }

}
