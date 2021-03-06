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
package soapmocks.generic.proxy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import soapmocks.api.ProxyDelegator;
import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;

final class ProxyRecordHandler {

    private static final Log LOG = LogFactory.create(ProxyRecordHandler.class);

    void handleProxyRecord(ProxyResult proxyResult) throws IOException {
	if(proxyResult.isGeneratedFault) {
	    LOG.out("No proxy record for generated Fault");
	    return;
	}
	if (ProxyDelegator.hasServiceIdentifier()) {
	    ProxyServiceIdentifier serviceIdentifier = ProxyDelegator
		    .getServiceIdentifier();
	    addSimpleFile(proxyResult, serviceIdentifier);
	    addHashedFile(proxyResult, serviceIdentifier);
	}
    }

    private void addSimpleFile(ProxyResult proxyResult,
	    ProxyServiceIdentifier serviceIdentifier) throws IOException {
	String pathnameSimple = ProxyRecordConfig.getProxyTraceAbsoluteDir()
		+ serviceIdentifier.generateFilename();
	File fileSimple = new File(pathnameSimple);
	if (!fileSimple.exists()) {
	    LOG.out("Proxy recorded to first " + fileSimple.getName());
	    FileUtils
		    .writeByteArrayToFile(fileSimple, proxyResult.bodyDeflated);
	}
    }

    private void addHashedFile(ProxyResult proxyResult,
	    ProxyServiceIdentifier serviceIdentifier) throws IOException {
	String hash = new Filehasing().hash(proxyResult.bodyDeflated, serviceIdentifier.getResponseIdentifier());
	String pathnameWithHash = ProxyRecordConfig.getProxyTraceAbsoluteDir()
		+ serviceIdentifier.generateFilename(hash);
	File fileWithHash = new File(pathnameWithHash);
	if (!fileWithHash.exists()) {
	    LOG.out("Proxy recorded to hashed " + fileWithHash.getName());
	    FileUtils.writeByteArrayToFile(fileWithHash,
		    proxyResult.bodyDeflated);
	} else {
	    LOG.out("Proxy existing record skipped: " + fileWithHash.getName());
	}
    }

    

}
