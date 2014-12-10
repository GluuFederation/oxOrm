package org.xdi.util.security;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;

/**
 * Helps to load X509Certificate
 * 
 * @author Yuriy Movchan Date: 04/24/2014
 */
public class CertificateHelper {

	public static X509Certificate loadCertificate(String certificate) throws CertificateException {
		return loadCertificate(certificate.getBytes());
	}

	public static X509Certificate loadCertificate(byte[] certificate) throws CertificateException {
		CertificateFactory fty = CertificateFactory.getInstance("X.509");
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(certificate));
		X509Certificate x509Certificate = (X509Certificate) fty.generateCertificate(bais);

		return x509Certificate;
	}
}