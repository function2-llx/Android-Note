package com.se.npe.androidnote.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class FileOperateTest {

    private File file;
    private String fileMixIn = "file";
    private File directory;
    private String directoryMixIn = "directory";
    private File fileInDirectory;
    private String fileInDirectoryMixIn = directoryMixIn + File.separator + fileMixIn;
    private File zipFile;
    private String zipFileMixIn = "file.zip";
    private File zipDirectory;
    private String zipDirectoryMixIn = "directory.zip";

    @Before
    public void setUp() {
        file = DataExample.getExampleFile(fileMixIn);
        directory = DataExample.getExampleDirectory(directoryMixIn);
        fileInDirectory = DataExample.getExampleFile(fileInDirectoryMixIn);
        zipFile = new File(DataExample.getExamplePath(zipFileMixIn));
        zipDirectory = new File(DataExample.getExamplePath(zipDirectoryMixIn));
    }

    @Test
    public void getSuffix() {
        assertEquals("jpg", FileOperate.getSuffix(DataExample.getExamplePicturePath(DataExample.EXAMPLE_MIX_IN)));
        assertEquals("mp3", FileOperate.getSuffix(DataExample.getExampleSoundPath(DataExample.EXAMPLE_MIX_IN)));
        assertEquals("wav", FileOperate.getSuffix(DataExample.getExampleVideoPath(DataExample.EXAMPLE_MIX_IN)));
    }

    @Test
    public void copy() throws IOException {
        File another = new File(DataExample.getExamplePath(fileMixIn + fileMixIn));
        FileOperate.copy(file, another);
        assertTrue(another.exists());
        assertFileEquals(file, another);
    }

    @Test
    public void zip() {
        // zip file
        FileOperate.zip(file.getAbsolutePath(), zipFile.getAbsolutePath());
        assertTrue(zipFile.exists());
        assertTrue(file.exists());
        // zip directory
        FileOperate.zip(directory.getAbsolutePath(), zipDirectory.getAbsolutePath());
        assertTrue(zipDirectory.exists());
        assertTrue(directory.exists());
        assertTrue(fileInDirectory.exists());
    }

    @Test
    public void unzip() throws IOException {
        zip();
        String anotherDirectoryMixin = "directory2";
        // unzip file
        FileOperate.unzip(zipFile.getAbsolutePath(), DataExample.getExamplePath(anotherDirectoryMixin));
        File anotherFile = new File(DataExample.getExamplePath(anotherDirectoryMixin + File.separator + fileMixIn));
        assertTrue(anotherFile.exists());
        // unzip directory
        FileOperate.unzip(zipDirectory.getAbsolutePath(), DataExample.getExamplePath(anotherDirectoryMixin));
        File anotherDirectory = new File(DataExample.getExamplePath(anotherDirectoryMixin + File.separator + directoryMixIn));
        File anotherFileInDirectory = new File(DataExample.getExamplePath(anotherDirectoryMixin + File.separator + fileInDirectoryMixIn));
        assertTrue(anotherDirectory.exists());
        assertTrue(anotherFileInDirectory.exists());
        assertFileEquals(fileInDirectory, anotherFileInDirectory);
    }

    private void assertFileEquals(File file, File another) throws IOException {
        try (InputStream in = new FileInputStream(file);
             InputStream inAnother = new FileInputStream(another)) {
            final int BUFFER_SIZE = 128;
            byte[] buffer = new byte[BUFFER_SIZE];
            in.read(buffer);
            byte[] bufferAnother = new byte[BUFFER_SIZE];
            inAnother.read(bufferAnother);
            for (int i = 0; i < BUFFER_SIZE; ++i) {
                assertEquals(buffer[i], bufferAnother[i]);
            }
        }
    }
}