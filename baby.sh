#!/bin/sh
##add some note
echo "一键安装三合一开始..."
uci set system.@system[0].description='wkopenwrt'
uci set system.@system[0].notes='插件的使用方法详见:
https://didiboy0702.gitbook.io/ruan-lu-you-shi-yong-zhi-nan/passwall'
uci commit system

echo "设置自定义域名劫持(主机名映射)..."
#add hostname for google tv
uci add dhcp domain
uci set dhcp.@domain[-1].name='time.android.com'
uci set dhcp.@domain[-1].ip='203.107.6.88'
uci commit dhcp

##install kxsw tools
echo "下载三大科学插件..."
cd /tmp
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/x86/all//OpenClash_x86_update.run
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/x86/all/PassWall_x86_update.run
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/x86/all/SSR-Plus_x86_update.run
echo "安装三大科学插件..."
sh OpenClash_x86_update.run
sh PassWall_x86_update.run
sh SSR-Plus_x86_update.run

## add keeflys.com for emotn store
echo "添加emotn store域名"
sed -i "s/keeflys.com//g" "/usr/share/passwall/rules/proxy_host"
echo -n "keeflys.com" | tee -a /usr/share/passwall/rules/proxy_host
sed -i "s/keeflys.com//g" "/etc/ssrplus/black.list"
echo -n "keeflys.com" | tee -a /etc/ssrplus/black.list

## install luci-app-poweroff and luci-app-ddnsto
echo "安装关机等必备插件"
is-opkg install 'app-meta-poweroff'
is-opkg install 'app-meta-ddnsto'
is-opkg install 'app-meta-systools'
echo "全部安装完毕"


