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
    private String directoryMixIn = "directory" + File.separator;
    private File fileInDirectory;
    private String fileInDirectoryMixIn = directoryMixIn + fileMixIn;
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
        FileOperate.zip(file.getAbsolutePath(), zipFile.getAbsolutePath());
        assertTrue(zipFile.exists());
        assertTrue(file.exists());
        FileOperate.zip(directory.getAbsolutePath(), zipDirectory.getAbsolutePath());
        assertTrue(zipDirectory.exists());
        assertTrue(directory.exists());
    }

    @Test
    public void unzip() throws IOException {
        zip();
        FileOperate.unzip(zipDirectory.getAbsolutePath(), directory.getAbsolutePath());
        assertTrue(directory.exists());
        assertTrue(fileInDirectory.exists());
        File anotherDirectory = new File(DataExample.getExamplePath(directoryMixIn + directoryMixIn));
        File anotherFileInDirectory = new File(DataExample.getExamplePath(directoryMixIn + directoryMixIn + fileMixIn));
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