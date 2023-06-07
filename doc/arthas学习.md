watch第一个参数中包含217和target class类名中包含ChannelTask的信息
```shell
watch com.fiberhome.sdn.netconfplugin.netconf.sync.task.AbstractQueryTask execute '{target,params[0]}' 'params[0].value.contains("217")&&target.class.simpleName.contains("ChannelTask")'

watch org.opendaylight.mdsal.binding.dom.codec.api.BindingNormalizedNodeSerializer toNormalizedNodeRpcData 'params,returnObj' 'params[0].class.simpleName.contains("CreateSubscription")' -x 2

watch org.opendaylight.mdsal.dom.api.DOMRpcService invokeRpc 'params,returnObj' 'params[1].toString.contains("notification")' -x 2
```

查看指定方法发生异常时的堆栈信息
```shell
watch --exception org.opendaylight.netconf.sal.connect.netconf.schema.mapping.NetconfMessageTransformer toRpcRequest throwExp -x 6
```

查看入参List中status值
```shell
watch org.springframework.data.jpa.repository.JpaRepository saveAll "params[0].{#this.status}" 'params[0].toString.contains("Ne")' -x 2

watch org.springframework.data.jpa.repository.JpaRepository saveAll "params[0].{#this.status}" 'params[0].toString.contains("Ne")' -x 2
```