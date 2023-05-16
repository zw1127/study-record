1. find命令

```shell
# 查找features.xml文件中的内容, 包含 “commons-net”
$ find . -regex '.*features.xml'  | xargs -I{} grep -nH --color 'commons-net' "{}"

# 查找并删除
$ find . -name *openvswitch* | xargs -I{} rm -r "{}"

# 查找kar包，并复制到backup/kar目录
$ find . -type f -name "*.kar" | grep -v 'parent' | xargs -I{} cp "{}" /d/backup/kar

# 查找所有的文本文件，并将文本文件格式转换为Unix格式的
$ find . -type f -exec grep -Iq . {} \; -print |xargs -I{} dos2unix "{}"

# 查找所有的文本文件，内容中包含 “snat_port” 的文件
$ find . -type f | xargs grep "snat_port"
```

2. 查询内存使用率

```shell
$ free | grep Mem | awk '{printf("%.2f%%\n"), $3/$2*100}'
```

3. 查看系统的架构

```shell
$ dpkg --print-architecture
amd64

$ arch
x86_64

$ file /lib/systemd/systemd
/lib/systemd/systemd: ELF 64-bit LSB shared object, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, BuildID[sha1]=9fec572505a9fe7cc5ae1ed11b617d391888ac6d, for GNU/Linux 3.2.0, stripped
```

4. tcpdump命令

```shell
$ tcpdump -s 0 -i eth0 -w /opt/test.cap host 192.168.1.100

$ tcpdump -i any host 192.168.1.100 -w test2.pcap
```

5. cpu

```shell
# 总核数 = 物理CPU个数 X 每颗物理CPU的核数
# 总逻辑CPU数 = 物理CPU个数 X 每颗物理CPU的核数 X 超线程数

# 查看物理CPU个数
$ cat /proc/cpuinfo| grep "physical id"| sort| uniq| wc -l

# 查看每个物理CPU中core的个数(即核数)
$ cat /proc/cpuinfo| grep "cpu cores"| uniq

# 查看逻辑CPU的个数
$ cat /proc/cpuinfo| grep "processor"| wc -l
```

6. tar命令

```shell
# 打包为tar.gz包
$ tar czvf opendaylight-0.9.2.tar.gz opendaylight-0.9.2

# 解压到当前目录
$ tar vxf opendaylight-0.9.2.tar.gz
```