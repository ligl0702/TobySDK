#!/bin/sh
#curl -sL https://ghproxy.com/https://raw.githubusercontent.com/ligl0702/TobySDK/master/make.sh | sh
opkg update
opkg install bash
wget -O /tmp/2500.sh https://ghproxy.com/https://raw.githubusercontent.com/ligl0702/TobySDK/master/2500.sh && chmod +x /tmp/2500.sh && /tmp/2500.sh
