/**
* Copyright 2010 Axway Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/
package com.axway.ebxml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.OutputStream;

public class XmlUtil
{
	private static Log logger = LogFactory.getLog(XmlUtil.class);

	/**
	 * Write the specified <code>Document</code> to the specified <code>OutputStream</code>
	 * @param document the <code>Document</code> to write out
	 * @param out	  the <code>OutputStream</code> to write to
	 * @throws java.io.IOException
	 */
	public static void writeTo(Document document, OutputStream out)
		throws KeyInfoWriterException
	{
		// Write the signed message to the provided OutputStream. If the provided
		// OutputStream is null then write the message to System.out
		if (document == null)
			throw new IllegalArgumentException("document cannot be null");

		if (out == null)
		{
			logger.debug("Writing document to System.out");
			out = System.out;
		}

		try
		{
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			LSOutput output = impl.createLSOutput();
			output.setByteStream(out);
			writer.write(document, output);
		}
		catch (ClassNotFoundException e)
		{
			throw new KeyInfoWriterException("Unexpected error serializing document to XML", e);
		}
		catch (InstantiationException e)
		{
			throw new KeyInfoWriterException("Unexpected error serializing document to XML", e);
		}
		catch (IllegalAccessException e)
		{
			throw new KeyInfoWriterException("Unexpected error serializing document to XML", e);
		}
	}

	/**
	 * Create a <code>Document</code> to use as a container to hold the <code>KeyInfo</code> being built.
	 * @return Constructed but empty <code>Document<code>
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public static org.w3c.dom.Document buildDocument()
		throws ParserConfigurationException
	{
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}
}
