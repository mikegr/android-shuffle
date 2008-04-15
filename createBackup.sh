#!/bin/sh

# Create a backup tar gzip file of the cwd with the date appended
# located in the parent directory.

FILENAME=../`pwd|xargs basename`-`date -j "+%Y-%m-%d"`.tgz

echo Will create $FILENAME
tar cfz $FILENAME .
echo Done.

