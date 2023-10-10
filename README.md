# 该仓库于2023年10月10日正式存档，并不再更新，这也意味着我的人生阶段即将开启新的篇章，感谢各位这段时间的陪伴，再会~

## 目录

*   [SGU-Script](#sgu-script)

*   [关于](#关于)

    *   [运行环境](#运行环境)

    *   [功能](#功能)

*   [严肃警告](#严肃警告)

*   [安装教程](#安装教程)

    *   [一、刷入带有UA2F的固件](#一刷入带有ua2f的固件)

        *   [Ⅰ.刷入 Breed Web 恢复控制台](#ⅰ刷入-breed-web-恢复控制台)

        *   [Ⅱ.进入 Breed Web 恢复控制台](#ⅱ进入-breed-web-恢复控制台)

        *   [Ⅲ.刷入固件](#ⅲ刷入固件)

    *   [二、路由器设置](#二路由器设置)

        *   [Ⅰ.配置wan口](#ⅰ配置wan口)

        *   [Ⅱ.配置lan口](#ⅱ配置lan口)

        *   [Ⅲ.配置WIFI](#ⅲ配置wifi)

        *   [Ⅳ.配置管理界面（SSH）密码](#ⅳ配置管理界面ssh密码)

    *   [三、安装SGU-Script](#三安装sgu-script)

        *   [Ⅰ.cli（命令行版本）](#ⅰcli命令行版本)

        *   [Ⅱ.gui（可视化界面版本）](#ⅱgui可视化界面版本)

    *   [四、苦尽甘来](#四苦尽甘来)

        *   [Ⅰ.插上网线](#ⅰ插上网线)

        *   [Ⅱ.开始享受](#ⅱ开始享受)

*   [SGU-Script配置命令](#sgu-script配置命令)

    *   [OpenWrt](#openwrt)

*   [添砖加瓦](#添砖加瓦)

*   [故障排除](#故障排除)

*   [用户反馈](#用户反馈)

# SGU-Script

# 关于

## 运行环境

*   cli版需要jdk17及以上

*   gui版需要x86 64-bit windows7及以上系统

***

## 功能

1.  SGU-Script是一款免费开源的路由器锐捷认证设置程序，本质是自动配置登录脚本

2.  主要为了应对2022年韶关学院电信锐捷上网认证，目前只支持搭载OpenWrt的路由器配置

3. 并且OpenWrt需已安装curl或wget模块，未安装的设备请先自行安装该模块

4.  你也可以fork本项目，并添加对其他系统的支持

5.  SGU-Script会实时监控你的网络状况，在掉线的时候进行自动重连，但并不能阻止共享上网的检查

6.  由于阻止共享上网的监测较为复杂，此处提供一个笔记以解决共享上网的解决方案[（首推UA2F）](https://www.notion.so/OpenWrt-f59ae1a76741486092c27bc24dbadc59)

7.  本教程我也会致力于为大家解决校园网检测问题，本项目将使用UA2F的方案

8.  我会在firmware分支中提前准备我适配好带UA2F的固件，但我不可能做到所有设备都适配，希望大家能够编译更多的固件放在firmware分支，然后给我提一个Pr

9.  下文安装教程也会教你如何安装固件

10. 若你不需要绕过校园网检测的话，直接查看[安装教程]-[路由器设置]&&[安装SGU-Script]即可

***

# 严肃警告

*   SGU-Script只为学习开发，若你对该程序有异议，可至github项目提起issue

*   原则上该项目不允许被投入商业用途，但如果有人使用该项目售卖路由器的话，产生一切后果与本项目无关

*   请大家有能力的可以好好钻研一下本项目，教程写的已经蛮详细了，实在有非科班（纯小白）的，建议还是找人买一台装好的吧

*   若你使用本程序则默认遵循MIT license，祝你使用愉快

***

# 安装教程

## 一、刷入带有UA2F的固件

> 如果你有UA2F的ipk包可用，可以尝试安装ipk包，但是ipk包需要编译UA2F的sdk内核版本与固件内核版本严格一致，否则会安装失败

### Ⅰ.刷入 Breed Web 恢复控制台

1.  由于不同机型刷入步骤不同，所以请自行百度

2.  若嫌麻烦，在买路由器时可以拜托店家刷入（可能需要加钱）

3.  已刷入 Breed Web 恢复控制台的请继续下一步

### Ⅱ.进入 Breed Web 恢复控制台

1.  此处以新三为例，其他路由器请自行百度 

2.  将网线的一端插在lan口上，另一端插电脑

3.  在`电源线`的旁边有一个小小的按钮，记住它（下文中我们称之为`RESET键`）

4.  拔掉`电源线`，按下`RESET键`（不松开）

5.  插上`电源线`（`RESET键`依旧不松开）

6.  等待8-9秒，松开`RESET键`

### Ⅲ.刷入固件

1.  在`firmware分支`中有我提前编译好带UA2F的固件，跳转链接：[firmware](../../tree/firmware/)，如需下载固件请点击右上角[克隆/下载]

2.  阅读`firmware分支`的`README.md`说明来选择适配你路由器的固件，若你使用的是该分支中的固件，请你先仔细阅读固件所在文件夹下的`info.txt`，不同固件以下步骤可能会有所不同

3.  电脑打开浏览器，进入[192.168.1.1](http://192.168.1.1/ "192.168.1.1")，打不开的话可以拔插网线试试

4.  不出所料的话你会进入以下界面

    ![输入图片说明](https://foruda.gitee.com/images/1666349059109396222/f0a2a7d6_9532490.png "1-min.png")

5.  之后我们选择[恢复出厂设置]-[Config 区 (公版)]&[Config 区 (精简)]-[执行]

    ![输入图片说明](https://foruda.gitee.com/images/1666681634051227397/f822938c_9532490.png "2-min.png")

6.  执行成功后，如图依次点击，上传带UA2F的固件，并更新

    ![输入图片说明](https://foruda.gitee.com/images/1666349075080075741/ab1aa4c9_9532490.png "3-min.png")

7.  显示此界面时固件就安装成功了，等待路由器开机（2-3分钟）

    ![输入图片说明](https://foruda.gitee.com/images/1666349081795589845/d776e1df_9532490.png "4-min.png")

## 二、路由器设置

> 请注意以下每一步操作后都请记得点击右下角的保存并应用，下文将不会再提醒你

### Ⅰ.配置wan口

1.  路由器开机后，依旧在浏览器输入[192.168.1.1](http://192.168.1.1/ "192.168.1.1")，默认密码为`admin`或`password`

    ![输入图片说明](https://foruda.gitee.com/images/1666349090010335867/05394ed0_9532490.jpeg "5-min.jpeg")

2.  进入[网络]-[接口]-[删除掉wan6]

    ![输入图片说明](https://foruda.gitee.com/images/1666349107670237165/79076ecd_9532490.png "6-min.png")

### Ⅱ.配置lan口

1.  可选，若路由器出现网页加载慢，但app可以正常访问网络的情况就配置

2.  进入[网络]-[接口]-[LAN]，也可以直接点击lan口右边的[修改]按钮

    ![输入图片说明](https://foruda.gitee.com/images/1666349132895086613/83e884bc_9532490.png "9-min.png")

3.  滑到最下面的[高级设置]-[DHCP选项]，设置`6,223.5.5.5,119.29.29.29`，注意逗号和点，此处是向lan口设备通告DNS

    ![输入图片说明](https://foruda.gitee.com/images/1666682949024924331/37213444_9532490.png "10-min.png")

### Ⅲ.配置WIFI

1.  进入[网络]-[无线]页面（以下教程使用新三演示，不同机型按钮位置可能不太一样，但大同小异）

    ![输入图片说明](https://foruda.gitee.com/images/1666349153163096727/ac77f67f_9532490.png "11-min.png")

2.  新三有[2.4 GHz]和[5 GHz]两个频段，这里通俗的讲解下

    - 2.4 GHz：信号好，穿墙能力强，但是负载网速慢

    - 5 GHz：信号良，穿墙能力差，但是负载网速快

3.  配置`2.4 GHz` WIFI

    - 进入2.4 GHz`WIFI`配置界面（菜单栏进入或通过修改按钮进入没有差别，不同机型按钮位置不同，需自行辨别）

      ![输入图片说明](https://foruda.gitee.com/images/1666349163081947188/d825d587_9532490.png "12-min.png")

    - 滑到最下面，给你的`WIFI`设置一个骚名字

      ![输入图片说明](https://foruda.gitee.com/images/1666349172550857628/30a501dc_9532490.png "13-min.png")

    - 给你的`WIFI`设置一个安全密码，加密方式推荐使用`WPA-PSK/WPA2-PSK Mixed Mode`，更好地兼容旧设备

      ![输入图片说明](https://foruda.gitee.com/images/1666349180462751559/ecf48b8d_9532490.png "14-min.png")

4.  配置`5 GHz` WIFI（可选，有些机型不支持5 GHz）

    - 进入5 GHz`WIFI`配置界面

      ![输入图片说明](https://foruda.gitee.com/images/1666349188317421827/cdebc439_9532490.png "15-min.png")

    - 剩余步骤与`2.4 GHz`形同，

    - 如果你希望自己选择使用`2.4 GHz`或`5 GHz`的话，你可以将`5 GHz`的WIFI名设置与`2 GHz`不同，这样你可以通过不同WiFi名来切换使用`2.4 GHz`或`5 GHz`

    - 如果你希望手机自己选择的话，可以将两个WIFI名设置一样的（我也建议这样做）

    - 但是不管你选择哪样，我都建议你将`5 GHz`的`WIFI`加密方式设置为更安全的`WPA2-PSK/WPA3-SAE Mixed Mode`，以此来提高安全性，但一些早期路由器的硬件不兼容`WPA3`，请慎重选择

    - 由于一些旧设备只能连接`2.4 GHz`，并且不支持最新的`WPA3`加密认证，于是我建议`2.4 GHz`的频段使用`WPA-PSK/WPA2-PSK Mixed Mode`加密

### Ⅳ.配置管理界面（SSH）密码

1.  至此路由器设置全部设置完毕

2.  但是原版OpenWrt可能需要你更改SSH隧道的密码，才可以使用SSH，因此你需要在[系统]-[管理权]中修改，同时你的路由器管理界面密码也会被对应修改

    ![输入图片说明](https://foruda.gitee.com/images/1666349196444397786/c586db7e_9532490.png "16-min.png")

## 三、安装SGU-Script

> cli版和gui版二选一，跳转至下载地址：[SGU-Script](../../releases)

### Ⅰ.cli（命令行版本）

1.  可跨平台，安装`jdk17`，使用`java -jar SGU-Script.jar`命令来启动程序

2.  跟着提示完成操作，后续脚本更新，只需重复以上操作，覆盖安装即可

    ![输入图片说明](https://foruda.gitee.com/images/1666349204792433990/be5fbfc0_9532490.png "17-min.png")

### Ⅱ.gui（可视化界面版本）

1.  双击`SGU-Script.exe`文件运行，跟着提示完成操作即可

2.  后续脚本更新，只需重复以上操作，覆盖安装即可

3.  提示：由于打包使用的`openjfx`和`jdk`使用了x86 64-bit win，所以请使用x86 64-bit windows7及以上系统来使用gui版

    ![输入图片说明](https://foruda.gitee.com/images/1666349213094625473/7151cd0e_9532490.png "18-min.png")

## 四、苦尽甘来

### Ⅰ.插上网线

1.  全部设置完成后，请将路由器wan口（可通过接口下方的文字或通过颜色辨别）与宿舍墙上的网口接通

2.  有些路由器固件的接口是乱的，可以在[路由器web管理界面]-[网络]-[交换机中]中查看wan口连接情况及真实位置，或将接口调整到正确位置

### Ⅱ.开始享受

1.  维护网络安全，保护个人隐私；树文明新风，建和谐校园

2.  妥善运用网络及遵守法律法规，请记住`校园网里的你并不是透明的`

***

# SGU-Script配置命令

你可以使用命令来控制`SGU-Script`

## OpenWrt

1.  **启动脚本**  `sgu_script start`

2.  **停止脚本**  `sgu_script stop`

3.  **重启脚本**  `sgu_script restart`

4.  **添加开机自启动**  `sgu_script enable`

5.  **取消开机自启动**  `sgu_script disable`

***

# 添砖加瓦

欢迎pr，欢迎fork

SGU-Script目前只支持Openwrt固件，但目前Openwrt也已足够使用

由于程序主函数部分采用反射机制完成，可以轻松地拓展更多的系统支持，具体操作如下

1.  在`cn.rabig.cli.controller.BaseSystem.systemList`中添加系统名称`{systemName}`

2.  并在`cn.rabig.cli.controller`中创建对应的系统类(`{systemName}.java`)并实现`BaseSystem`接口，依次实现接口中的方法

3.  在`src/main/resources/script/{systemName}`中创建对应的shell脚本（变量使用{}占位，FileUtils中的`writeSGUScript`方法可以为占位符赋值）

4.  CLI以及GUI主函数部分不需要改动，对应的下拉框新选项以及安装提示会自动生成，方法将会通过反射调用，只需要实现`BaseSystem`接口中的方法即可

***

# 故障排除

1.  SGU-Script安装完毕后，理论上脚本就会自动去尝试登录账号（如果还是没有网络请查看日志）

2.  你可以在[路由器管理界面]-[状态]-[系统日志]查看全部日志，也可以在/var/log/sgu_script.log文件中查看错误日志，或直接使用软件查看错误日志

3.  若提醒你账号已在线，可能是账号在脚本登录之前就已经登录过了，或者被别人登录了，你可以在自助中心进行查看在线记录

    [校园网自助服务](http://172.16.253.121/)

4.  自助中心的强制下线功能是无效的，你可以使用以下链接登出账号，但该链接只能在已经登录你账号的网络下才有效

    [断线链接](http://2.2.2.2/)

5.  若被检测到共享，请验证`UA2F`防共享是否运行正常，打开下面的链接，若真实UA是一堆`F`，则说明`UA2F`运行正常，第一次使用本脚本时，可能会被检测共享，但之后次数会减少，因为修改的UA也会被视为新设备

    [UA检测](http://ua.233996.xyz/)

6.  据作者所说，`UA2F`可能会和`mwan3`与`Flow Offloading`冲突，若你的`UA2F`存在频繁掉的现象请尝试关闭`mwan3`与`Flow Offloading`，分别在[网络]-[负载均衡]与[网络]-[Turbo ACC 网络加速]-[软件流量分载]/[硬件流量分载]

7.  腾讯系软件/游戏（LOL、CF、和平精英、王者荣耀等）中的网络加速服务会绕过UA2F，在一大段时间内只能登录一个号，否则将导致网络被封禁，可尝试关闭软件中的网络加速服务，或者直接使用流量游玩游戏，切记桥接网络也会导致网络被封禁

8.  目前有些网站在UA被篡改的情况下可能会访问异常，例如宝塔面板；因此如果你既使用了UA2F又想要正常浏览此类网站，可以安装浏览器拓展`User-Agent Switcher and Manager`来修改`浏览器UA`为`UA2F篡改后的UA`；操作步骤：右键拓展切换到`"white-list" mod`模式；在拓展选项中的`White-List Mode`列表里添加需要修改的网站，如宝塔；单击拓展，将`userAgent`修改为[UA检测网站](http://ua.233996.xyz/)中获取到的`真实UA`并`Apply(all windows)`

9.  某些多频段路由器固件的无线模块可能存在问题，表现为开机一段时间就崩溃关机，可以尝试将两个频段(2.4 GHz, 5 GHz)的无线名称、密码、加密方式调整为相同的来解决这个问题

***

# 用户反馈

有任何问题欢迎在[Issues](../../issues)中提出，或者通过邮箱[rabig@foxmail.com](mailto:rabig@foxmail.com "rabig@foxmail.com")联系我

这里贴一个[韶关学院路由器交流群](https://jq.qq.com/?_wv=1027&k=SbuWAkeP)，大家也可以在群里讨论
