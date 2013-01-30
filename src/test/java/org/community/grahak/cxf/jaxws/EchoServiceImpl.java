package org.community.grahak.cxf.jaxws;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class EchoServiceImpl implements EchoService
{
    private static Logger log = LoggerFactory.getLogger(EchoServiceImpl.class);
	@Override
	public String echo(String message) {
        log.info("==== EchoServiceImpl:echo() === ");
		return "EchoServiceImpl:"+message;
	}

	@Override
	public String anotherEcho(String message) {
        log.info("==== EchoServiceImpl:anotherEcho() === ");
		return "EchoServiceImpl:"+message;
	}
}
