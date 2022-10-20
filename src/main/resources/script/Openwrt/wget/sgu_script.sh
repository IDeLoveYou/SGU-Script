#!/bin/sh /etc/rc.common
# shellcheck disable=SC2034
START=99 #这里是启动优先级
STOP=15  #这里是停止优先级
LOG_FILE=/var/log/sgu_script.log
logger_interval=600 #这里是"网络连接正常!"日志的间隔时间(每10分钟输出一次日志)

#登录认证
login() {
  if ping -c 2 www.baidu.com >/dev/null 2>&1; then
    if "$2"; then
      logger -t SGU-Script "网络连接正常!"
    fi
    return 1
  else
    time_count="$logger_interval" #网络出错立即输出日志
    response=$(wget --post-data="userId={}&password={}&queryString=wlanuserip={}&wlanacname=NAS&ssid=Ruijie&nasip=172.16.253.90&mac=000000000000&t=wireless-v2-plain&url=http://1.1.1.1/" http://172.16.253.93:8080/eportal/InterFace.do?method=login -qO-)
    if [ -n "$response" ]; then
      tmp=${response#*message\":\"}
      result=${tmp%\",\"forwordurl*} #formate response
      #不为空有错误信息
      if [ -n "$result" ]; then
        #是否是夜间禁止上网信息，或者账号未开通
        isILogin=$(echo "$result" | grep "在线")
        isNight=$(echo "$result" | grep "运营商用户认证失败")
        isProxies=$(echo "$result" | grep "架设代理")
        if [ -n "$isNight" ]; then
          log "$1" "$result(可能原因有：夜间禁止上网信息，或者账号未开通)"
          sleep 60 #休息1分钟
        elif [ "$result" = "用户不存在,请输入正确的用户名!" ] || [ "$result" = "密码不匹配,请输入正确的密码!" ]; then
          log "$1" "$result"
          exit 1
        elif [ -n "$isILogin" ]; then
          log "$1" "$result(账号已经在线，请检查网络，若无网络，账号可能异地登录，请于自助中心下线设备并重启设备)"
          sleep 60 #休息1分钟
        elif [ -n "$isProxies" ]; then
          log "$1" "$result(可能原因有：使用代理软件，或者腾讯系软件/游戏的网络代理加速服务，请见【README】-【验证网络】-【第6条】)"
          sleep 300 #休息5分钟
        else
          log "$1" "$result"
          sleep 60 #休息1分钟
        fi
      else
        logger -t SGU-Script "login Success!"
      fi
    else
      log "$1" "请检查网线是否插紧,路由器设置是否正确"
    fi
  fi
}

#清理日志
clean_log() {
  time_now=$(date '+%H%M') #获取当前时间，格式是时分，例如当前是上午8：50，time_now=850
  #凌晨清理日志
  if [ "$time_now" -eq 0000 ]; then
    out_time=$(date '+%Y-%m-%d-%H:%M') #格式：2019-04-24-21:26
    echo "$out_time:清理日志信息" >$LOG_FILE
  fi
}

#UA2F保活进程
keep_alive() {
  isInstall=$(opkg list_installed | grep "\ua2f")
  if [ -n "$isInstall" ]; then
    isAlive=$(pgrep ua2f)
    if [ -z "$isAlive" ]; then
      /etc/init.d/ua2f start
      logger -t SGU-Script "UA2F失活，启动UA2F"
    fi
  fi
}

#输出错误日志
log() {
  echo "$1:$2" >>$LOG_FILE #输出错误日志
  logger -t SGU-Script "$2"
}

#打印日志计时器
time_count=0                   #初始化日志计时器
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

#同步时间后调用的登录方法
time_right_login() {
  while true; do
    keep_alive
    clean_log
    WEEK_DAY=$(date +%w)
    time_now=$(date '+%H%M')
    #获取星期，星期六日不断网
    if [ "$WEEK_DAY" -eq 6 ] || [ "$WEEK_DAY" -eq 0 ]; then
      login "$time_now" logger_counter
    else
      #早上5.30--24.00 执行脚本
      if [ "$time_now" -ge 530 ] && [ "$time_now" -le 2400 ]; then
        login "$time_now" logger_counter
      fi
    fi
    sleep 1 #休息1s
  done
}

#未知时间时调用
time_unknown_login() {
  #循环直到联网成功
  while login "未知时间" true; do
    sleep 1 #休息1s
  done
  #同步时间
  ntpd -n -q -p ntp.aliyun.com
  logger -t SGU-Script "同步时间成功"
}

#登录函数
reload() {
  #同步时间
  time_unknown_login
  #开机等待ua2f运行
  sleep 60
  #脚本主函数
  time_right_login
}

start() {
  /etc/init.d/sgu_script reload &
  echo "SGU-script has started."
}

stop() {
  kill -9 "$(pgrep -f "sgu_script reload")" >/dev/null 2>&1
  echo "SGU-script has stopped."
}
