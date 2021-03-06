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
package soapmocks.generic.listener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContext;

import com.sun.xml.ws.transport.http.ResourceLoader;

final class ServletResourceLoader implements ResourceLoader {

    private final ServletContext context;

    public ServletResourceLoader(ServletContext context) {
	this.context = context;
    }

    public URL getResource(String path) throws MalformedURLException {
	return context.getResource(path);
    }

    public URL getCatalogFile() throws MalformedURLException {
	return null;
    }

    public Set<String> getResourcePaths(String path) {
	return context.getResourcePaths(path);
    }
}