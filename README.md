# Jisho
## Summary
A clojure [babashka](https://github.com/babashka/babashka#readme) script that queries the [jisho api](https://jisho.org/api/v1/search/words?keyword=%E8%A1%A8) for fields of interest.
 
## Installation
1. Install [babashka](https://github.com/babashka/babashka#installation)
2. Clone this repository
3. Run the script using any of the valid arguments listed below

## Valid arguments
`$ bb -m jisho.main query -j` will return the japanese word for query (i.e., 問い)

`$ bb -m jisho.main 問い -e` will return the english definition for 問い (i.e, query)

`$ bb -m jisho.maim 問い -l` will return the jlpt level for 問い (i.e, query)

`$ bb -m jisho.main 問い -p` will return the parts of speech for 問い (i.e, query)

`$ bb -m jisho.main 問い -d` List all the english definitions for each of the senses.

Optional integer arguments are the slug index and the senses index. Note, the senses index must be accompanied by the slug index

`$ bb -m jisho.main 問い 1 -e` Return the first english definition from the first slug in the map for the query '問い'.

`$ bb -m jisho.main circle 1 -e 2` Return the second english definition from the first slug in the map for the query 'circle'. 

## Wish List
