# scala-ascii-art

A simple Ascii art generator in Scala.

I wrote this to help me learn Scala syntax, Java interop and basic programming style. The algorithm itself is just a ripoff from this blog:

http://www.bestinclass.dk/index.php/2010/02/my-tribute-to-steve-ballmer/

## Build

    sbt one-jar

## Run

    ./run.sh <args>

Pass either a local file or a URL:

    ./run.sh photos/foo.jpg

    ./run.sh http://www.example.com/bar.png

Optionally pass an output file to save your amazing Ascii art for posterity. Save as either text or HTML.

    ./run.sh foo.jpg output.txt

    ./run.sh bar.jpg output.html

If one Ascii art is not enough for you, use Flickr to generate an endless (ahem, not actually endless) stream of colourful characters.

    ./run.sh flick cats
