#!/bin/bash
args=$(pbpaste)
cd $HOME/Dropbox/code/clojure/jisho
/opt/homebrew/bin/bb  jisho.jar -p $args | pbcopy | pbpaste
