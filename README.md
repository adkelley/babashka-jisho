# Babashka-Jisho
## Summary
A clojure [babashka](https://github.com/babashka/babashka#readme) script that queries the [jisho api](https://jisho.org/api/v1/search/words?keyword=%E8%A1%A8) for fields of interest.
 
## Installation
1. Install [babashka](https://github.com/babashka/babashka)
2. Clone this repository
3. Create an uberjar prior to running babashka

```$ bb -cp $(clojure -A:remove-clojure -Spath) uberjar jisho.jar -m jisho.main```

--or--

Use the bash script provided to avoid typing mistakes

```$ ./uberjar.sh```

## Usage (cli) 

`$ bb jisho.jar <word> <argument>`

If no arguments, beside the word, is specified then return the parts of speech.

Examples:

`$ bb jisho.jar query -j` will return the japanese word for 'query' => 問い  [とい]

`$ bb jisho.jar 問い -e` will return the english definition for '問い' => query

`$ bb jisho.jar 問い -r` will return the japanese reading for '問い' => とい

`$ bb jisho.jar 問い -l` will return the jlpt level for '問い' => 3. If no jlpt level exists then returns the empty string.

`$ bb jisho.jar 問い -p` (default argument) will return the parts of speech for '問い' => Noun

`$ bb jisho.jar 問い -d` List all the english definitions for each of the senses.


## Usage (TextExpander script)
Create the following shell script snippit in [TextExpander](https://textexpander.com/):
```bash
#!/bin/bash
args=$(pbpaste)
cd path/to/jisho
/path/to/bin/bb jisho.jar $args
```
then copy the query arguments into your clipboard and run the shell script snippit. See 'Usage (Babashka)' for more details.

## Usage (Babashka)
This project was structured to work with the shell script feature in [TextExpander](https://textexpander.com/), which apparently doesn't like ```bb.edn``` files.  Thus, if a `bb.edn` is present in the jisho directory, the TextExpander shellscript given above will not work. The workaround is to create a JAR file (see 'Installation') and call jisho from the cli or from TextExpander shell script as described in the previous sections.

This is not the standard approach to using Babashka.  If you're __not__ interested in combining jisho with TextExpander then do the following during installation or later:

```bash
$ mv deps.edn bb.edn
$ rm jisho.jar
$ bb -m jisho.main <word> <argument>
```

## TODO:
1. Give the ability to list all the related words and meanings
2. Select a particular related word and/or meaning


In previous implementations, Item 2 from the cli was implemented using the following approach:

Optional integer arguments are the slug index and the senses index. Note, the senses index must be accompanied by the slug index

```$ bb jisho.jar 問い 2 -e``` Return the first english definition for the second slug for the query '問い'. The first slug is returned by default, thus it's not necessary to type `-e 1` explicitly.

```$ bb jisho.jar circle 1 -e 2``` Return the second english definition from the first slug in the map for the query 'circle'. 
