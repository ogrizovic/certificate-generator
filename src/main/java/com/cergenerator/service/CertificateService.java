package com.cergenerator.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import com.cergenerator.model.CertificateData;

@Service
public class CertificateService {

//	private static final String BC = BouncyCastleProvider.PROVIDER_NAME;
	
	public CertificateService() {
		// TODO Auto-generated constructor stub
	}
	
	public CertificateData newCertificate(CertificateData cerData) throws OperatorCreationException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		
		String BC = BouncyCastleProvider.PROVIDER_NAME;
		Security.addProvider(new BouncyCastleProvider());
		
		System.out.println("Provajder: " + BC);
		// a sample key pair.
		
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
				new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
				new BigInteger("11", 16));
		
		RSAPrivateCrtKeySpec privKeySpec = new RSAPrivateCrtKeySpec(new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
	            new BigInteger("11", 16),
	            new BigInteger("9f66f6b05410cd503b2709e88115d55daced94d1a34d4e32bf824d0dde6028ae79c5f07b580f5dce240d7111f7ddb130a7945cd7d957d1920994da389f490c89", 16),
	            new BigInteger("c0a0758cdf14256f78d4708c86becdead1b50ad4ad6c5c703e2168fbf37884cb", 16),
	            new BigInteger("f01734d7960ea60070f1b06f2bb81bfac48ff192ae18451d5e56c734a5aab8a5", 16),
	            new BigInteger("b54bb9edff22051d9ee60f9351a48591b6500a319429c069a3e335a1d6171391", 16),
	            new BigInteger("d3d83daf2a0cecd3367ae6f8ae1aeb82e9ac2f816c6fc483533d8297dd7884cd", 16),
	            new BigInteger("b8f52fc6f38593dabb661d3f50f8897f8106eee68b1bce78a95b132b4e5b5d19", 16));
		
		// set up the keys
		
		PrivateKey privKey;
		PublicKey pubKey;
		
		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
		
		privKey = fact.generatePrivate(privKeySpec);
		pubKey = fact.generatePublic(pubKeySpec);
		
		// name table.
		
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
/*		builder.addRDN(BCStyle.CN, cerData.getCommonName());
		builder.addRDN(BCStyle.O, cerData.getOrganization());
		builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
		builder.addRDN(BCStyle.C, cerData.getCountry());
		builder.addRDN(BCStyle.ST, cerData.getState());
		builder.addRDN(BCStyle.L, cerData.getCity());*/
		
		builder.addRDN(BCStyle.CN, "CommonName");
		builder.addRDN(BCStyle.O, "Organization");
		builder.addRDN(BCStyle.OU, "OrganizationUnit");
		builder.addRDN(BCStyle.C, "Country");
		builder.addRDN(BCStyle.ST, "State");
		builder.addRDN(BCStyle.L, "Locality");
		
		// create the certificate v3
		
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(privKey);
		X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
				builder.build(), 
				BigInteger.valueOf(1), 
				new Date(System.currentTimeMillis() - 50000),
				new Date(System.currentTimeMillis() + 50000), 
				builder.build(), 
				pubKey);
				
		X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));	
		
		System.out.println(cert);
		
		Certificate[] chain = new X509Certificate[1];
		chain[0] = cert;
		
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, null);
			keyStore.setKeyEntry("Aliasss", privKey, "password".toCharArray(), chain);
			keyStore.store(new FileOutputStream(".keystore"), "password".toCharArray());
		} catch (KeyStoreException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	
	}
}
