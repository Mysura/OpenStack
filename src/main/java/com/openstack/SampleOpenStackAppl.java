package com.openstack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openstack.util.UploadUtil;

@SpringBootApplication

public class SampleOpenStackAppl {
	
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleOpenStackAppl.class, args);
	}
}