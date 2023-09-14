#!/bin/sh
#curl -sL https://ghproxy.com/https://raw.githubusercontent.com/ligl0702/TobySDK/master/make.sh | sh
opkg update
opkg install bash
cd /tmp
wget -O 2500.sh https://raw.githubusercontent.com/ligl0702/TobySDK/master/2500.sh && chmod +x 2500.sh &&./2500.sh
