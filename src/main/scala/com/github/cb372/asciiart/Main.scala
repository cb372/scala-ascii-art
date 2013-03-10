package com.github.cb372.asciiart

import java.io.File
import scala.xml._
import sbt._

class AppMain extends xsbti.AppMain {

  def run(configuration: xsbti.AppConfiguration): Exit =
    new Exit (
      try {
        Main.main(configuration.arguments)
        0
      } catch {
        case e: Throwable => 1
      }
    )
}

class Exit(val code: Int) extends xsbti.Exit

object Main {

  def main(args: Array[String]): Unit = {
    System.setProperty("java.awt.headless", "true")
    args.toList match {
      case "flickr" :: keyword :: _ => {
        val generator = buildGenerator(new HttpLoader, new PlainTextAsciifier)
        val imageUrls: Seq[String] = getFlickrSearchResults(keyword)
        for (url <- imageUrls) {
          generator.run(url, None, PrintToScreen)
          println()
          Thread.sleep(500)
        }
      }
      case url :: xs => {
        val loader = chooseLoader(url)
        val (outputFormat, widthSetting) = xs match {
          case Nil => (PrintToScreen, None)
          case Intejarr(width) :: _ => (PrintToScreen, Some(width))
          case filename :: Intejarr(width) :: _ => (WriteToFile(new File(filename)), Some(width))
          case filename :: _ => (WriteToFile(new File(filename)), None)
        }
        val asciifier = chooseAsciifier(outputFormat)
        val generator = buildGenerator(loader, asciifier)
        generator.run(url, widthSetting, outputFormat)
      }
      case _ => printUsage()
    }
  }

  // Extractor for integers
  object Intejarr {
    def unapply(x: String): Option[Int] =
      try {
        Some(x.toInt)
      } catch {
        case e: Exception => None
      }
  }

  def printUsage() {
    println("Usage:")
    println("Main flickr keyword                         # grab a new image from Flickr every few seconds")
    println("Main inputUrl [outputWidth]                 # print to screen")
    println("Main inputUrl outputFile.html [outputWidth] # output to HTML file")
  }

  def getFlickrSearchResults(keyword: String): Seq[String] = {
    import dispatch._

    val search = url(s"http://api.flickr.com/services/feeds/photos_public.gne?format=atom&tags=${keyword}")
    val results: Elem = Http(search OK as.xml.Elem)() 
    for {
      link <- results \\ "entry" \\ "link" filter(attributeValueExists("enclosure"))
      url <- link.attribute("href")
    } yield url.toString
  }

  def attributeValueExists(value: String)(node: Node) = {
     node.attributes.exists(_.value.text == value)
   }
  
  def chooseLoader(url: String): ImageLoader = 
      if (url.startsWith("http"))
        new HttpLoader
      else
        new FileLoader

  def chooseAsciifier(outputFormat: OutputFormat): Asciifier = outputFormat match {
    case PrintToScreen => new PlainTextAsciifier
    case WriteToFile(file) if file.getName.endsWith(".html") => new HtmlAsciifier
    case WriteToFile(file) => new PlainTextAsciifier
  }

  def buildGenerator(loaderImpl: ImageLoader, asciifierImpl: Asciifier): AsciiArtGenerator = {
    // bake the cake!
    new AsciiArtGenerator 
               with ImageLoaderModule 
               with ImageScalerModule 
               with AsciifierModule { 
      val imageLoader = loaderImpl
      val asciifier = asciifierImpl 
    }
  }
}
