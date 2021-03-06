/*
Copyright 2016 Peter Bilstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package soapmocks.generic.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Log {

    private final Logger logger;

    Log(String clazz) {
	this.logger = LoggerFactory.getLogger(clazz);
    }

    public void out(String message) {
	logger.info(message);
    }

    public void info(String message) {
	logger.info(message);
    }
    
    public void warn(String message) {
	logger.warn(message);
    }

    public void debug(String message) {
	logger.debug(message);
    }

    public void error(Throwable t, String message) {
	logger.error(message, t);
    }

    public void error(Throwable t) {
	logger.error(t.getMessage(), t);
    }
}
