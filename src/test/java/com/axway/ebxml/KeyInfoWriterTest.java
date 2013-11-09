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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;

import java.io.IOException;
import java.security.cert.CertificateException;

/**
 * Junit for testing KeyInfoWriter
 */
public class KeyInfoWriterTest
{
	@Test
	public void testBuildKeyInfo()
		throws KeyInfoWriterException, IOException, CertificateException, KeyResolverException
	{
		KeyInfoWriter keyInfoWriter = KeyInfoWriter.getInstance();
		KeyInfo keyInfo = keyInfoWriter.buildKeyInfo(new CertificateChain("src/test/data/self-signed.p7b").getCertificates());
		assertFalse("KeyInfo should not contain a KeyName", keyInfo.containsKeyName());
		assertTrue("KeyInfo should contain a KeyValue", keyInfo.containsKeyValue());
		assertTrue("KeyInfo should contain a X509Data", keyInfo.containsX509Data());

		try
		{
			keyInfoWriter.buildKeyInfo(null);
			fail("Expected exception for null KeyInfo");
		}
		catch (KeyInfoWriterException e)
		{
			fail("Expected IllegalArgumentException for null KeyInfo");
		}
		catch (IllegalArgumentException e)
		{
		}
	}
}
