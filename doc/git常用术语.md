* WIP Work in progress, do not merge yet. // 开发中
* LGTM Looks good to me. // Riview 完别人的 PR ，没有问题
* PTAL Please take a look. // 帮我看下，一般都是请别人 review 自己的 PR
* CC Carbon copy // 一般代表抄送别人的意思
* RFC — request for comments. // 我觉得这个想法很好，我们来一起讨论下
* IIRC — if I recall correctly. // 如果我没记错
* ACK — acknowledgement. // 我确认了或者我接受了，我承认了
* NACK/NAK — negative acknowledgement. // 我不同意


#### 一、git命令统计

##### 1、统计某人代码提交量

```shell
git log --author="mengfanxiao" --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }' -
```

##### 2、统计所有人代码提交量（指定统计提交文件类型）
```shell
git log --format='%aN' | sort -u | while read name; do echo -en "$name\t"; git log --author="$name" --pretty=tformat: --numstat | grep "\(.html\|.java\|.xml\|.properties\|.css\|.js\|.txt\)$" | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }' -; done
```

##### 3、统计某时间范围内的代码提交量
```shell
git log --author=mengfanxiao --since=2019-01-01 --until=2021-02-01 --format='%aN' | sort -u | while read name; do echo -en "$name\t"; git log --author="$name" --pretty=tformat: --numstat | grep "\(.html\|.java\|.xml\|.properties\)$" | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }' -; done
```
结果：
```shell
added lines: 106243, removed lines: 14088, total lines: 92155
```
##### 4、查看git提交前5名
```shell
git log --pretty='%aN' | sort | uniq -c | sort -k1 -n -r | head -n 5
```

##### 5、贡献值统计
```shell
git log --pretty='%aN' | sort -u | wc -l
```

##### 6、提交数统计
```shell
git log --oneline | wc -l
```

##### 7、统计或修改的行数
```shell
git log --stat|perl -ne 'END { print $c } $c += $1 if /(\d+) insertions/'
```