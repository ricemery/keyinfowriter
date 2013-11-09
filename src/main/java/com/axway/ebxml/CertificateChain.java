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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a DER or p7b encoded certificate file
 */
public class CertificateChain
{
	private static Log logger = LogFactory.getLog(CertificateChain.class);

	private X509Certificate[] chain;

	/**
	 * Constructor
	 * @param certificatePath path to a p7b or DER encoded file
	 * @return Array of X509Certificate
	 * @throws java.io.FileNotFoundException
	 * @throws java.security.cert.CertificateException
	 */
	public CertificateChain(String certificatePath)
		throws CertificateException, IOException
	{
		if (certificatePath == null)
			throw new IllegalArgumentException("certificatePath expected");

		logger.debug("Loading certificate from: " + certificatePath);

		LinkedList<X509Certificate> returnList = new LinkedList<X509Certificate>();
		FileInputStream fis = new FileInputStream(certificatePath);
		try
		{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection certificates = cf.generateCertificates(fis);
			for (Object cert : certificates)
			{
				returnList.add((X509Certificate)cert);
				logger.debug("Certificate: " + cert);
			}
		}
		finally
		{
			fis.close();
		}

		chain = returnList.toArray(new X509Certificate[returnList.size()]);
	}

	/**
	 * Returns an array of certificates loaded from the p7b or DER encoded file
	 */
	public X509Certificate[] getCertificates()
	{
		return chain.clone();
	}
}
