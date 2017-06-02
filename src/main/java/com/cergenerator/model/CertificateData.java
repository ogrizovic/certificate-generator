/*
POJO koji prima ulazne podatke. Iz njega se izvlace podaci potrebni za SubjectData i IssuerData.
*/
package com.cergenerator.model;

public class CertificateData {

	private String serialNumber;	//automatski
	private boolean ca;
	private boolean revoked;
	
	private int keySize;
	private String keyType;

	private String commonName;
	private String organization;
	private String organizationUnit;
	private String city;
	private String state;
	private String country;
	private int validity;
	private String alias;
	private char[] keystorePass = "test".toCharArray();

	private String signedBy;
	
	public CertificateData() {
		// TODO Auto-generated constructor stub
	}
	


	public CertificateData(boolean ca, boolean revoked, int keySize, String keyType, String commonName,
			String organization, String organizationUnit, String city, String state, String country, int validity,
			String alias, char[] keystorePass, String signedBy) {
		super();
		this.ca = ca;
		this.revoked = revoked;
		this.keySize = keySize;
		this.keyType = keyType;
		this.commonName = commonName;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.city = city;
		this.state = state;
		this.country = country;
		this.validity = validity;
		this.alias = alias;
		this.keystorePass = keystorePass;
		this.signedBy = signedBy;
	}




	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Boolean.toString(ca) + " " + Boolean.toString(revoked) + " " + keySize + " " + keyType +
				" " + commonName + " " + organization + " " + organizationUnit + " " + city + " " + state + " " + country +
				" " + validity + " " + alias + " " + keystorePass.toString() + " " + signedBy + " " + "\n";
	}

	

	public String getSignedBy() {
		return signedBy;
	}




	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}




	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getKeyType() {
		return keyType;
	}


	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public boolean isCa() {
		return ca;
	}

	public void setCa(boolean ca) {
		this.ca = ca;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getValidity() {
		return validity;
	}

	public void setValidity(int validity) {
		this.validity = validity;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public char[] getKeystorePass() {
		return keystorePass;
	}

	public void setKeystorePass(char[] keystorePass) {
		this.keystorePass = keystorePass;
	}
	
	
	
}
