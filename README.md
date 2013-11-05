# Codespeak Demo

A Clojure library designed to visualize some ways of reading and parsing text. 

## Installation

You will need to install Java, if you haven't, and [Leiningen](http://leiningen.org/).

## Usage

Run `lein repl`. The functions you can execute are listed below. All of them take a file path as input.

`read-bytes`, which takes a filename and prints the bytes.

`read-chars`, which takes a filename (better if it's a text file), and prints the characters.

`read-lines`, which prints a file line-by-line.

`read-tokens`, which splits a text file on whitespace and (not all) punctuation, and prints it token-by-token.

`sax-parse`, which performs SAX parsing on an XML file and prints events as they happen as well as the buffer of the underlying reader when it's reloaded.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
