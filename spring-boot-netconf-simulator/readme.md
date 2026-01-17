1、netonf通道建立后，会进行schema的协商，网管会下发如下报文

```xml

<rpc message-id="101" xmlns="urn:ietf:params:xml:ns:netconf:base:1.0">
    <get>
        <filter type="subtree">
            <netconf-state xmlns="urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring">
                <schemas/>
            </netconf-state>
        </filter>
    </get>
</rpc>
```

设备返回的内容如下：

```xml
<data xmlns="urn:ietf:params:xml:ns:netconf:base:1.0">
  <netconf-state xmlns="urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring">
    <schemas>
      <schema>
        <identifier>openconfig-rib-bgp-ext</identifier>
        <version>2016-10-17</version>
        <format>yang</format>
        <namespace>http://openconfig.net/yang/rib/bgp-ext</namespace>
        <location>http://192.168.1.249/fs/netconf/openconfig-rib-bgp-ext@2016-10-17.yang</location>
      </schema>
      <schema>
        <identifier>rg-grpc</identifier>
        <version>2022-04-12</version>
        <format>yang</format>
        <namespace>urn:rg:params:xml:ns:yang:rg-grpc</namespace>
        <location>http://192.168.1.249/fs/netconf/rg-grpc@2022-04-12.yang</location>
      </schema>
      <schema>
        <identifier>openconfig-platform-port</identifier>
        <version>2018-11-21</version>
        <format>yang</format>
        <namespace>http://openconfig.net/yang/platform/port</namespace>
        <location>http://192.168.1.249/fs/netconf/openconfig-platform-port@2018-11-21.yang</location>
      </schema>
      <schema>
        <identifier>ietf-ip</identifier>
        <version>2018-01-09</version>
        <format>yang</format>
        <namespace>urn:ietf:params:xml:ns:yang:ietf-ip</namespace>
        <location>http://192.168.1.249/fs/netconf/ietf-ip@2018-01-09.yang</location>
      </schem>
    </schemas>
  </netconf-state>
</data>
```
2、根据获取的schema数据，获取schema实例
下发如下报文：
```xml

<rpc message-id="102" xmlns="urn:ietf:params:xml:ns:netconf:base:1.0">
    <get-schema xmlns="urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring">
        <identifer>openconfig-isis-routing</identifer>
        <version>2017-08-24</version>
    </get-schema>
</rpc>

```
正常情况下，设备会返回这个yang模型的，目前设备没有返回
