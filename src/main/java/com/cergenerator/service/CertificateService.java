package com.cergenerator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import com.cergenerator.model.CertificateData;
import com.cergenerator.model.IssuerData;
import com.cergenerator.model.SubjectData;
import com.cergenerator.service.helper.CertificateGenerator;
import com.cergenerator.service.helper.KeyStoreReader;
import com.cergenerator.service.helper.KeyStoreWriter;

@Service
public class CertificateService {

//	private static final String BC = BouncyCastleProvider.PROVIDER_NAME;
	
	public CertificateService() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public byte[] newCertificate (CertificateData cerData){
		try {
			SubjectData subjectData = generateSubjectData(cerData);
			
			KeyPair keyPairIssuer = generateKeyPair(2048, "RSA");
			IssuerData issuerData = generateIssuerData(keyPairIssuer.getPrivate());
			
			CertificateGenerator cg = new CertificateGenerator();
			X509Certificate cert = cg.generateCertificate(subjectData, issuerData);
			
			System.out.println("\n===== Podaci o izdavacu sertifikata =====");
			System.out.println(cert.getIssuerX500Principal().getName());
			System.out.println("\n===== Podaci o vlasniku sertifikata =====");
			System.out.println(cert.getSubjectX500Principal().getName());
			System.out.println("\n===== Sertifikat =====");
			System.out.println("-------------------------------------------------------");
			System.out.println(cert);
			System.out.println("-------------------------------------------------------");
			

			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(null, cerData.getKeystorePass());
			keyStoreWriter.write(cerData.getAlias(), subjectData.getPrivateKey(), cerData.getKeystorePass(), cert);
			keyStoreWriter.saveKeyStore(cerData.getAlias()+".jks", cerData.getKeystorePass()); // keystore pass je isti kao i pass za key store entry
		
			KeyStoreReader keyStoreReader = new KeyStoreReader();
			X509Certificate certificate = (X509Certificate) keyStoreReader.readCertificate(cerData.getAlias()+".jks", cerData.getKeystorePass(), cerData.getAlias());
			
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(certificate);
			
			return b.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	private SubjectData generateSubjectData(CertificateData cerData) {
		KeyPair keyPairSubject = generateKeyPair(cerData.getKeySize(), cerData.getKeyType());
		
		//SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + (cerData.getValidity() * 86400000));

		//String serialNumber = UUID.randomUUID().toString();
		String serialNumber = "12345";
		
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, cerData.getCommonName());
		builder.addRDN(BCStyle.O, cerData.getOrganization());
		builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
		builder.addRDN(BCStyle.C, cerData.getCountry());
		builder.addRDN(BCStyle.ST, cerData.getState());
		builder.addRDN(BCStyle.L, cerData.getCity());
		
		return new SubjectData(keyPairSubject.getPrivate(), keyPairSubject.getPublic(), builder.build(), serialNumber, startDate, endDate);
	}
	
	private KeyPair generateKeyPair(int keySize, String keyType) {
        try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType); 
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(keySize, random);
			return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
        return null;
	}

	private IssuerData generateIssuerData(PrivateKey issuerKey) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
	    builder.addRDN(BCStyle.CN, "Nikola Luburic");
	    builder.addRDN(BCStyle.SURNAME, "Luburic");
	    builder.addRDN(BCStyle.GIVENNAME, "Nikola");
	    builder.addRDN(BCStyle.O, "UNS-FTN");
	    builder.addRDN(BCStyle.OU, "Katedra za informatiku");
	    builder.addRDN(BCStyle.C, "RS");
	    builder.addRDN(BCStyle.E, "nikola.luburic@uns.ac.rs");
	    //UID (USER ID) je ID korisnika
	    builder.addRDN(BCStyle.UID, "654321");

		//Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
	    // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
	    // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
		return new IssuerData(issuerKey, builder.build());
	}
	
	/*public CertificateData newCertificate(CertificateData cerData) throws OperatorCreationException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		
		String BC = BouncyCastleProvider.PROVIDER_NAME;
		Security.addProvider(new BouncyCastleProvider());
		
		Random rdm = SecureRandom.getInstanceStrong();
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
				new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
//				new BigInteger(1024, rdm),
				new BigInteger("11", 16)); // 17?
		
//		RSAPrivateCrtKeySpec privKeySpec = new RSAPrivateCrtKeySpec(new BigInteger(1024, rdm),
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
		builder.addRDN(BCStyle.CN, cerData.getCommonName());
		builder.addRDN(BCStyle.O, cerData.getOrganization());
		builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
		builder.addRDN(BCStyle.C, cerData.getCountry());
		builder.addRDN(BCStyle.ST, cerData.getState());
		builder.addRDN(BCStyle.L, cerData.getCity());
		
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
		
		cert.checkValidity(new Date(System.currentTimeMillis() - 1000));
		cert.verify(pubKey);
		
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
		
	
	}*/
}
