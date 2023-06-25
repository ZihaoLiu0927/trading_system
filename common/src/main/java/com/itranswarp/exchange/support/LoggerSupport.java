package com.itranswarp.exchange.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggerSupport {

    /**
     * Child class should use this logger to log.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

}