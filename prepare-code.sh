#!/usr/bin/env bash

chmod +x ./format-code.sh

./format-code.sh

files_modified="$(git diff --quiet && git diff --staged --quiet)"

if $files_modified ; then
  echo "Committing files modified during prepare code step"
  # Commit the changes here
  git commit -m "Committing files modified during prepare code step"
  git push
else
  echo 'No files needs to be committed'
fi

echo 'Prepare code step complete'