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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateChainTest
{
	@Test
	public void testGetCertificates()
		throws IOException, CertificateException
	{
		X509Certificate[] certs = new CertificateChain("src/test/data/self-signed.p7b").getCertificates();
		assertEquals("One certificate was expected", 1, certs.length);

		certs = new CertificateChain("src/test/data/self-signed.cer").getCertificates();
		assertEquals("One certificate was expected", 1, certs.length);

		certs = new CertificateChain("src/test/data/VeriSignClass1IndividualA.p7b").getCertificates();
		assertEquals("Two certificates were expected", 2, certs.length);

		try
		{
			new CertificateChain(null).getCertificates();
			fail("IllegalArgumentException expected for null filename");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			new CertificateChain("src/test/data/bogusfilename.p7b").getCertificates();
			fail("FileNotFoundException expected for bogus filename");
		}
		catch (FileNotFoundException e)
		{
		}

		try
		{
			new CertificateChain("src/test/data/boguscert").getCertificates();
			fail("CertificateException expected for bogus Cert");
		}
		catch (CertificateException e)
		{
		}
	}
}
