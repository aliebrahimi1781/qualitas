package com.google.code.qualitas.engines.ode.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.qualitas.engines.api.core.Entry;
import com.google.code.qualitas.engines.ode.core.OdeBundle;

public class OdeBundleTest {

	private OdeBundle odeArchive;

	@Before
	public void setUp() throws IOException {
		odeArchive = new OdeBundle();
		byte[] zippedArchive = FileUtils.readFileToByteArray(new File("src/test/resources/XhGPWWhile.zip"));
		odeArchive.setBundle(zippedArchive);
	}

	@After
	public void tearDown() {
		try {
            odeArchive.cleanUp();
        } catch (IOException e) {
        }
	}

	@Test
	public void testGetEntry() throws IOException {
		String entryName = "testRequest.soap";
		Entry entry = odeArchive.getEntry(entryName);
		Assert.assertEquals(entryName, entry.getName());
		Assert.assertNotNull(entry.getContent());
	}

	@Test
	public void testSetBundle() throws IOException {
		byte[] zippedArchive = FileUtils.readFileToByteArray(new File("src/test/resources/XhGPWWhile.zip"));
		odeArchive.setBundle(zippedArchive);
	}

	@Test
	public void testBuildArchive() throws IOException {
		byte[] zippedArchive = FileUtils.readFileToByteArray(new File("src/test/resources/XhGPWWhile.zip"));
		byte[] builtArchive = odeArchive.buildBundle();
		Assert.assertNotNull(builtArchive);
		Assert.assertEquals(zippedArchive.length, builtArchive.length);
	}

	@Test
	public void testAddEntry() throws IOException {
		String name = "test.qwq";
		odeArchive.addEntry(new Entry(name, "content".getBytes()));
		String tempDirName = odeArchive.getDirTempPath();
		File tempDir = new File(tempDirName);
		String files[] = tempDir.list();
		int found = Arrays.binarySearch(files, name);
		Assert.assertTrue(found >= 0);
	}

	@Test
	public void testGetOdeDescriptor() throws IOException {
		Entry entry = odeArchive.getOdeDescriptor();
		Assert.assertNotNull(entry);
		Assert.assertEquals("deploy.xml", entry.getName());
	}

	@Test
	public void testRemoveEntry() throws IOException {
		String name = "test.qwq";
		odeArchive.addEntry(new Entry(name, "content".getBytes()));
		String tempDirName = odeArchive.getDirTempPath();
		File tempDir = new File(tempDirName);
		String files[] = tempDir.list();
		int found = Arrays.binarySearch(files, name);
		Assert.assertTrue(found >= 0);
		// now remove entry
		odeArchive.removeEntry(name);
		// test if removed
		files = tempDir.list();
		found = Arrays.binarySearch(files, name);
		Assert.assertTrue(found < 0);
	}

	@Test
	public void testRenameEntry() throws IOException {
		String name = "test.qwq";
		String newName = "test2.qwq";
		odeArchive.addEntry(new Entry(name, "content".getBytes()));
		String tempDirName = odeArchive.getDirTempPath();
		File tempDir = new File(tempDirName);
		String files[] = tempDir.list();
		int found = Arrays.binarySearch(files, name);
		Assert.assertTrue(found >= 0);
		// now rename entry
		odeArchive.renameEntry(name, newName);
		// test if name removed
		files = tempDir.list();
		Arrays.sort(files);
		found = Arrays.binarySearch(files, name);
		Assert.assertTrue(found < 0);
		// test if newName exists
		found = Arrays.binarySearch(files, newName);
		Assert.assertTrue(found >= 0);
	}

	@Test
	public void testCleanUp() throws IOException {
		String tempDirName = odeArchive.getDirTempPath();
		File tempDir = new File(tempDirName);
		Assert.assertTrue(tempDir.exists());
		odeArchive.cleanUp();
		Assert.assertTrue(!tempDir.exists());
	}

	@Test
	public void testSetMainProcessQName() {
		QName mainProcessName = new QName("mainProcessName");
		odeArchive.setMainProcessQName(mainProcessName);
		Assert.assertEquals(mainProcessName, odeArchive.getMainProcessQName());
	}

	@Test
	public void testGetMainProcessName() {
		QName mainProcessName = new QName("mainProcessName");
		odeArchive.setMainProcessQName(mainProcessName);
		Assert.assertEquals(mainProcessName, odeArchive.getMainProcessQName());
	}

	@Test
	public void testGetQualitasConfiguration() throws IOException {
		Entry configuration = odeArchive.getQualitasConfiguration();
		Assert.assertNotNull(configuration);
		Assert.assertEquals("qualitas.xml", configuration.getName());
	}

	@Test
	public void testGetMainProcessDefinition() throws IOException {
		QName mainProcessName = new QName("XhGPWWhile");
		odeArchive.setMainProcessQName(mainProcessName);
		Entry mainProcessEntry = odeArchive.getMainProcessDefinition();
		Assert.assertEquals(mainProcessName + ".bpel", mainProcessEntry.getName());
	}

	@Test
	public void testGetProcessDefinition() throws IOException {
		String mainProcessName = "XhGPWWhile";
		Entry mainProcessEntry = odeArchive.getProcessDefinition(mainProcessName);
		Assert.assertEquals(mainProcessName + ".bpel", mainProcessEntry.getName());
	}

	@Test
	public void testGetWSDL() throws IOException {
		String mainProcessName = "XhGPWWhile";
		Entry mainProcessWSDLEntry = odeArchive.getWSDL(mainProcessName);
		Assert.assertEquals(mainProcessName + ".wsdl", mainProcessWSDLEntry.getName());
	}

}
