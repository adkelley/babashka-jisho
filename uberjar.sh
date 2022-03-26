#!/bin/bash
# Make an uberjar
bb -cp $(clojure -A:remove-clojure -Spath) uberjar jisho.jar -m jisho.main
