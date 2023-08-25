##Change the timezone
uci set system.@system[0].zonename='Asia/Shanghai'
uci set system.@system[0].timezone='CST-8'
uci commit system
/etc/init.d/system reload

##Add the 3rd party packages for argon theme
echo "src/gz supes https://op.dllkids.xyz/packages/aarch64_cortex-a53/" >> /etc/opkg/customfeeds.conf
opkg update
opkg install luci-app-argon-config
uci set luci.main.mediaurlbase='/luci-static/argon'
uci set luci.main.lang='zh_cn'
uci commit

##Add the hostname for Android TV/Google TV
uci add dhcp domain
uci set dhcp.@domain[-1].name='time.android.com'
uci set dhcp.@domain[-1].ip='203.107.6.88'
uci commit dhcp

## Modify the firewall input for wan interface
uci set firewall.@zone[1].input='ACCEPT'
uci commit firewall


##install luci-app-store
cd /tmp
wget https://istore.linkease.com/repo/all/store/taskd_1.0.3-1_all.ipk
wget https://istore.linkease.com/repo/all/store/luci-lib-xterm_4.18.0_all.ipk
wget https://istore.linkease.com/repo/all/store/luci-lib-taskd_1.0.18_all.ipk
wget https://istore.linkease.com/repo/all/store/luci-app-store_0.1.14-1_all.ipk
opkg install taskd_1.0.3-1_all.ipk
opkg install luci-lib-xterm_4.18.0_all.ipk
opkg install luci-lib-taskd_1.0.18_all.ipk
opkg install luci-app-store_0.1.14-1_all.ipk
opkg install luci-app-quickstart


##Remove the 3rd party packages source
echo "# add your custom package feeds here" > /etc/opkg/customfeeds.conf

##Modify the starting cpu temperature for fan work
cd /tmp
sed -i 's/76/48/g' /etc/config/glfan

##install the kxsw tools
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/apps/all/OpenClash_a53_update.run
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/apps/all/PassWall_a53_update.run
wget https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/apps/all/SSR-Plus_a53_update.run
sh OpenClash_a53_update.run
sh PassWall_a53_update.run
sh SSR-Plus_a53_update.run
cd ..

## reboot the mt3000 router
reboot




