package spring.hk.springcenter.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spring.hk.springcenter.component.CompactDisc;

/**
 * @author I323691
 *
 */
@Component
public class CompactDiscImpl implements CompactDisc
{
	private Logger logger = LoggerFactory.getLogger(CompactDiscImpl.class);

	public void play() {
		logger.info("");
	}

}
