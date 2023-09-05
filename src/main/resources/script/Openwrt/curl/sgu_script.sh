#!/bin/sh /etc/rc.common
# author: {}
# version: {}
# gitee: {}
# github: {}
START=99 #这里是启动优先级
STOP=15  #这里是停止优先级
username={}
password={}
LOG_FILE=/var/log/sgu_script.log
logger_interval=600 #这里是"网络连接正常"日志的间隔时间(每10分钟输出一次日志)
net_start=600 #网络开始供应时间(6.00)
net_end=2400 #网络结束供应时间(24.00)

# 定义函数，提取两个字符串之间的值
sub_between() {
    tmp="${1#*"$2"}"
    echo "${tmp%"$3"*}"
}

#登录认证
login() {
  if ping -c 2 www.baidu.com >/dev/null 2>&1; then
    if "$2"; then
      logger -t SGU-Script "网络连接正常"
    fi
    return 1
  else
    time_count="$logger_interval" #网络出错立即输出日志
    #动态获取ip地址
    redirect=$(curl -v -s http://1.1.1.1 2>&1)
    ip=$(sub_between "$redirect" "wlanuserip=" "&wlanacname")
    wlan=$(sub_between "$redirect" "wlanacname=" "&mac")
    #ip和wlan不为空则开始登录请求
    if [ -n "$ip" ] || [ -n "$wlan" ]; then
      response=$(curl -s -X GET "http://172.16.253.121/quickauth.do?userid=$username&passwd=$password&wlanuserip=$ip&wlanacname=$wlan")
      if [ -n "$response" ]; then
        result=$(sub_between "$response" "message\":\"" "\",\"wlanacIp")
        #不为认证成功有错误信息
        if [ "$result" != "认证成功" ]; then
          #是否是夜间禁止上网信息，或者账号未开通
          isILogin=$(echo "$result" | grep "在线")
          isNight=$(echo "$result" | grep "认证失败")
          isProxies=$(echo "$result" | grep "代理")
          if [ -n "$isNight" ]; then
            log "$1" "$result(可能原因有：夜间禁止上网信息，或者账号未开通)"
            sleep 60 #休息1分钟
          elif [ "$result" = "用户不存在,请输入正确的用户名!" ] || [ "$result" = "密码不匹配,请输入正确的密码!" ]; then
            log "$1" "$result(请重新使用正确的用户名和密码覆盖安装SGU-Script)"
            exit 1
          elif [ -n "$isILogin" ]; then
            log "$1" "$result(账号已经在线，请检查网络，若无网络，账号可能异地登录，请于自助中心下线设备并重启设备)"
            sleep 60 #休息1分钟
          elif [ -n "$isProxies" ]; then
            log "$1" "$result(可能原因有：使用代理软件，或者腾讯系软件/游戏的网络代理加速服务，请见[README]-[故障排除]-[第6条])"
            sleep 1800 #休息30分钟
          else
            log "$1" "$result"
            sleep 60 #休息1分钟
          fi
        else
          logger -t SGU-Script "锐捷网页认证成功"
        fi
      else
        log "$1" "请检查网线是否插紧,或者[安装教程]-[路由器设置]步骤是否正确"
      fi
    fi
  fi
}

#夜间默认不重连
night_net_reconnection=false
is_invoke_flag=false
#自动判断夜间是否有网络
is_night_have_net() {
  time_now=$(date '+%H%M') #获取当前时间，格式是时分，例如当前是上午8：50，time_now=850
  #网络结束供应时间过后1分钟
  net_check_time=$((net_end + 1 >= 2400 ? net_end + 1 - 2400 : net_end + 1))
  if [ "$time_now" -eq "$net_check_time" ]; then
    #没有执行过本方法，则执行
    if ! "$is_invoke_flag";then
      #在断网时间内请求一下网络
      if ping -c 5 www.baidu.com >/dev/null 2>&1; then
        logger -t SGU-Script "断网时刻网络连接正常，开启夜间断网重连"
        night_net_reconnection=true
      else
        logger -t SGU-Script "断网时刻网络连接失败，关闭夜间断网重连"
        night_net_reconnection=false
      fi
    fi
    #设置为已执行
    is_invoke_flag=true
  else
    #设置为未执行
    is_invoke_flag=false
  fi
}

#清理日志
clean_log() {
  time_now=$(date '+%H%M') #获取当前时间，格式是时分，例如当前是上午8：50，time_now=850
  #凌晨清理日志
  if [ "$time_now" -eq 0000 ]; then
    out_time=$(date '+%Y-%m-%d-%H:%M') #格式：2019-04-24-21:26
    echo "$out_time:为了防止日志过长，定时清理日志信息" >$LOG_FILE
  fi
}

#UA2F保活进程
keep_alive() {
  isInstall=$(opkg list_installed | grep "\ua2f")
  if [ -n "$isInstall" ]; then
    isAlive=$(pgrep ua2f)
    if [ -z "$isAlive" ]; then
      /etc/init.d/ua2f start
      logger -t SGU-Script "UA2F失活，即将启动UA2F"
    fi
  fi
}

#输出错误日志
log() {
  echo "$1:$2" >>$LOG_FILE #输出错误日志
  logger -t SGU-Script "$2"
}

#打印日志计时器
time_count=0                             #初始化日志计时器
logger_interval=$((logger_interval / 2)) #由于网络验证时ping了两次，差不多又多消耗一秒，于是次数减半
logger_counter() {
  if [ "$time_count" -ge "$logger_interval" ]; then
    time_count=0 #计时器结束重置日志计时器
    return 0     #输出日志
  else
    time_count=$((time_count + 1)) #计时器
    return 1                       #不输出日志
  fi
}

#脚本主函数
main_script() {
  while true; do
    #ua2f保活
    keep_alive
    #清理日志
    clean_log
    #自动判断夜间是否有网络
    is_night_have_net
    #判断是否夜间，夜间断网不执行脚本
    WEEK_DAY=$(date +%w)
    time_now=$(date '+%H%M')
    #获取星期，星期六日不断网
    if [ "$WEEK_DAY" -eq 6 ] || [ "$WEEK_DAY" -eq 0 ]; then
      login "$time_now" logger_counter
    else
      #工作日网络供应时间
      if [ "$time_now" -ge "$net_start" ] && [ "$time_now" -le "$net_end" ]; then
        login "$time_now" logger_counter
      else
        #是否开启夜间网络重连
        if $night_net_reconnection;then
          login "$time_now" logger_counter
        fi
      fi
    fi
    sleep 1 #休息1s
  done
}

#同步时间
syn_time() {
  #循环直到联网成功
  while login "未知时间" true; do
    sleep 1 #休息1s
  done
  #同步时间
  ntpd -n -q -p ntp.aliyun.com
  logger -t SGU-Script "同步时间成功"
}

#后台运行主函数
reload() {
  #同步时间
  syn_time
  #开机等待ua2f运行
  sleep 60
  #脚本主函数
  main_script
}

start() {
  if [ -n "$(pgrep -f "sgu_script reload")" ]; then
    echo "SGU-script has already started"
  else
    /etc/init.d/sgu_script reload &
    echo "SGU-script has started"
  fi
}

stop() {
  kill -9 "$(pgrep -f "sgu_script reload")" >/dev/null 2>&1
  echo "SGU-script has stopped"
}
