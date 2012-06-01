package com.googlecode.qualitas.engines.ode.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.qualitas.engines.api.configuration.ProcessType;
import com.googlecode.qualitas.engines.api.core.AbstractBundle;
import com.googlecode.qualitas.engines.api.core.Entry;
import com.googlecode.qualitas.utils.zip.ZipUtils;

/**
 * The Class OdeProcessBundle.
 */
public class OdeBundle extends AbstractBundle {

    /** The Constant LOG. */
    private static final Log LOG = LogFactory.getLog(OdeBundle.class);

    /** The Constant ROOT_TMP_DIR. */
    private static final String ROOT_TMP_DIR = System.getProperty("java.io.tmpdir");

    /** The Constant ODE_DESCRIPTOR_NAME. */
    private static final String ODE_DESCRIPTOR_NAME = "deploy.xml";

    /** The Constant WS_BPEL_EXTENSION. */
    private static final String WS_BPEL_EXTENSION = ".bpel";

    /** The Constant WSDL_EXTENSION. */
    private static final String WSDL_EXTENSION = ".wsdl";

    /** The temporary process bundle name. */
    private String tmpProcessBundleName;

    /** The temporary dir. */
    private File tmpDir;

    /** The temporary process bundle. */
    private File tmpProcessBundle;

    /* (non-Javadoc)
     * @see com.google.code.qualitas.engines.api.core.ProcessBundle#getProcessType()
     */
    @Override
    public ProcessType getProcessType() {
        return ProcessType.WS_BPEL_2_0_APACHE_ODE;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#setBundle(byte[])
     */
    @Override
    public void setBundle(byte[] bundle) throws IOException {
        tmpProcessBundleName = UUID.randomUUID().toString();
        createTempDir();
        createPlaceHolderFiles();
        createBundle(bundle);
        extractArchive();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#buildBundle()
     */
    @Override
    public byte[] buildBundle() throws IOException {
        try {
            ZipUtils.createArchive(tmpProcessBundle, tmpDir);
        } catch (IOException e) {
            String msg = "Could not create archive " + tmpProcessBundle.getAbsolutePath()
                    + " from " + tmpDir.getAbsolutePath() + " directory";
            LOG.debug(msg, e);
            throw e;
        }

        try {
            return FileUtils.readFileToByteArray(tmpProcessBundle);
        } catch (IOException e) {
            String msg = "Could not read archive " + tmpProcessBundle.getAbsolutePath();
            LOG.debug(msg, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#getEntry(java
     * .lang.String)
     */
    @Override
    public Entry getEntry(String name) throws IOException {
        File file = new File(tmpDir, name);
        if (!file.exists()) {
            return null;
        }
        try {
            byte[] content = FileUtils.readFileToByteArray(file);
            return new Entry(name, content);
        } catch (IOException e) {
            String msg = "Could not read file " + file.getAbsolutePath();
            LOG.error(msg, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#addEntry(com.
     * google.code.qualitas.engines.api.core.Entry)
     */
    @Override
    public void addEntry(Entry entry) throws IOException {
        File file = new File(tmpDir, entry.getName());
        try {
            FileUtils.writeByteArrayToFile(file, entry.getContent());
        } catch (IOException e) {
            String msg = "Could not add a new entry to archive";
            LOG.error(msg, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#removeEntry(java
     * .lang.String)
     */
    @Override
    public void removeEntry(String entryName) throws IOException {
        File file = new File(tmpDir, entryName);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.qualitas.engines.api.core.ProcessBundle#renameEntry(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void renameEntry(String oldName, String newName) throws IOException {
        File oldFile = new File(tmpDir, oldName);
        File newFile = new File(tmpDir, newName);
        try {
            FileUtils.copyFile(oldFile, newFile);
        } catch (IOException e) {
            String msg = "Could not rename " + oldFile.getAbsolutePath() + " to "
                    + newFile.getAbsolutePath();
            LOG.debug(msg, e);
            throw e;
        }
        FileUtils.forceDelete(oldFile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.qualitas.engines.api.core.ProcessBundle#cleanUp()
     */
    @Override
    public void cleanUp() throws IOException {
        if (tmpDir != null) {
            try {
                FileUtils.forceDelete(tmpDir);
            } catch (IOException e) {
                if (tmpDir.exists()) {
                    tmpDir.deleteOnExit();
                } else {
                    throw e;
                }
    
            }
        }
        if (tmpProcessBundle != null) {
            try {
                FileUtils.forceDelete(tmpProcessBundle);
            } catch (IOException e) {
                if (tmpProcessBundle.exists()) {
                    tmpProcessBundle.deleteOnExit();
                } else {
                    throw e;
                }
            }
        }
    }
    
    /**
     * File to entry.
     *
     * @param file the file
     * @return the entry
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static Entry fileToEntry(File file) throws IOException {
        Entry entry = new Entry();
        entry.setName(file.getName());
        byte[] content = FileUtils.readFileToByteArray(file);
        entry.setContent(content);
        return entry;
    }
    
    /**
     * Gets the entries.
     *
     * @param pattern the pattern
     * @return the entries
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<Entry> getEntries(String pattern) throws IOException {
        List<Entry> entries = new ArrayList<Entry>();
        
        Iterator<File> it = FileUtils.iterateFiles(tmpDir, new WildcardFileFilter(pattern), TrueFileFilter.TRUE);
        
        while(it.hasNext()) {
            Entry entry = fileToEntry(it.next());
            entries.add(entry);
        }
        
        return entries;
    }

    /**
     * Gets the ode descriptor.
     *
     * @return the ode descriptor
     * @throws IOException the IO exception
     */
    public Entry getOdeDescriptor() throws IOException {
        return getEntry(ODE_DESCRIPTOR_NAME);
    }

    /**
     * Gets the process definition.
     *
     * @param processName the process name
     * @return the process definition
     * @throws IOException the IO exception
     */
    public Entry getProcessDefinition(String processName) throws IOException {
        return getEntry(processName + WS_BPEL_EXTENSION);
    }

    /**
     * Gets the wsdl.
     *
     * @param processName the process name
     * @return the WSDL
     * @throws IOException the IO exception
     */
    public Entry getWSDL(String processName) throws IOException {
        return getEntry(processName + WSDL_EXTENSION);
    }

    /**
     * Gets the dir temp path.
     *
     * @return the dir temp path
     */
    public String getDirTempPath() {
        return tmpDir.getAbsolutePath();
    }

    /**
     * Creates the temp dir.
     * @throws IOException 
     */
    private void createTempDir() throws IOException {
        tmpDir = new File(ROOT_TMP_DIR, tmpProcessBundleName);
        boolean directoryCreated = tmpDir.mkdir();
        if (!directoryCreated) {
            throw new IOException("Could not create temporary directory " + tmpDir.getCanonicalPath());
        }
        tmpDir.deleteOnExit();
    }

    /**
     * Creates the place holder files.
     */
    private void createPlaceHolderFiles() {
        tmpProcessBundle = new File(ROOT_TMP_DIR, tmpProcessBundleName + ".zip");
        tmpProcessBundle.deleteOnExit();
    }

    /**
     * Creates the bundle.
     *
     * @param bundle the bundle
     * @throws IOException the IO exception
     */
    private void createBundle(byte[] bundle) throws IOException {
        FileUtils.writeByteArrayToFile(tmpProcessBundle, bundle);
    }

    /**
     * Extracts the archive.
     *
     * @throws IOException the IO exception
     */
    private void extractArchive() throws IOException {
        try {
            ZipUtils.extractArchive(tmpProcessBundle, tmpDir);
        } catch (IOException e) {
            String msg = "Could not extract archive " + tmpProcessBundle.getAbsolutePath() + " to "
                    + tmpDir.getAbsolutePath() + " directory";
            LOG.debug(msg, e);
            throw e;
        }
    }

}
