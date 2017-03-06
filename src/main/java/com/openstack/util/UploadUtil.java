package com.openstack.util;

import static com.google.common.io.ByteSource.wrap;
import static org.jclouds.io.Payloads.newByteSourcePayload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.jclouds.ContextBuilder;
import org.jclouds.io.Payload;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.PutOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;

@Component
public class UploadUtil {
	public static final String CONTAINER_NAME = "jclouds-example";
	public static final String OBJECT_NAME = "jclouds-example.txt";

	private SwiftApi swiftApi;
	private ObjectApi objectApi;
	@Value("${objSize}")
	private int objSize;

	public void uploadObjectFromString() throws InterruptedException {

		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
		swiftApi = ContextBuilder.newBuilder("openstack-swift").endpoint("http://192.168.1.23:5000/v2.0")
				.credentials("admin:admin", "secret").modules(modules).buildApi(SwiftApi.class);
		ContainerApi containerApi = swiftApi.getContainerApi("RegionOne");

		if (containerApi.get(CONTAINER_NAME) == null) {
			CreateContainerOptions options = CreateContainerOptions.Builder
					.metadata(ImmutableMap.of("key1", "value1", "key2", "value2"));
			containerApi.create(CONTAINER_NAME, options);
		}
		System.out.println("Upload Object From String");

		objectApi = swiftApi.getObjectApi("RegionOne", CONTAINER_NAME);
		Payload payload = newByteSourcePayload(wrap("Hello World".getBytes()));
		String[] nameArray = OBJECT_NAME.split(Pattern.quote("."));
		System.out.println(nameArray.length);
		for (int i = 1; i <= 20; i++){
		String ObjectName = nameArray[0]+i+"."+nameArray[1];	
		objectApi.put(ObjectName, payload, PutOptions.Builder.metadata(ImmutableMap.of("key1", "value1")));
		System.out.println("Uploaded File Names" + ObjectName);
		}
		
	}

	public void curdOperation() throws IOException {
		System.out.println("List Containers");
		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
		swiftApi = ContextBuilder.newBuilder("openstack-swift").endpoint("http://192.168.1.23:5000/v2.0")
				.credentials("admin:admin", "secret").modules(modules).buildApi(SwiftApi.class);
		ContainerApi containerApi = swiftApi.getContainerApi("RegionOne");
		objectApi = swiftApi.getObjectApi("RegionOne", CONTAINER_NAME);
		Set<Container> containers = containerApi.list().toSet();

		for (Container container : containers) {
			System.out.println("  " + container.getObjectCount());
		}

		System.out.println("List of Objects");
		ObjectList listOfObjs = objectApi.list();
		System.out.println("Objects Available in Container :::::" + listOfObjs.size());
		List<SwiftObject> sortedList = new ArrayList<SwiftObject>(listOfObjs);
		Collections.sort(sortedList, new Comparator<SwiftObject>() {
			public int compare(SwiftObject m1, SwiftObject m2) {
				return m1.getLastModified()
						.compareTo(m2.getLastModified());
			}
		});
		System.out.println("No.of Items should be Deleted ::::" + objSize);
		for (int i = 0; i < (sortedList.size() - objSize); i++) {
			System.out.println("Deleted File Names:::"+sortedList.get(i).getName());
			objectApi.delete(sortedList.get(i).getName());
		}
		swiftApi.getContainerApi("RegionOne").deleteIfEmpty(CONTAINER_NAME);
		
		Closeables.close(swiftApi, true);
	}

}