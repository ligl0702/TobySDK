#!/bin/sh

# 获取 model name 的值
model_name=$(cat /proc/cpuinfo | grep "model name" | head -n 1 | cut -d ':' -f 2 | sed -e 's/^[ \t]*//')

# 检查 model name 的值是否是 "Intel(R) N100"
if [ "$model_name" = "Intel(R) N100" ]; then
    echo "n100"
else
    echo "other cpu"
fi
