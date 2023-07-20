package cn.javastudy.springboot.simulator.netconf.service.impl;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.getRandomDecimal;

import cn.javastudy.springboot.simulator.netconf.properties.DynamicConfig;
import cn.javastudy.springboot.simulator.netconf.utils.Utils;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("RegexpSinglelineJava")
public class XPathTest {

    @Test
    public void namespaceTest() throws Exception {
        Document document = XmlUtil.readXmlToDocument(
            XPathTest.class.getResourceAsStream("/message/simulated-config.xml"));

        String path = "/rpc-reply/data/components/component/state/temperature/instant";
        DynamicConfig config = new DynamicConfig();

        config.setScale(1);
        config.setStart("1.0");
        config.setEnd("100.0");
        config.setPath(path);
        NodeList titleNodes = Utils.evaluate(document, config);
        Assert.assertNotNull(titleNodes);

        // 输出查询结果
        for (int i = 0; i < titleNodes.getLength(); i++) {
            Element titleElement = (Element) titleNodes.item(i);
            titleElement.setTextContent(generateDynamicValue(config));
            System.out.println(titleElement.getTextContent());
        }
//        System.out.println(XmlUtils.toString(document));
    }

    private String generateDynamicValue(DynamicConfig config) {
        Integer scale = config.getScale();

        BigDecimal min = new BigDecimal(config.getStart());
        BigDecimal max = new BigDecimal(config.getEnd());

        BigDecimal randomDecimal = getRandomDecimal(min, max, scale);
        return randomDecimal.toString();
    }

    @Test
    public void testBuildPath() {
        String expect = "/*[local-name()='data']/*[local-name()='components']"
            + "/*[local-name()='component']/*[local-name()='state']/*[local-name()='temperature']/*[local-name()"
            + "='instant']";
        String path = "/data/components/component/state/temperature/instant";
        Assert.assertEquals(Utils.buildPath(path), expect);
    }
}
