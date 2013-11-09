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
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Utility for building the ds:KeyInfo Element to include within the Certificate Element of an
 * ebXML CPP or CPA version 2.
 * (see <a href="http://www.oasis-open.org/committees/ebxml-cppa/documents/ebcpp-2.0.pdf">ebXML Collaboration-Protocol Profile and Agreement Specification version 2 )
 */
public class KeyInfoWriter
{
	private static Log logger = LogFactory.getLog(KeyInfoWriter.class);

	private static KeyInfoWriter keyInfoWriter;

	private KeyInfoWriter()
	{
		org.apache.xml.security.Init.init();
	}

	/**
	 * Get the singleton instance of the <link>KeyInfoWriter</link>
	 * @return <link>KeyInfoWriter</link> instance
	 */
	public static synchronized KeyInfoWriter getInstance()
	{
		if (keyInfoWriter == null)
			keyInfoWriter = new KeyInfoWriter();

		return keyInfoWriter;
	}

	/**
	 * Builds <code>KeyInfo</code> instance given an array of <code>X509Certificate</code>. The
	 * <code>KeyInfo</code> can be serialized and used within the Certificate element of an ebXML CPP or CPA
	 * @param certs Array of certificates to include. The certificates in the array must be related in a certificate chain
	 *		The first certificate in the array must be the end-entity certificate.
	 * @return Initialized <code>KeyInfo</code> ready to be serialized
	 * @throws IllegalArgumentException Null or Empty certificate list is passed as parameter
	 * @throws KeyInfoWriterException Thrown when there is any other error encountered. The <code>KeyInfoWriterException</code>
	 *  may wrap other exceptions caught within this method.
	 */
	public KeyInfo buildKeyInfo(X509Certificate[] certs)
		throws KeyInfoWriterException
	{
	    if (certs == null || certs.length == 0)
	        throw new IllegalArgumentException("cert is null or empty");

		try
		{
			org.w3c.dom.Document doc = XmlUtil.buildDocument();

			KeyInfo keyInfo = new KeyInfo(doc);
			X509Data x509Data;
			for (X509Certificate cert : certs)
			{
				if (cert == certs[0]) // Only add KeyInfo for the first certificate in the chain (the end entity certificate)
					keyInfo.add(cert.getPublicKey());
				x509Data = buildX509Data(doc, cert);  // Add X509Data elements for all the certificates in the chain
				keyInfo.add(x509Data);
			}

			doc.appendChild(keyInfo.getElement());
			return keyInfo;
		}
		catch (ParserConfigurationException e)
		{
			logger.error("Exception writing KeyInfo", e);
			throw new KeyInfoWriterException(e);
		}
		catch (XMLSecurityException e)
		{
			logger.error("Exception writing KeyInfo", e);
			throw new KeyInfoWriterException(e);
		}
	}

	/**
	 * Builds the X509Data element sub-element of the KeyInfo element
	 * @param doc Containing document to create the element for. Cannot be null.
	 * @param cert <code>X509Certificate</code> to create the <code>X509Data</code> element for. Cannot be null.
	 * @return Populated <code>X509Data</code>
	 * @throws XMLSecurityException
	 */
	private X509Data buildX509Data(org.w3c.dom.Document doc, X509Certificate cert)
		throws XMLSecurityException
	{
		X509Data x509Data;
		x509Data = new X509Data(doc);
		x509Data.addIssuerSerial(cert.getIssuerDN().getName(), cert.getSerialNumber());
		x509Data.addSubjectName(cert);
		x509Data.addCertificate(cert);
		return x509Data;
	}

	/**
	 * Loads the certificate (or certificate chain) from <code>certificatePath</code> builds the KeyInfo element
	 * and writes the resulting XML to the path specified by <code>outputPath</code>
	 * @param certificatePath Path to the certificate to build the KeyInfo element for. The certificate can be p7b encoded
	 * or DER encoded.
	 * @param outputPath Path/Filename to write the KeyInfo element into. Null indicates System.out
	 * @throws KeyInfoWriterException Thrown if there is any error in loading the certificate, creating the KeyInfo element or
	 * serializing the result.
	 */
	public void writeKeyInfo(String certificatePath, String outputPath)
		throws KeyInfoWriterException
	{
		try
		{
			logger.info("Loading certificate " + certificatePath);
			X509Certificate[] certificates = new CertificateChain(certificatePath).getCertificates();
			KeyInfo keyInfo = buildKeyInfo(certificates);
			logger.info("Writing KeyInfo to " + (outputPath == null ? "System.out" : outputPath));
			FileOutputStream stream = outputPath == null ? null : new FileOutputStream(outputPath);
			XmlUtil.writeTo(keyInfo.getDocument(), stream);
		}
		catch (FileNotFoundException e)
		{
			logger.error("Exception writing KeyInfo", e);
			throw new KeyInfoWriterException(e);
		}
		catch (CertificateException e)
		{
			logger.error("Exception writing KeyInfo", e);
			throw new KeyInfoWriterException(e);
		}
		catch (IOException e)
		{
			logger.error("Exception writing KeyInfo", e);
			throw new KeyInfoWriterException(e);
		}
	}

	/**
	 * Runs the KeyInfoWriter Utility
	 * @param args Usage: java com.axway.ebxml.KeyInfoWriter certificatePath [outputFilePath]. If the outputFilePath
	 * parameter is not included the resulting KeyInfo element XML will be written to System.out
	 */
	public static void main(String[] args)
	{
	    if (args.length != 1 && args.length != 2)
	    {
	        System.out.println("Usage: java com.axway.ebxml.KeyInfoWriter certificatePath [outputFilePath]");
	        return;
	    }

	    try
	    {
			String certificatePath = args[0];
			String outputFilePath = args.length == 1 ? null : args[1];
	        KeyInfoWriter writer = KeyInfoWriter.getInstance();
	        writer.writeKeyInfo(certificatePath, outputFilePath);
	    }
	    catch (Exception e)
	    {
		    logger.error(e);
	        e.printStackTrace();
	    }
	}
}
