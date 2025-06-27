#!/usr/bin/env bash


git remote set-url origin "git@github.com:${GITHUB_REPOSITORY}.git"
git fetch origin main
git fetch origin "${GITHUB_REF}"
git checkout "${GITHUB_REF}"


# Run spotless plugin to format code.
mvn spotless:apply

if [[ -n $(git status --porcelain) ]]; then
  echo "Spotless has formatted files."
  FORMATTED_FILES_COUNT=$(git ls-files --modified | wc -l)
  echo "$FORMATTED_FILES_COUNT files were formatted."
  git add .
else
  echo 'No files needed to be formatted.'
fi

echo 'Spotless code formatter step completed.'