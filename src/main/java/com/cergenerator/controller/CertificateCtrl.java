package com.cergenerator.controller;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cergenerator.model.CertificateData;
import com.cergenerator.service.CertificateService;
import com.cergenerator.service.helper.CertType;

@RestController
@RequestMapping("/certificate")
public class CertificateCtrl {

	@Autowired
	private CertificateService cerService;
	
	public CertificateCtrl() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ArrayList<CertificateData> getAll(){
		// TODO: Implement
		return null;
	}
	
	@RequestMapping(value = "/{id}", 
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CertificateData> getById(int id){
		// TODO: Implement
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void add(@RequestBody CertificateData cer) throws Exception{
		System.out.println(cer.toString());
		if (cer.isCa() == true){
			if (cer.getSignedBy().equals("self")){
				cerService.newCertificate(cer, CertType.SELFSIGNED_CA);
			}
			else cerService.newCertificate(cer, CertType.CA);
		}
		else cerService.newCertificate(cer, CertType.REGULAR);
	}
	
	// Povlacenje sertifikata
	@RequestMapping(value = "/{id}/revoke",
			method = RequestMethod.POST)
	public ResponseEntity<CertificateData> revoke(int id){
		// TODO: Implement
		return null;
	}
	
	// Provera da li je sertifikat povucen (po uzoru na OCSP protokol)
	@RequestMapping(value = "/{id}/revoke",
			method = RequestMethod.GET)
	public ResponseEntity<String> getRevocationStatus(int id){
		// TODO: Implement
		return null;
	}
}
