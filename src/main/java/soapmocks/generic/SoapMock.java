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
package soapmocks.generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import soapmocks.api.ProxyDelegator;
import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;
import soapmocks.generic.proxy.ProxyHandler;
import soapmocks.generic.proxy.ProxyHandlerResult;

public abstract class SoapMock extends
	com.sun.xml.ws.transport.http.servlet.WSServlet {

    private static final Log LOG = LogFactory.create(SoapMock.class);
    private static final long serialVersionUID = 1L;

    private MockPercentageLog mockPercentageLog = new MockPercentageLog();
    private StaticFileHandler staticFileHandler = new StaticFileHandler();
    private ProxyHandler proxyHandler = new ProxyHandler();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException {
	try {
	    doGetInternal(req, resp);
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new RuntimeException(t);
	}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException {
	try {
	    ProxyDelegator.restart();
	    doPostInternal(req, resp);
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new RuntimeException(t);
	} finally {
	    ProxyDelegator.restart();
	}
    }

    private void doGetInternal(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	String uri = req.getRequestURI();
	LOG.out("GET " + uri);
	if (!staticFileHandler.containsKey(uri)) {
	    super.doGet(req, resp);
	} else {
	    send(resp, new ByteArrayInputStream(("Found " + uri).getBytes()));
	}
    }

    private void doPostInternal(HttpServletRequest requestOriginal,
	    HttpServletResponse respOriginal) throws Exception {
	BackupHttpServletRequest req = new BackupHttpServletRequest(
		requestOriginal);
	BackupHttpServletResponse resp = new BackupHttpServletResponse(
		respOriginal);
	String uri = req.getRequestURI();
	LOG.out("POST " + uri);
	if (!staticFileHandler.containsKey(uri)) {
	    jaxWsFirstThenProxy(req, resp, uri);
	} else {
	    resp.setHeader("Content-Type", "text/xml;charset=utf-8");
	    GenericSoapResponse soapResponse;
	    soapResponse = staticFileHandler
		    .findResponseByPropertiesAndRequest(req, uri);
	    if (soapResponse != null
		    && soapResponse.getResponseStream() != null) {
		int code = soapResponse.getResponseCode();
		resp.setStatus(code);
		send(resp, soapResponse.getResponseStream());
		resp.commit();
		LOG.out("MOCKED (Generic Config) Response sent (" + code
			+ "). " + mockPercentageLog.logMock() + "\n");
	    } else {
		sendFault(resp);
		resp.commit();
		LOG.out("Fault sent. " + mockPercentageLog.logMock() + " \n");
	    }
	}
    }

    private void jaxWsFirstThenProxy(BackupHttpServletRequest req,
	    BackupHttpServletResponse resp, String uri)
	    throws ServletException, IOException {
	super.doPost(req, resp);
	if (!ProxyDelegator.isDelegateToProxy()
		&& !resp.getResponse().trim().isEmpty()) {
	    resp.commit();
	    LOG.out(resp.getResponse());
	    LOG.out("MOCKED (JaxWS) Response sent. "
		    + mockPercentageLog.logMock() + "\n");
	} else {
	    if (proxyHandler.isProxy(uri)) {
		LOG.out("Using Proxy now...");
		ProxyHandlerResult proxyHandlerResult = proxyHandler.doPost(uri, req, resp);
		resp.commit();
		if(proxyHandlerResult.isSuccessful()) {
		    LOG.out("Proxy Response sent (took " + proxyHandlerResult.getTookTimeMillis() + "ms). "
			+ mockPercentageLog.logProxy() + "\n");
		} else {
		    LOG.out("Proxy Response failed, sending mocked fault (took " + proxyHandlerResult.getTookTimeMillis() + "ms). "
				+ mockPercentageLog.logProxyFailed() + "\n");
		}
	    } else {
		String additionalMessage = "No mock or proxy found.";
		sendFault(resp, additionalMessage);
		resp.commit();
		LOG.out("Fault sent. " + additionalMessage + " "
			+ mockPercentageLog.logMock() + " \n");
	    }
	}
    }

    private void sendFault(HttpServletResponse resp) throws IOException {
	sendFault(resp, null);
    }

    private void sendFault(HttpServletResponse resp, String additionalMessage)
	    throws IOException {
	String message = "SOAPMOCKS did not find a fitting response for the given request.";
	if (additionalMessage != null) {
	    message += " " + additionalMessage;
	}
	String fault = new FaultCreator().createFault(message);
	resp.setStatus(500);
	IOUtils.copy(new ByteArrayInputStream(fault.getBytes()),
		resp.getOutputStream());
    }

    

    private void send(HttpServletResponse resp, InputStream soapResponse)
	    throws IOException {
	IOUtils.copy(soapResponse, resp.getOutputStream());
    }

}
