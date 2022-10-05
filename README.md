## 目录

*   [说明](#说明)

*   [维护设备列表](#维护设备列表)

*   [编译教程](#编译教程)

    *   [注意](#注意)

    *   [编译命令](#编译命令)

    *   [二次编译](#二次编译)

    *   [macOS 原生系统进行编译](#macos-原生系统进行编译)

*   [警告](#警告)

# 固件分支

# 说明

*   此分支用于存储编译的UA2F固件

*   感谢`coolsnowwolf大佬`提供的openwrt源码以及`Zxilly大佬`的UA2F源码

*   编译教程参考了`coolsnowwolf大佬`与`Zxilly大佬`的教程

*   `lede仓库` <https://github.com/coolsnowwolf/lede>

*   `UA2F仓库` <https://github.com/Zxilly/UA2F>

🔔 **除了自用设备，其他设备由于没有机器所以没有进行测试，有问题请提issue**

***

# 维护设备列表

| 机型 | 标签 | 自用 | 是否测试 |
| :----: | :----: | :----: | :----: |
| 新路由3 | [d-team_newifi-d2](./ramips-mt7621-d-team_newifi-d2) | ✅ | ✅ |
| 斐讯K2 | [phicomm_psg1218](./ramips-mt7620-phicomm_psg1218) | ❎ | ✅ |
| 斐讯K2P | [phicomm_k2p](./ramips-mt7621-phicomm_k2p) | ❎ | ❎ |
| 极壹S | [hiwifi_hc5661](./ramips-mt7620-hiwifi_hc5661) | ❎ | ✅ |
| 极路由2 | [hiwifi_hc5761](./ramips-mt7620-hiwifi_hc5761) | ❎ | ✅ |
| 小米路由器mini | [xiaomi_miwifi-mini](./ramips-mt7620-xiaomi_miwifi-mini) | ❎ | ❎ |

***

# 编译教程

## 注意

1.  **不要用 root 用户进行编译**

2.  国内用户编译前最好准备好`ladder`

3.  请保证全局`魔法`环境，编译失败的大多数原因是网络问题

***

## 编译命令

1.  首先装好 Linux 系统，推荐Deepin 、Debian 11 或 Ubuntu LTS

2.  安装编译依赖

    ```bash
    # 系统更新
    sudo apt-get -y update
    sudo apt-get -y upgrade
    ```

    ```bash
    # 安装依赖关系
    sudo apt-get -y install build-essential asciidoc binutils bzip2 gawk gettext git libncurses5-dev libz-dev patch python3 python2.7 unzip zlib1g-dev lib32gcc1 libc6-dev-i386 subversion flex uglifyjs git-core gcc-multilib p7zip p7zip-full msmtp libssl-dev texinfo libglib2.0-dev xmlto qemu-utils upx libelf-dev autoconf automake libtool autopoint device-tree-compiler g++-multilib antlr3 gperf wget curl swig rsync
    ```

3.  下载源代码，更新 feeds 并选择配置

    ```bash
    # 下载源码 (下面三者选其一)
    git clone https://github.com/openwrt/openwrt.git
    git clone https://github.com/coolsnowwolf/lede
    git clone https://github.com/immortalwrt/immortalwrt.git
    cd openwrt/lede/immortalwrt
    ./scripts/feeds update -a
    ./scripts/feeds install -a

    ```

4.  加入UA2F模块

    ```bash
    git clone https://github.com/Zxilly/UA2F.git package/UA2F
    make menuconfig

    # 在配置面板中按/搜索，即可找到
    # 一般在此位置
    network
        --> Routing and Redirection
            --> UA2F <*>
    # 说明:
    # <M>:单独编译
    # <*>:编译进固件

    # 然后保存退出

    ```

5.  加入LuCI

    ```bash
    # 找到以下模块并选上<*>

    LuCI
        --> 1. Collections
            --> luci
        --> 2. Modules
            --> Translations
                --> Chinese Simplified (zh_Hans)
        --> 3. Modules
            --> luci-compat
            
    # 然后保存退出
    ```

6.  加入防 TTL 检测依赖

    ```bash
    make menuconfig

    # 按下/可搜索，搜索iptables-mod-ipopt, kmod-ipt-ipopt
    # 一般在此位置
    1.Network
        --> Firewall
            --> iptables-mod-ipopt <*>
    2.Kernel modules
        --> Netfilter Extensions
            --> kmod-ipt-ipopt <*>
            
    # 然后保存退出
    ```

7.  其它依赖

    ```bash
    Network 
        --> 1.ipset <*>
        --> 2.Firewall
            --> iptables-mod-conntrack-extra <*>
            
    # 然后保存退出
    ```

8.  修改内核配置文件

    ```bash
    # 在openwrt/lede中找到[.config]文件(隐藏文件)底部加上

    CONFIG_NETFILTER_NETLINK_GLUE_CT=y

    ```

9.  下载 dl 库

    ```bash
    make download -j8
    ```

10. 编译固件 （-j 后面是线程数，第一次编译推荐用单线程）

    ```bash
    # 非 WSL 使用此命令编译
    make V=s -j1
    # 如果你使用 WSL/WSL2 进行编译，请使用以下命令
    PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin make -j$(($(nproc) + 1)) || PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin make -j1 V=s
    ```

11. 编译完成后输出路径：bin/targets，一般使用包含squashfs-sysupgrade的固件，如果编译完只有内核固件，说明你加的模块太多了，体积超过了系统固件的最大体积

***

## 二次编译

```bash
cd lede
git pull
./scripts/feeds update -a
./scripts/feeds install -a
make defconfig
make download -j8
make V=s -j$(nproc)
```

如果需要重新配置：

```bash
rm -rf ./tmp && rm -rf .config
make menuconfig
make V=s -j$(nproc)
```

***

## macOS 原生系统进行编译

1.  在 AppStore 中安装 Xcode

2.  安装 Homebrew：

    ```bash
    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    ```

3.  使用 Homebrew 安装工具链、依赖与基础软件包:

    ```bash
    brew unlink awk
    brew install coreutils diffutils findutils gawk gnu-getopt gnu-tar grep make ncurses pkg-config wget quilt xz
    brew install gcc@11
    ```

4.  然后输入以下命令，添加到系统环境变量中：

    ```bash
    echo 'export PATH="/usr/local/opt/coreutils/libexec/gnubin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/findutils/libexec/gnubin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/gnu-getopt/bin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/gnu-tar/libexec/gnubin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/grep/libexec/gnubin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/gnu-sed/libexec/gnubin:$PATH"' >> ~/.bashrc
    echo 'export PATH="/usr/local/opt/make/libexec/gnubin:$PATH"' >> ~/.bashrc
    ```

5.  重新加载一下 shell 启动文件 `source ~/.bashrc`，然后输入 `bash` 进入 bash shell，就可以和 Linux 一样正常编译了

***

# 警告

*   编译的固件绝对不能包括任何后门或可以监控或者劫持你的 HTTPS 的闭源软件， SSL 安全是互联网最后的壁垒。安全干净才是固件应该做到的

*   编译的固件请开一个新文件夹存放，命名规则取编译完固件名称中`openwrt-`与`-squashfs-sysupgrade.bin`之间的内容

*   编译的固件请备注好默认密码，以及默认路由器管理地址，写在文件夹单独的`info.txt`中
