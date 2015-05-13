#!/bin/bash
# Javauto uninstall script for Linux

# make sure it's run as root
if [ "$(id -u)" != "0" ]; then
	echo "This script must be run as root" 1>&2
	exit 1
fi


# remove directry
if [ -d "/etc/javauto" ]; then
	echo "rm -rf /etc/javauto/"
	rm -rf /etc/javauto/
fi

# remove links
if [ -h "/bin/javauto" ]; then
	echo "rm /bin/javauto"
	rm /bin/javauto
fi
if [ -h "/bin/javauto-helper" ]; then
	echo "rm /bin/javauto-helper"
	rm /bin/javauto-helper
fi
if [ -h "/bin/javauto-lookup" ]; then
	echo "rm /bin/javauto-lookup"
	rm /bin/javauto-lookup
fi

echo "Uninstall complete."
