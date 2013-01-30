package org.community.grahak.cxf.jaxws;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class AnotherEchoServiceImpl implements EchoService
{
    private static Logger log = LoggerFactory.getLogger(AnotherEchoServiceImpl.class);
	@Override
	public String echo(String message) {

        log.info("==== AnotherEchoServiceImpl:echo() === ");
		return "AnotherEchoServiceImpl:"+message;
	}

	@Override
	public String anotherEcho(String message) {
        log.info("==== AnotherEchoServiceImpl:anotherEcho() === ");
		return "AnotherEchoServiceImpl:"+message;
	}
}
