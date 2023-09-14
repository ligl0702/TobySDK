#!/bin/bash
opkg update
opkg install bash
proxy_github="https://ghproxy.com/"
setup_base_init() {

	#添加作者信息
	add_author_info

	#添加安卓时间服务器
	add_dhcp_domain

	##设置时区
	uci set system.@system[0].zonename='Asia/Shanghai'
	uci set system.@system[0].timezone='CST-8'
	uci commit system
	/etc/init.d/system reload

	## 设置防火墙wan 打开
	uci set firewall.@zone[1].input='ACCEPT'
	uci commit firewall

}

## 安装应用商店
install_istore() {
	##设置Argon 紫色主题 并且 设置第三方软件源
	setup_software_source 1
	opkg install luci-app-argon-config
	uci set luci.main.mediaurlbase='/luci-static/argon'
	uci set luci.main.lang='zh_cn'
	uci commit

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
	##安装完毕之后 还原软件源
	setup_software_source 0
	requiredInstallation
}
#设置风扇工作温度
setup_cpu_fans() {
	##Modify the starting cpu temperature for fan work
	cd /tmp
	sed -i 's/76/48/g' /etc/config/glfan
}
#设置第三方软件源
setup_software_source() {
	## 传入0和1 分别代表原始和第三方软件源
	if [ "$1" -eq 0 ]; then
		echo "# add your custom package feeds here" >/etc/opkg/customfeeds.conf
		# 在这里执行与选项0相关的操作
		opkg update
	elif [ "$1" -eq 1 ]; then
		echo "src/gz supes https://op.dllkids.xyz/packages/aarch64_cortex-a53" >>/etc/opkg/customfeeds.conf
		# 在这里执行与选项1相关的操作
		opkg update
	else
		echo "Invalid option. Please provide 0 or 1."
	fi
}

is_x86_64_router() {
	arch=$(uname -m)
	if [ "$arch" = "x86_64" ]; then
		return 0
	else
		return 1
	fi
}
# 安装run app
install_run_apps() {
	cd /tmp
	arm_base_url=${proxy_github}"https://raw.githubusercontent.com/AUK9527/Are-u-ok/main/apps/all/"
	armapps=("OpenClash_a53_update.run" "PassWall_a53_update.run" "SSR-Plus_a53_update.run" "PassWall2_a53_all.run" "VSSR_a53.run" "ByPass_a53.run")
	base_apps=("${armapps[@]}") # 使用双引号和 @ 符号来复制数组
	if [ $# -eq 1 ] && [[ "$1" =~ ^[0-9]+$ ]]; then
		num_to_install=$1 # 传入的参数是一个数字，表示要安装的数量
		for ((i = 0; i < num_to_install; i++)); do
			run="${base_apps[i]}"
			if [ -e "$run" ]; then
				echo "$run 已存在,跳过下载,直接安装"
			else
				wget -O "$run" "$arm_base_url$run"
			fi
			sh "$run"
		done
	elif [ $# -eq 2 ] && [[ "$1" =~ ^[0-9]+$ ]] && [[ "$2" =~ ^[0-9]+$ ]]; then
		index_to_execute=$2 # 传入的参数是两个数字，第二个数字表示要执行的数组索引
		if [ "$index_to_execute" -ge 0 ] && [ "$index_to_execute" -lt ${#base_apps[@]} ]; then
			run="${base_apps[index_to_execute]}"
			if [ -e "$run" ]; then
				echo "$run 已存在,跳过下载,直接安装"
			else
				wget -O "$run" "$base_url$run"
			fi
			sh "$run"
		else
			echo "索引超出范围"
		fi
	else
		echo "请提供正确的参数：一个数字（安装数量）或两个数字（第一个数字不作数，第二个数字代表数组下标）"
	fi

	#更新clash 内核 和 OpenClash客户端版本
	upgrade_clash_core
	upgrade_openclash
}

## 升级clash core版本
upgrade_clash_core() {
	echo -e "***********正在升级 Clash Core 内核到最新版*************************"
	dev_base_url=$proxy_github"https://raw.githubusercontent.com/vernesong/OpenClash/core/master/dev/"
	meta_base_url=$proxy_github"https://raw.githubusercontent.com/vernesong/OpenClash/core/master/meta/"
	dev_filename_arm="clash-linux-arm64.tar.gz"
	meta_filename_arm=$dev_filename_arm
	## 下载ARM Dev arm 内核
	wget -O clashdev.tar.gz $dev_base_url$dev_filename_arm
	## 下载ARM Tun arm 内核
	wget -O clashtun.gz $proxy_github"https://raw.githubusercontent.com/wukongdaily/allinonescript/main/arm64/tun.gz"
	## 下载ARM meta arm内核
	wget -O clashmeta.tar.gz $meta_base_url$meta_filename_arm

	# 解压 clashdev.tar.gz 到 /etc/openclash/core/clash 目录
	tar -xzvf /tmp/clashdev.tar.gz -C /etc/openclash/core/
	#解压 clashtun.gz 文件到 /etc/openclash/core/ 目录，并设置可执行权限
	gzip -d -c /tmp/clashtun.gz >/etc/openclash/core/clash_tun && chmod +x /etc/openclash/core/clash_tun
	#解压 meta内核 文件到 /etc/openclash/core/meta/
	mkdir -p /etc/openclash/core/meta/
	tar -xzvf /tmp/clashmeta.tar.gz -C /etc/openclash/core/meta/
	mv /etc/openclash/core/meta/clash /etc/openclash/core/clash_meta
	rm -rf /etc/openclash/core/meta/
}

## 升级openclash客户端版本
upgrade_openclash() {
	echo -e "\n\n*********** 正在升级 Openclash 客户端到最新版 ***********\n"
	setup_software_source 1
	opkg install luci-app-openclash
	setup_software_source 0
}

# 添加主机名映射(解决安卓原生TV首次连不上wifi的问题)
add_dhcp_domain() {
	local domain_name="time.android.com"
	local domain_ip="203.107.6.88"

	# 检查是否存在相同的域名记录
	existing_records=$(uci show dhcp | grep "dhcp.@domain\[[0-9]\+\].name='$domain_name'")
	if [ -z "$existing_records" ]; then
		# 添加新的域名记录
		uci add dhcp domain
		uci set "dhcp.@domain[-1].name=$domain_name"
		uci set "dhcp.@domain[-1].ip=$domain_ip"
		uci commit dhcp
		echo
		echo "已添加新的域名记录"
	else
		echo "相同的域名记录已存在，无需重复添加"
	fi
	echo -e "\n"
	echo -e "time.android.com    203.107.6.88 "
}

# 添加emotn域名
add_emotn_domain() {
	echo -e "\n\n"
	# 检查 passwall 的代理域名文件是否存在
	if [ -f "/usr/share/passwall/rules/proxy_host" ]; then
		sed -i "s/keeflys.com//g" "/usr/share/passwall/rules/proxy_host"
		echo -n "keeflys.com" | tee -a /usr/share/passwall/rules/proxy_host
		echo "已添加到passwall代理域名"
	else
		echo "添加失败! 请确保 passwall 已安装"
	fi

	# 检查 SSRP 的黑名单文件是否存在
	if [ -f "/etc/ssrplus/black.list" ]; then
		sed -i "s/keeflys.com//g" "/etc/ssrplus/black.list"
		echo -n "keeflys.com" | tee -a /etc/ssrplus/black.list
		echo "已添加到SSRP强制域名代理"
	else
		echo "添加失败! 请确保 SSRP 已安装"
	fi

	echo -e "\n\n"
}

#装机必备
requiredInstallation() {

	is-opkg do_self_upgrade
	is-opkg install 'app-meta-ddnsto'
	is-opkg install 'app-meta-linkease'
}

#添加作者信息
add_author_info() {
	cd /tmp
	uci set system.@system[0].description='wukongdaily'
	uci set system.@system[0].notes='插件的使用方法详见:
    https://didiboy0702.gitbook.io/ruan-lu-you-shi-yong-zhi-nan/passwall'
	uci commit system
}

## 一键脚本
run_all_in_one_script() {
	## 先判断是否为x86_64,再判断num_scripts
	num_scripts=$1
	if is_x86_64_router; then
		if [ "$num_scripts" -eq 3 ]; then
			wget -O /tmp/OPS.run ${proxy_github}https://raw.githubusercontent.com/wukongdaily/allinonescript/main/x86/OPS.run && chmod +x /tmp/OPS.run && /tmp/OPS.run
		else
			wget -O /tmp/OPSPVB.run ${proxy_github}https://raw.githubusercontent.com/wukongdaily/allinonescript/main/x86/OPSPVB.run && chmod +x /tmp/OPSPVB.run && /tmp/OPSPVB.run
		fi
	else
		if [ "$num_scripts" -eq 3 ]; then
			wget -O /tmp/OPS.run ${proxy_github}https://raw.githubusercontent.com/wukongdaily/allinonescript/main/arm64/OPS.run && chmod +x /tmp/OPS.run && /tmp/OPS.run
		else
			wget -O /tmp/OPSPVB.run ${proxy_github}https://raw.githubusercontent.com/wukongdaily/allinonescript/main/arm64/OPSPVB.run && chmod +x /tmp/OPSPVB.run && /tmp/OPSPVB.run
		fi
	fi
}

##获取软路由型号信息
get_router_name() {
	if is_x86_64_router; then
		model_name=$(grep "model name" /proc/cpuinfo | head -n 1 | awk -F: '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//')
		echo "$model_name"
	else
		model_info=$(cat /tmp/sysinfo/model)
		echo "$model_info"
	fi
}

while true; do
	clear
	echo "***********************************************************************"
	echo "*      一键安装工具箱(for gl-inet Router) v1.0        "
	echo "*      Developed by @wukongdaily Youtube        "
	echo "**********************************************************************"
	echo
	echo "*      当前的软路由型号: $(get_router_name)"
	echo
	echo "**********************************************************************"
	echo
	echo " 1. MT2500一键初始化脚本"
	echo " 2. MT3000一键初始化脚本"
	echo " q. 退出本程序"
	echo
	read -p "请选择一个选项: " choice

	case $choice in
	1)
		echo "MT2500一键初始化脚本"
		#基础必备设置
		setup_base_init
		#安装Argon主题和iStore商店风格
		install_istore
		#三大插件
		install_run_apps 3
		;;
	2)
		echo "MT3000一键初始化脚本"
		#设置风扇工作温度
		setup_cpu_fans
		#基础必备设置
		setup_base_init
		#安装Argon主题和iStore商店风格
		install_istore
		#三大插件
		install_run_apps 3
		;;

	q | Q)
		echo "退出"
		exit 0
		;;
	*)
		echo "无效选项，请重新选择。"
		;;
	esac

	read -p "按 Enter 键继续..."
done
