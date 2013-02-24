#!/bin/sh

jar=$(ls target/scala-2.10/scala-ascii-art*-one-jar.jar)

java -Djava.awt.headless=true -jar ${jar} $*
