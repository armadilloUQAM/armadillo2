#!/bin/sh

###################################################################
# This file update this distribution of the armadillo src via svn 
###################################################################

#############################
# Update src folder
#############################
echo "Updating src folder..."
cd src 
svn up
cd ..
#############################
# Update data folder
#############################
echo "Updating data folder..."
cd data
svn up
cd ..
#############################
# Update lib folder
#############################
echo "Updating lib folder..."
cd lib
svn up
cd ..
#############################
# Update examples folder
#############################
echo "Updating examples folder..."
cd examples
svn up
cd ..
#############################
# Update executable folder
#############################
echo "Updating executable folder..."
cd executable
svn up
cd ..

#############################
# Done
#############################
echo "Done."
