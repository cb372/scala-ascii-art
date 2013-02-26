package com.github.cb372.asciiart

import java.awt.image._
import java.awt.Color

trait AsciifierModule {
  val asciifier: Asciifier
}

trait Asciifier { 
	def asciify(image: BufferedImage): String
  val maxSensibleWidth: Int
  val maxSensibleHeight: Int
}

trait AsciifierUtil {
  val asciiChars = List('#','A','@','%','$','+','=','*',':',',','.',' ')

	def chooseChar(rgbMax: Double) = rgbMax match {
		case 0 => asciiChars.last
		case n => {
			val index = ((asciiChars.length * (rgbMax / 255)) - (0.5)).toInt
			asciiChars(index)
		}
	}

	def rgbMax(pixel: Color) = 
	  List(pixel.getRed, pixel.getGreen, pixel.getBlue).reduceLeft(Math.max) 

  def mapImage[A](image: BufferedImage)(f: Color => A): Seq[Seq[A]] = {
    (0 until image.getHeight) map { y =>
      (0 until image.getWidth) map { x =>
				val pixel = new Color(image.getRGB(x, y))
        f(pixel)
			}
		}
  }

}

class PlainTextAsciifier extends Asciifier with AsciifierUtil {
  val maxSensibleWidth = 120
  val maxSensibleHeight = 50

  import com.github.cb372.rainbow.{Rainbow, Colour}
  import Colour.spellColourProperly

  def asciify(image: BufferedImage) = {
    val lines: Seq[Seq[(Char, Color)]] = mapImage(image) { pixel =>
      val char = chooseChar(rgbMax(pixel))
      (char, pixel)
    }
    lines.map(line => "        " + Rainbow.rainbowify(line, escape = false)).mkString("\n")
  }

}

import scala.xml._

class HtmlAsciifier extends Asciifier with AsciifierUtil {
  val maxSensibleWidth = 800 
  val maxSensibleHeight = 600

  def asciify(image: BufferedImage) = {
    val lines = mapImage(image) { pixel =>
      val char = chooseChar(rgbMax(pixel))
      charToSpan(pixel.getRed, pixel.getGreen, pixel.getBlue, char)
    } 

		val html =
      <html>
        <body style="padding: 20px;">
          <p style="
            font-family:Courier,monospace;
            font-size:5pt;
            letter-spacing:1px;
            line-height:4pt;
            font-weight:bold">
            { lines.map(xml => xml ++ <br/>) }
          </p>
        </body>
      </html>

    val sb = new StringBuilder();
    Xhtml.toXhtml(html, sb = sb, minimizeTags = true)
    sb.toString
  }

	def charToSpan(red: Int, green: Int, blue: Int, char: Char): Node = {
    val string = toHtmlString(char) 
		// use Unparsed() to preserve "&nbsp;"
		<span style={String.format("display:inline; color: rgb(%s, %s, %s)", red.toString, green.toString, blue.toString)}>{ Unparsed(string) }</span>
  }

  def toHtmlString(char: Char) = char match {
    case ' ' => "&nbsp;"
    case c => c.toString
  }
}
