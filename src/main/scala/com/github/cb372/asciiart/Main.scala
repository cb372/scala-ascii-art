package com.github.cb372.asciiart

import java.io.File
import scala.xml._

object Main extends App {

  args.toList match {
    case "flickr" :: keyword :: width :: _ => {
      val generator = buildGenerator(new HttpLoader, new PlainTextAsciifier)
      val imageUrls: Seq[String] = getFlickrSearchResults(keyword)
      for (url <- imageUrls) {
        generator.run(url, width.toInt, PrintToScreen)
        println()
        Thread.sleep(500)
      }
    }
    case url :: width :: xs => {
      val loader = chooseLoader(url)
      val outputFormat = chooseOutputFormat(xs)
      val asciifier = chooseAsciifier(outputFormat)
      val generator = buildGenerator(loader, asciifier)
        generator.run(url, width.toInt, outputFormat)
    }
    case _ => printUsage()
  }

  def printUsage() {
    println("Usage:") 
    println("Main flickr keyword                        # grab a new image from Flickr every few seconds")
    println("Main inputUrl outputWidth                  # print to screen")
    println("Main inputUrl outputWidth outputFile.html  # output to HTML file")
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

  def chooseOutputFormat(args: Seq[String]): OutputFormat = args match {
    case Nil => PrintToScreen
    case filename :: xs => WriteToFile(new File(filename))
  }

  def chooseAsciifier(outputFormat: OutputFormat): Asciifier = outputFormat match {
    case PrintToScreen => new PlainTextAsciifier
    case WriteToFile(file) if file.getName.endsWith(".html") => new HtmlAsciifier
    case WriteToFile(file) => new PlainTextAsciifier
  }

  def buildGenerator(loaderImpl: ImageLoader, asciifierImpl: Asciifier): AsciiArtGenerator = {
    // bake the cake!
    new AsciiArtGenerator 
               with ImageLoaderModule 
               with AsciifierModule { 
      val imageLoader = loaderImpl
      val asciifier = asciifierImpl 
    }
  }
}
