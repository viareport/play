#!/bin/bash

pushd `dirname $0`

# --- clean SOURCES and copie app to build
rm -rf SOURCES/play-*
unzip ../framework/dist/play-*.zip -d SOURCES

APP_VERSION=`ls -d SOURCES/play* | sed "s@SOURCES/play-@@"`

# --- Application vairables
APP_NAME=play
APP_DIR=/opt/$APP_NAME
APP_USER=$APP_NAME

APP_LOGDIR=/var/log/$APP_NAME
APP_CONFIG=$APP_DIR/conf

APP_TMPDIR=$APP_DIR/tmp
APP_WORKDIR=/var/$APP_NAME

# --- Build variables
BUILD_DIR=/tmp/$APP_NAME

# --- CREATE SOURCE DIR
if [ ! -d SOURCES ]; then
	echo "Creating sources directory"
	mkdir SOURCES
fi

echo "Packaging $APP_NAME"

# --- Prepare directory
rm -rf  $BUILD_DIR TMP
mkdir -p $BUILD_DIR TMP
mkdir -p $BUILD_DIR/$APP_DIR

# --- Copy debian files to build
cp -R debian $BUILD_DIR/

# --- Replace placeholder
for DEBIANFILE in `ls SOURCES/app.*`; do
	debiandestfile=$APP_NAME${DEBIANFILE#SOURCES/app}
	cp $DEBIANFILE $BUILD_DIR/debian/$debiandestfile;
  sed -i "s|@@MYAPP_APP@@|$APP_NAME|g" $BUILD_DIR/debian/$debiandestfile
  sed -i "s|@@MYAPP_USER@@|$APP_USER|g" $BUILD_DIR/debian/$debiandestfile
  sed -i "s|@@MYAPP_VERSION@@|version $APP_VERSION release $APP_RELEASE|g" $BUILD_DIR/debian/$debiandestfile
  sed -i "s|@@MYAPP_EXEC@@|$APP_EXEC|g" $BUILD_DIR/debian/$debiandestfile
#  sed -i "s|@@MYAPP_LOGDIR@@|$APP_LOGDIR|g" $BUILD_DIR/debian/$debiandestfile
#  sed -i "s|@@APP_TMPDIR@@|$APP_TMPDIR|g" $BUILD_DIR/debian/$debiandestfile
done

cp SOURCES/control $BUILD_DIR/debian
sed -i "s|@@MYAPP_APP@@|$APP_NAME|g" $BUILD_DIR/debian/control
sed -i "s|@@MYAPP_APPVERSION@@|$APP_VERSION|g" $BUILD_DIR/debian/control

cp SOURCES/changelog $BUILD_DIR/debian
sed -i "s|@@MYAPP_APP@@|$APP_NAME|g" $BUILD_DIR/debian/changelog
sed -i "s|@@MYAPP_APPVERSION@@|$APP_VERSION|g" $BUILD_DIR/debian/changelog

# --- Copy our application in the build dir
cp -R SOURCES/play-$APP_VERSION/* $BUILD_DIR/$APP_DIR

# --- Create debian package
pushd $BUILD_DIR
dch -v $APP_VERSION --package $APP_NAME -m "release"
debuild -us -uc -B
popd

cp $BUILD_DIR/../$APP_NAME*.deb .

# --- Copy the .deb to the repo
repo=/var/www/repository/play

if [ -d $repo ]; then
    cp *.deb $repository
    rm *.deb
fi


popd
