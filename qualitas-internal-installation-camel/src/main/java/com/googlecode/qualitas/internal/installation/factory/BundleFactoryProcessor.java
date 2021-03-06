package com.googlecode.qualitas.internal.installation.factory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import com.googlecode.qualitas.engines.api.configuration.ProcessStatus;
import com.googlecode.qualitas.engines.api.configuration.ProcessType;
import com.googlecode.qualitas.engines.api.core.Bundle;
import com.googlecode.qualitas.engines.api.factory.BundleFactory;
import com.googlecode.qualitas.internal.installation.core.AbstractProcessor;
import com.googlecode.qualitas.internal.installation.core.FailureStatus;
import com.googlecode.qualitas.internal.installation.core.QualitasHeadersNames;
import com.googlecode.qualitas.internal.installation.core.SuccessfulStatus;

/**
 * The Class BundleFactoryProcessor.
 */
@Component
@SuccessfulStatus(ProcessStatus.CREATION_OK)
@FailureStatus(ProcessStatus.CREATION_ERROR)
public class BundleFactoryProcessor extends AbstractProcessor {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        
        String qualitasProcessTypeString = (String) in.getHeader(QualitasHeadersNames.QUALITAS_PROCESS_TYPE_HEADER);
        ProcessType processType = ProcessType.valueOf(qualitasProcessTypeString);
        
        byte[] contents = exchange.getIn().getBody(byte[].class);
        
        BundleFactory factory = findQualitasComponent(BundleFactory.class, processType);
        Bundle bundle = factory.createProcessBundle(contents);
        
        Message out = exchange.getOut();
        out.setBody(bundle);
    }

}
