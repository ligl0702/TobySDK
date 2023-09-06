## you can run the script
## curl -sL https://raw.githubusercontent.com/ligl0702/TobySDK/master/mt3000base.sh | sh
## curl -sL https://raw.kgithub.com/ligl0702/TobySDK/master/mt3000base.sh | sh

##Change the timezone
uci set system.@system[0].zonename='Asia/Shanghai'
uci set system.@system[0].timezone='CST-8'
uci commit system
/etc/init.d/system reload

##Modify the starting cpu temperature for fan work
cd /tmp
sed -i 's/76/48/g' /etc/config/glfan

## Modify the firewall input for wan interface
uci set firewall.@zone[1].input='ACCEPT'
uci commit firewall

setup_software_source() {
  
  if [ "$1" -eq 0 ]; then
    echo "# add your custom package feeds here" > /etc/opkg/customfeeds.conf
    
  elif [ "$1" -eq 1 ]; then
    echo "src/gz supes https://op.dllkids.xyz/packages/aarch64_cortex-a53" >> /etc/opkg/customfeeds.conf
    
  else
    echo "Invalid option. Please provide 0 or 1."
  fi
}

add_dhcp_domain() {
    local domain_name="time.android.com"
    local domain_ip="203.107.6.88"
    existing_records=$(uci show dhcp | grep "dhcp.@domain\[[0-9]\+\].name='$domain_name'")
    if [ -z "$existing_records" ]; then
        uci add dhcp domain
        uci set "dhcp.@domain[-1].name=$domain_name"
        uci set "dhcp.@domain[-1].ip=$domain_ip"
        uci commit dhcp
        echo
        echo "add domain success!"
    else
        echo "already add 203.107.6.88"
    fi
    echo -e "\n"
    echo -e "time.android.com    203.107.6.88 "
}

##Add the hostname for Android TV/Google TV
add_dhcp_domain

##Add the 3rd party packages for argon theme
setup_software_source 0
setup_software_source 1
opkg update
opkg install luci-app-argon-config
uci set luci.main.mediaurlbase='/luci-static/argon'
uci set luci.main.lang='zh_cn'
uci commit

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

##install ddnsto
is-opkg install 'app-meta-ddnsto'
is-opkg install 'app-meta-linkease'





