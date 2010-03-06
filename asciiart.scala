import scala.io._
import scala.Math._
import scala.xml._
import java.io._
import javax.imageio._
import javax.swing.ImageIcon
import java.awt.{Color, RenderingHints}
import java.awt.image._

object AsciiArt {

	val asciiChars = List(
			"#","A","@","%",
			"$","+","=","*",
			":",",",".","&nbsp;")

	def rgbMax(red:Int, green:Int, blue:Int) = 
	List(red, green, blue).reduceLeft(max) 

	def chooseChar(rgbPeak:Double) = rgbPeak match {
		case 0 => asciiChars.last
		case n => {
			val index = ((asciiChars.length * (rgbPeak / 255)) - (0.5)).toInt
			asciiChars(index)
		}
	}

	def charToSpan(red:Int, green:Int, blue:Int, char:String) = 
		// use Unparsed() to preserve "&nbsp;"
		<span style={String.format("display:inline; color: rgb(%s, %s, %s)", red.toString, green.toString, blue.toString)}>{ Unparsed(char) }</span>

	def pixelToSpan(red:Int,green:Int,blue:Int) =
		charToSpan(red,green,blue,chooseChar(rgbMax(red,green,blue)))

	def scaleImage(image:BufferedImage, width:Int) = {
		val height = ((width.toDouble / image.getWidth) * image.getHeight).toInt
		val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val gfx = scaledImage.createGraphics()
		gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		gfx.drawImage(image, 0, 0, width, height, null)
		gfx.dispose
		scaledImage
	}

	def convertImage(image:BufferedImage) = {
		val h = image.getHeight()
		val w = image.getWidth()
		for {y <- 0 to h-1} yield {
			for {x <- 0 to w-1} yield {
				val pixel = new Color(image.getRGB(x, y))
				pixelToSpan(pixel.getRed, pixel.getGreen, pixel.getBlue) 
			}
		}
	}

	def main(args: Array[String]) {
		val inputFile = args(0)
		val outputFile = args(1)
		var outputWidth = 150
		if (args.length > 2)
			outputWidth = args(2).toInt 

		val image = scaleImage(ImageIO.read(new File(inputFile)), outputWidth)
		val spans = convertImage(image)
		val html = 
		<html>
			<body>
				<p style="
					font-family:Courier,monospace;
					font-size:5pt;
					letter-spacing:1px;
					line-height:4pt;
					font-weight:bold">
					{ spans.map(xml => xml ++ <br/>) }
				</p>
			</body>
		</html>

		//XML.save("/home/chris/scalasandbox/tamori2.html", html)
		// Can't find a way to stop Scala replacing "<br/>" with "<br></br>",
		// which messes up the image in the browser, so we have to restore the self-closing tag.
		val writer = new FileWriter(outputFile)
		try {
			writer.write(Xhtml.toXhtml(html, false, false).replace("<br></br>","<br/>"))
		} finally {
			writer.close()
		}
	}

}
