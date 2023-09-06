## you can run the script
## curl -sL https://raw.githubusercontent.com/ligl0702/TobySDK/master/mt3000base.sh | sh

##Change the timezone
uci set system.@system[0].zonename='Asia/Shanghai'
uci set system.@system[0].timezone='CST-8'
uci commit system
/etc/init.d/system reload

setup_software_source() {
  ## 传入0和1 分别代表原始和第三方软件源
  if [ "$1" -eq 0 ]; then
    echo "# add your custom package feeds here" > /etc/opkg/customfeeds.conf
    # 在这里执行与选项0相关的操作
  elif [ "$1" -eq 1 ]; then
    echo "src/gz supes https://op.dllkids.xyz/packages/aarch64_cortex-a53" >> /etc/opkg/customfeeds.conf
    # 在这里执行与选项1相关的操作
  else
    echo "Invalid option. Please provide 0 or 1."
  fi
}

##Add the 3rd party packages for argon theme
setup_software_source 1
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
setup_software_source 0

##Modify the starting cpu temperature for fan work
cd /tmp
sed -i 's/76/48/g' /etc/config/glfan



## add keeflys.com for emotn store
sed -i "s/keeflys.com//g" "/usr/share/passwall/rules/proxy_host"
echo -n "keeflys.com" | tee -a /usr/share/passwall/rules/proxy_host
sed -i "s/keeflys.com//g" "/etc/ssrplus/black.list"
echo -n "keeflys.com" | tee -a /etc/ssrplus/black.list

##install ddnsto
is-opkg install 'app-meta-ddnsto'
is-opkg install 'app-meta-linkease'





