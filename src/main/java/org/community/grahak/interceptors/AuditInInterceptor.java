package org.community.grahak.interceptors;


import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditInInterceptor extends AbstractPhaseInterceptor<Message> {
	
	private static Logger log = LoggerFactory.getLogger(AuditInInterceptor.class);

	public AuditInInterceptor() {
		this(Phase.RECEIVE);
	}

	public AuditInInterceptor(String phase) {
		super(phase);
	}

	@Override
	public void handleMessage(Message message) {

		if (log.isDebugEnabled()) {
			log.info("In Interceptor chain: " + message.getInterceptorChain());
		}

	}

}
