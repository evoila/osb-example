#!/bin/bash
cp tile.template.yml tile.yml
while read -r line; do replace="$(echo "$line" | awk '{print $1;}')"; with="$(echo "$line" | cut -d' ' -f2-)"; sed -i "s|label:[ \t]*$replace|label: $with|g" tile.yml; done < translate.txt
