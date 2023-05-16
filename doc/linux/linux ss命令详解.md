ss 是 Socket Statistics 的缩写。ss 命令可以用来获取 socket 统计信息，它显示的内容和 netstat 类似。但 ss 的优势在于它能够显示更多更详细的有关 TCP 和连接状态的信息，而且比 netstat 更快。当服务器的 socket 连接数量变得非常大时，无论是使用 netstat 命令还是直接 cat /proc/net/tcp，执行速度都会很慢。ss 命令利用到了 TCP 协议栈中 tcp_diag。tcp_diag 是一个用于分析统计的模块，可以获得 Linux 内核中第一手的信息，因此 ss 命令的性能会好很多。

### 常用选项

* -h, –help 帮助
* -V, –version 显示版本号
* -t, –tcp 显示 TCP 协议的 sockets
* -u, –udp 显示 UDP 协议的 sockets
* -x, –unix 显示 unix domain sockets，与 -f 选项相同
* -n, –numeric 不解析服务的名称，如 “22” 端口不会显示成 “ssh”
* -l, –listening 只显示处于监听状态的端口
* -p, –processes 显示监听端口的进程(Ubuntu 上需要 sudo)
* -a, –all 对 TCP 协议来说，既包含监听的端口，也包含建立的连接
* -r, –resolve 把 IP 解释为域名，把端口号解释为协议名称
  
### 常见用例
如果不添加选项 ss 命令默认输出所有建立的连接(不包含监听的端口)，包括 tcp, udp, and unix socket 三种类型的连接：

```shell
$ sudo ss |head -n 5
Netid State      Recv-Q Send-Q                            Local Address:Port                      Peer Address:Port                     Process
u_str ESTAB      0      0                                             * 21108                                * 21300
u_str ESTAB      0      0                   /run/dbus/system_bus_socket 23222                                * 23041
u_str ESTAB      0      0                                             * 23217                                * 23216
u_str ESTAB      0      0                                             * 23216                                * 23217

```
1. 查看主机监听的端口
```shell
$ ss -tnl
State                Recv-Q               Send-Q                             Local Address:Port                              Peer Address:Port               Process
LISTEN               0                    64                                       0.0.0.0:42885                                  0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:111                                    0.0.0.0:*
LISTEN               0                    128                                127.0.0.53%lo:53                                     0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:22                                     0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:42359                                  0.0.0.0:*
LISTEN               0                    128                                    127.0.0.1:6010                                   0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:37341                                  0.0.0.0:*
LISTEN               0                    10                                       0.0.0.0:65533                                  0.0.0.0:*
LISTEN               0                    128                                    127.0.0.1:46369                                  0.0.0.0:*
LISTEN               0                    64                                       0.0.0.0:2049                                   0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:43361                                  0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:8899                                   0.0.0.0:*
LISTEN               0                    128                                         [::]:36809                                     [::]:*
LISTEN               0                    64                                          [::]:46223                                     [::]:*
LISTEN               0                    128                                         [::]:111                                       [::]:*
LISTEN               0                    128                                         [::]:44595                                     [::]:*
LISTEN               0                    128                                         [::]:22                                        [::]:*
LISTEN               0                    128                                        [::1]:6010                                      [::]:*
LISTEN               0                    128                                         [::]:55935                                     [::]:*
LISTEN               0                    64                                          [::]:2049                                      [::]:*
LISTEN               0                    128                                        [::1]:8899                                      [::]:*

```
2. 通过 -r 选项解析 IP 和端口号
```shell
$ ss -tlr
State               Recv-Q              Send-Q                           Local Address:Port                                     Peer Address:Port             Process
LISTEN              0                   64                                     0.0.0.0:rpc.nlockmgr                                  0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:rpc.portmapper                                0.0.0.0:*
LISTEN              0                   128                               localhost%lo:domain                                        0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:ssh                                           0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:rpc.mountd                                    0.0.0.0:*
LISTEN              0                   128                                  localhost:6010                                          0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:rpc.mountd                                    0.0.0.0:*
LISTEN              0                   10                                     0.0.0.0:65533                                         0.0.0.0:*
LISTEN              0                   128                                  localhost:46369                                         0.0.0.0:*
LISTEN              0                   64                                     0.0.0.0:rpc.nfs                                       0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:rpc.mountd                                    0.0.0.0:*
LISTEN              0                   128                                    0.0.0.0:8899                                          0.0.0.0:*
LISTEN              0                   128                                       [::]:36809                                            [::]:*
LISTEN              0                   64                                        [::]:46223                                            [::]:*
LISTEN              0                   128                                       [::]:rpc.portmapper                                   [::]:*
LISTEN              0                   128                                       [::]:44595                                            [::]:*
LISTEN              0                   128                                       [::]:ssh                                              [::]:*
LISTEN              0                   128                                  localhost:6010                                             [::]:*
LISTEN              0                   128                                       [::]:55935                                            [::]:*
LISTEN              0                   64                                        [::]:rpc.nfs                                          [::]:*
LISTEN              0                   128                                  localhost:8899                                             [::]:*

```
3. 使用 -p 选项查看监听端口的程序名称
```shell
$ sudo ss -tlp
State         Recv-Q         Send-Q                 Local Address:Port                   Peer Address:Port        Process
LISTEN        0              64                           0.0.0.0:42885                       0.0.0.0:*
LISTEN        0              128                          0.0.0.0:sunrpc                      0.0.0.0:*            users:(("rpcbind",pid=683,fd=4),("systemd",pid=1,fd=35))
LISTEN        0              128                    127.0.0.53%lo:domain                      0.0.0.0:*            users:(("systemd-resolve",pid=686,fd=13))
LISTEN        0              128                          0.0.0.0:ssh                         0.0.0.0:*            users:(("sshd",pid=1289,fd=3))
LISTEN        0              128                          0.0.0.0:42359                       0.0.0.0:*            users:(("rpc.mountd",pid=1240,fd=17))
LISTEN        0              128                        127.0.0.1:6010                        0.0.0.0:*            users:(("sshd",pid=659841,fd=11))
LISTEN        0              128                          0.0.0.0:37341                       0.0.0.0:*            users:(("rpc.mountd",pid=1240,fd=9))
LISTEN        0              10                           0.0.0.0:65533                       0.0.0.0:*            users:(("ovs-vswitchd",pid=1149,fd=30))
LISTEN        0              128                        127.0.0.1:46369                       0.0.0.0:*            users:(("containerd",pid=1250,fd=7))
LISTEN        0              64                           0.0.0.0:nfs                         0.0.0.0:*
LISTEN        0              128                          0.0.0.0:43361                       0.0.0.0:*            users:(("rpc.mountd",pid=1240,fd=13))
LISTEN        0              128                          0.0.0.0:8899                        0.0.0.0:*            users:(("privoxy",pid=1258,fd=4))
LISTEN        0              128                             [::]:36809                          [::]:*            users:(("rpc.mountd",pid=1240,fd=19))
LISTEN        0              64                              [::]:46223                          [::]:*
LISTEN        0              128                             [::]:sunrpc                         [::]:*            users:(("rpcbind",pid=683,fd=6),("systemd",pid=1,fd=37))
LISTEN        0              128                             [::]:44595                          [::]:*            users:(("rpc.mountd",pid=1240,fd=11))
LISTEN        0              128                             [::]:ssh                            [::]:*            users:(("sshd",pid=1289,fd=4))
LISTEN        0              128                            [::1]:6010                           [::]:*            users:(("sshd",pid=659841,fd=10))
LISTEN        0              128                             [::]:55935                          [::]:*            users:(("rpc.mountd",pid=1240,fd=15))
LISTEN        0              64                              [::]:nfs                            [::]:*
LISTEN        0              128                            [::1]:8899                           [::]:*            users:(("privoxy",pid=1258,fd=5))

```
最后一列就是运行的程序名称。还可以通过 grep 继续过滤：
```shell
$ sudo ss -tlp | grep ssh
LISTEN    0         128                0.0.0.0:ssh               0.0.0.0:*       users:(("sshd",pid=1289,fd=3))
LISTEN    0         128              127.0.0.1:6010              0.0.0.0:*       users:(("sshd",pid=659841,fd=11))
LISTEN    0         128                   [::]:ssh                  [::]:*       users:(("sshd",pid=1289,fd=4))
LISTEN    0         128                  [::1]:6010                 [::]:*       users:(("sshd",pid=659841,fd=10))

```
4. 查看建立的 TCP 连接
```qute
-a –all 对 TCP 协议来说，既包含监听的端口，也包含建立的连接
```
```shell
$ ss -tna
State                Recv-Q               Send-Q                             Local Address:Port                              Peer Address:Port               Process
LISTEN               0                    64                                       0.0.0.0:42885                                  0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:111                                    0.0.0.0:*
LISTEN               0                    128                                127.0.0.53%lo:53                                     0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:22                                     0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:42359                                  0.0.0.0:*
LISTEN               0                    128                                    127.0.0.1:6010                                   0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:37341                                  0.0.0.0:*
LISTEN               0                    10                                       0.0.0.0:65533                                  0.0.0.0:*
LISTEN               0                    128                                    127.0.0.1:46369                                  0.0.0.0:*
LISTEN               0                    64                                       0.0.0.0:2049                                   0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:43361                                  0.0.0.0:*
LISTEN               0                    128                                      0.0.0.0:8899                                   0.0.0.0:*
ESTAB                0                    0                                  10.190.49.224:22                                10.36.240.92:63428
ESTAB                0                    0                                  10.190.49.224:22                                10.36.240.92:63427
LISTEN               0                    128                                         [::]:36809                                     [::]:*
LISTEN               0                    64                                          [::]:46223                                     [::]:*
LISTEN               0                    128                                         [::]:111                                       [::]:*
LISTEN               0                    128                                         [::]:44595                                     [::]:*
LISTEN               0                    128                                         [::]:22                                        [::]:*
LISTEN               0                    128                                        [::1]:6010                                      [::]:*
LISTEN               0                    128                                         [::]:55935                                     [::]:*
LISTEN               0                    64                                          [::]:2049                                      [::]:*
LISTEN               0                    128                                        [::1]:8899                                      [::]:*

```
5. 显示更多的信息
```qute
-o, –options 显示时间信息
-m, –memory 显示 socket 使用的内存 
-i, –info 显示更多 TCP 内部的信息
```
```shell
$ sudo ss -imo state established '( sport = :ssh )'
Netid                  Recv-Q                  Send-Q                                   Local Address:Port                                     Peer Address:Port                   Process
tcp                    0                       0                                        10.190.49.224:ssh                                      10.36.240.92:63428                   timer:(keepalive,106min,0)
         skmem:(r0,rb131072,t0,tb87040,f0,w0,o0,bl0,d0) sack cubic wscale:8,7 rto:236 rtt:34.732/16.502 ato:64 mss:1460 pmtu:1500 rcvmss:536 advmss:1460 cwnd:12 bytes_acked:10777 bytes_received:1328 segs_out:26 segs_in:26 data_segs_out:21 data_segs_in:16 send 4.0Mbps lastsnd:789988 lastrcv:789988 lastack:789928 pacing_rate 8.1Mbps delivery_rate 3.9Mbps app_limited busy:640ms rcv_space:14600 rcv_ssthresh:64076 minrtt:15.93
tcp                    0                       64                                       10.190.49.224:ssh                                      10.36.240.92:63427                   timer:(on,248ms,0)
         skmem:(r0,rb131072,t0,tb87040,f1792,w2304,o0,bl0,d0) sack cubic wscale:8,7 rto:256 rtt:53.801/17.009 ato:72 mss:1460 pmtu:1500 rcvmss:1168 advmss:1460 cwnd:10 ssthresh:20 bytes_acked:46289 bytes_received:13892 segs_out:264 segs_in:362 data_segs_out:256 data_segs_in:179 send 2.2Mbps lastsnd:8 lastrcv:12 lastack:12 pacing_rate 2.6Mbps delivery_rate 14.1Mbps app_limited busy:10012ms unacked:1 rcv_space:14600 rcv_ssthresh:64076 minrtt:3.255

```
6. 显示概要信息
```shell
$ sudo ss -s
Total: 278
TCP:   25 (estab 2, closed 1, orphaned 0, timewait 1)

Transport Total     IP        IPv6
RAW       0         0         0
UDP       19        12        7
TCP       24        15        9
INET      43        27        16
FRAG      0         0         0
```
7. dst/src dport/sport 语法
```qute
可以通过 dst/src/dport/sprot 语法来过滤连接的来源和目标，来源端口和目标端口。
```
```shell
# 匹配远程地址和端口号
$ ss dst 192.168.1.5
$ ss dst 192.168.119.113:http
$ ss dst 192.168.119.113:443

#匹配本地地址和端口号
$ ss src 192.168.119.103
$ ss src 192.168.119.103:http
$ ss src 192.168.119.103:80

#将本地或者远程端口和一个数比较, 可以使用下面的语法做端口号的过滤：
$ ss dport OP PORT
$ ss sport OP PORT
```
```qute
OP 可以代表以下任意一个
```

| 符号  | 英文  | 描述          |
|-----|-----|-------------|
| <=  | le  | 	小于或等于某个端口号 |
| >=  | ge  | 大于或等于某个端口号  |
| ==  | eq  | 等于某个端口号     |
| !=  | ne  | 不等于某个端口号    |
| \>  | gt  | 大于某个端口号     |
| <   | lt  | 小于某个端口号     |

下面是一个简单的 demo **(注意，需要对尖括号使用转义符)**：
```shell
$ ss -tunl sport lt 50
Netid             State              Recv-Q             Send-Q                         Local Address:Port                         Peer Address:Port            Process
tcp               LISTEN             0                  128                                  0.0.0.0:22                                0.0.0.0:*
tcp               LISTEN             0                  128                                     [::]:22                                   [::]:*

$ ss -tunl sport \< 50
Netid             State              Recv-Q             Send-Q                         Local Address:Port                         Peer Address:Port            Process
tcp               LISTEN             0                  128                                  0.0.0.0:22                                0.0.0.0:*
tcp               LISTEN             0                  128                                     [::]:22                                   [::]:*

```
8. 通过 TCP 的状态进行过滤
ss 命令还可以通过 TCP 连接的状态进程过滤，支持的 TCP 协议中的状态有：
* established
* syn-sent
* syn-recv
* fin-wait-1
* fin-wait-2
* time-wait
* closed
* close-wait
* last-ack
* listening
* closing 

除了上面的 TCP 状态，还可以使用下面这些状态：

| 状态           | 描述                                        |
|--------------|-------------------------------------------|
| all          | 列出所有的 TCP 状态。                             |
| connected    | 列出除了 listening 和 closing 之外的所有 TCP 状态。    |
| synchronized | 列出除了 syn-sent 之外的所有 TCP 状态。               |
| bucket       | 列出 maintained 的状态，如：time-wait 和 syn-recv。 |
| big          | 列出和 bucket 相反的状态。                         |
```shell
# 使用 ipv4 时的过滤语法如下：
$ ss -4 state filter

# 使用 ipv6 时的过滤语法如下：
$ ss -6 state filter

$ ss -4 state listening
Netid               Recv-Q               Send-Q                             Local Address:Port                               Peer Address:Port               Process
tcp                 0                    64                                       0.0.0.0:42885                                   0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:sunrpc                                  0.0.0.0:*
tcp                 0                    128                                127.0.0.53%lo:domain                                  0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:ssh                                     0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:42359                                   0.0.0.0:*
tcp                 0                    128                                    127.0.0.1:6010                                    0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:37341                                   0.0.0.0:*
tcp                 0                    10                                       0.0.0.0:65533                                   0.0.0.0:*
tcp                 0                    128                                    127.0.0.1:46369                                   0.0.0.0:*
tcp                 0                    64                                       0.0.0.0:nfs                                     0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:43361                                   0.0.0.0:*
tcp                 0                    128                                      0.0.0.0:8899                                    0.0.0.0:*


```
9. 过滤 TCP 的状态和端口号
**(注意下面命令中的转义符和空格，都是必须的。如果不用转义符，可以使用单引号) **
显示所有状态为 established 的 ssh 连接：
```shell
$ sudo ss -4n state established
Netid               Recv-Q               Send-Q                             Local Address:Port                              Peer Address:Port                Process
udp                 0                    0                                  10.190.49.224:39790                              10.7.100.99:53
udp                 0                    0                                      127.0.0.1:59780                               127.0.0.53:53
tcp                 0                    0                                  10.190.49.224:22                                10.36.240.92:63428
tcp                 0                    0                                  10.190.49.224:60120                             10.115.50.29:8081
tcp                 0                    64                                 10.190.49.224:22                                10.36.240.92:63427

# 下面的两种写法是等价的，要有使用 \ 转义小括号，要么使用单引号括起来：
$ ss -4n state listening \( sport = :ssh \)
$ ss -4n state listening '( sport = :ssh )' 
```
列出所有连接到 22 端口的连接和对 22 端口的监听：
```shell
$ ss state all sport = :22
Netid             State              Recv-Q             Send-Q                         Local Address:Port                         Peer Address:Port             Process
tcp               LISTEN             0                  128                                  0.0.0.0:ssh                               0.0.0.0:*
tcp               ESTAB              0                  0                              10.190.49.234:ssh                          10.36.240.92:63154
tcp               ESTAB              0                  64                             10.190.49.234:ssh                          10.36.240.92:63153
tcp               LISTEN             0                  128                                     [::]:ssh                                  [::]:*

```
下面是一个来自 ss man page 的例子，它列举出处于 FIN-WAIT-1状态的源端口为 80 或者 443，目标网络为 193.233.7/24 所有 TCP 套接字：
```shell
$ ss state fin-wait-1 '( sport = :http or sport = :https )' dst 193.233.7/24 
```

### 总结
由于性能出色且功能丰富，ss 命令可以用来替代 netsate 命令成为我们日常查看 socket 相关信息的利器。其实抛弃 netstate 命令已经是大势所趋，有的 Linux 版本默认已经不再内置 netstate 而是内置了 ss 命令。