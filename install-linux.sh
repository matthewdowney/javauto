#!/bin/bash
# Javauto install script for Linux

# make sure it's run as root
if [ "$(id -u)" != "0" ]; then
	echo "This script must be run as root" 1>&2
	exit 1
fi

# if javauto already exists get rid of it
if [ -h "/bin/javauto" ]; then
	set -x
	rm /bin/javauto
	set +x
fi
if [ -h "/bin/javauto-helper" ]; then
	set -x
	rm /bin/javauto-helper
	set +x
fi
if [ -h "/bin/javauto-lookup" ]; then
	set -x
	rm /bin/javauto-lookup
	set +x
fi
if [ -d "/etc/javauto" ]; then
	set -x
	rm -rf /etc/javauto
	set +x
fi

# set verbose
set -x

# install javauto
mkdir /etc/javauto
cp jars/javauto.jar /etc/javauto/
cp jars/javauto-helper.jar /etc/javauto/
cp jars/javauto-lookup.jar /etc/javauto/

# create our scripts to run it
touch /etc/javauto/javauto
touch /etc/javauto/javauto-helper
touch /etc/javauto/javauto-lookup
echo 'java -jar /etc/javauto/javauto.jar "$@"' > /etc/javauto/javauto
echo 'java -jar /etc/javauto/javauto-helper.jar "$@"' > /etc/javauto/javauto-helper
echo 'java -jar /etc/javauto/javauto-lookup.jar "$@"' > /etc/javauto/javauto-lookup

# make executable
chmod +x /etc/javauto/javauto*

# make links to bin so it can be run from anywhere
ln -s /etc/javauto/javauto /bin/javauto
ln -s /etc/javauto/javauto-helper /bin/javauto-helper
ln -s /etc/javauto/javauto-lookup /bin/javauto-lookup
