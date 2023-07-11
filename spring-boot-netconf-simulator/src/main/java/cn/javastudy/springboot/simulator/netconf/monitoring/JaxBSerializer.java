package cn.javastudy.springboot.simulator.netconf.monitoring;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JaxBSerializer {
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(NetconfState.class);
        } catch (final JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Element toXml(final NetconfState monitoringModel) {
        final DOMResult res;
        try {
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            res = new DOMResult();
            marshaller.marshal(monitoringModel, res);
        } catch (final JAXBException e) {
            throw new IllegalStateException("Unable to serialize netconf state " + monitoringModel, e);
        }
        return ((Document) res.getNode()).getDocumentElement();
    }
}
