package com.cergenerator.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import com.cergenerator.model.CertificateData;
import com.cergenerator.model.IssuerData;
import com.cergenerator.model.SubjectData;
import com.cergenerator.service.helper.CertType;
import com.cergenerator.service.helper.CertificateGenerator;
import com.cergenerator.service.helper.KeyStoreReader;
import com.cergenerator.service.helper.KeyStoreWriter;

@Service
public class CertificateService {

//	private static final String BC = BouncyCastleProvider.PROVIDER_NAME;
	
	public CertificateService() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public void newCertificate (CertificateData cerData, CertType certType){
		
			SubjectData subjectData = generateSubjectData(cerData);
			
			KeyPair keyPairIssuer = generateKeyPair(2048, "RSA");
			IssuerData issuerData = generateIssuerData(keyPairIssuer.getPrivate(), cerData, certType);
			
			CertificateGenerator cg = new CertificateGenerator();
			
			X509Certificate cert;
			if(certType == CertType.REGULAR){
				cert = cg.generateCertificate(subjectData, issuerData, false);
			}
			else {
				cert = cg.generateCertificate(subjectData, issuerData, true);
			}
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
			keyStoreWriter.saveKeyStore("src/main/resources/certificates"+cerData.getAlias()+".jks", cerData.getKeystorePass()); // keystore pass je isti kao i pass za key store entry
		
	}
	
	private SubjectData generateSubjectData(CertificateData cerData) {
		KeyPair keyPairSubject = generateKeyPair(cerData.getKeySize(), cerData.getKeyType());
		
		//SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + (cerData.getValidity() * 86400000));

		Random rand = new Random();
		String serialNumber = Integer.toString(rand.nextInt(Integer.MAX_VALUE));
//		String serialNumber = UUID.randomUUID().toString();
		//String serialNumber = "12345";
		
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

	private IssuerData generateIssuerData(PrivateKey issuerKey, CertificateData cerData, CertType certType) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		KeyStoreReader ksReader = new KeyStoreReader();
		
		if (certType == CertType.SELFSIGNED_CA){
			builder.addRDN(BCStyle.CN, cerData.getCommonName());
			builder.addRDN(BCStyle.O, cerData.getOrganization());
			builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
			builder.addRDN(BCStyle.C, cerData.getCountry());
			builder.addRDN(BCStyle.ST, cerData.getState());
			builder.addRDN(BCStyle.L, cerData.getCity());
			
			return new IssuerData(issuerKey, builder.build());
		}
		else if (certType == CertType.CA){
			return ksReader.readIssuerFromStore(cerData.getSignedBy()+".jks", cerData.getSignedBy(), "test".toCharArray(), "test".toCharArray());
		}
		else {
			return ksReader.readIssuerFromStore(cerData.getSignedBy()+".jks", cerData.getSignedBy(), "test".toCharArray(), "test".toCharArray());
		}
//	    builder.addRDN(BCStyle.CN, "Nikola Luburic");
//	    builder.addRDN(BCStyle.SURNAME, "Luburic");
//	    builder.addRDN(BCStyle.GIVENNAME, "Nikola");
//	    builder.addRDN(BCStyle.O, "UNS-FTN");
//	    builder.addRDN(BCStyle.OU, "Katedra za informatiku");
//	    builder.addRDN(BCStyle.C, "RS");
//	    builder.addRDN(BCStyle.E, "nikola.luburic@uns.ac.rs");
//	    //UID (USER ID) je ID korisnika
//	    builder.addRDN(BCStyle.UID, "654321");

//		//Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
//	    // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
//	    // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
//		return new IssuerData(issuerKey, builder.build());
	}
}
