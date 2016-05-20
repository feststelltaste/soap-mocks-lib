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

public final class ProxyServiceIdentifier {

    private final String method;
    
    private final String[] parameters;

    public ProxyServiceIdentifier(String method, String... parameters) {
	this.method = method;
	this.parameters = parameters;
    }
    
    String[] getParameters() {
	return parameters;
    }

    String getMethod() {
	return method;
    }
    
    String generateFilename() {
	return generateFilename(null);
    }
    
    String generateFilename(String hash) {
	StringBuilder filename = new StringBuilder();
	filename.append(method);
	for (String parameter : parameters) {
	    filename.append("-").append(parameter);
	}
	addHashIfNotNull(hash, filename);
	return filename.append(".xml").toString();
    }

    private void addHashIfNotNull(String hash, StringBuilder filename) {
	if(hash!=null) {
	    filename.append(".resp-" + hash);
	}
    }
}
