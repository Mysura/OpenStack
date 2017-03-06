package com.openstack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openstack.util.UploadUtil;

@RestController
public class UploadController {
	
	@Autowired
	UploadUtil uploadUtil;
	
	@RequestMapping(value="/upload")
	public void uploadFiles() throws Exception{
		uploadUtil.uploadObjectFromString();
	}
	
	@RequestMapping(value="/delete")
	public void deleteFiles() throws Exception{
		uploadUtil.curdOperation();
	}

}
