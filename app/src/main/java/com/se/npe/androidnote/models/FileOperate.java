package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileOperate {

    // no constructor
    private FileOperate() {
    }

    @NonNull
    public static String getSuffix(@NonNull String path) {
        String[] stringList = path.split("\\.");
        if (stringList.length == 0)
            throw new NullPointerException("There is no suffix");
        return stringList[stringList.length - 1];
    }

    /**
     * copy file using stream
     *
     * @param source source file
     * @param dest   destination file
     */
    public static void copy(File source, File dest) {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Logger.log("CopyFile", e);
        }
    }

    /**
     * zip file
     *
     * @param src  source file/directory path
     * @param dest destination zip path
     */
    public static void zip(String src, String dest) {
        //提供了一个数据项压缩成一个ZIP归档输出流
        File outFile = new File(dest);//源文件或者目录
        File fileOrDirectory = new File(src);//压缩文件路径
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile))) {
            zipFileOrDirectory(out, fileOrDirectory, "");
        } catch (IOException e) {
            Logger.log("Zip", e);
        }
    }

    private static void zipFileOrDirectory(@NonNull ZipOutputStream out, @NonNull File fileOrDirectory, @NonNull String curPath) {
        if (fileOrDirectory.isFile())
            zipFile(out, fileOrDirectory, curPath);
        else if (fileOrDirectory.isDirectory())
            zipDirectory(out, fileOrDirectory, curPath);
    }

    private static void zipFile(@NonNull ZipOutputStream out, @NonNull File fileOrDirectory, @NonNull String curPath) {
        // 压缩文件
        byte[] buffer = new byte[4096];
        int bytesRead;
        try (FileInputStream in = new FileInputStream(fileOrDirectory)) {
            //实例代表一个条目内的ZIP归档
            ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
            //条目的信息写入底层流
            out.putNextEntry(entry);
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.closeEntry();
        } catch (IOException e) {
            Logger.log("ZipFile", e);
        }
    }

    private static void zipDirectory(@NonNull ZipOutputStream out, @NonNull File directory, @NonNull String curPath) {
        // 压缩目录
        File[] entries = directory.listFiles();
        for (int i = 0; i < entries.length; i++) {
            // 递归压缩，更新curPaths
            zipFileOrDirectory(out, entries[i], curPath + directory.getName() + File.separator);
        }
    }

    /**
     * unzip file
     *
     * @param zipFileName     the path of zip file
     * @param outputDirectory the path of output directory
     */
    public static void unzip(String zipFileName, String outputDirectory) {
        File dest = new File(outputDirectory);
        ReturnValueEater.eat(dest.mkdirs());
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                if (zipEntry.isDirectory())
                    unzipDirectory(zipEntry, outputDirectory);
                else
                    unzipFile(zipFile, zipEntry, outputDirectory);
            }
        } catch (IOException e) {
            Logger.log("Unzip", e);
        }
    }

    private static void unzipDirectory(@NonNull ZipEntry zipEntry, String outputDirectory) {
        // 创建目录
        File f = new File(outputDirectory + File.separator + zipEntry.getName());
        ReturnValueEater.eat(f.mkdirs());
    }

    private static void unzipFile(ZipFile zipFile, @NonNull ZipEntry zipEntry, String outputDirectory) {
        int lastFileSeparatorIndex = zipEntry.getName().lastIndexOf(File.separator);
        if (lastFileSeparatorIndex != -1) {
            File df = new File(outputDirectory + File.separator + zipEntry.getName().substring(0, lastFileSeparatorIndex));
            ReturnValueEater.eat(df.mkdirs());
        }
        try (InputStream in = zipFile.getInputStream(zipEntry);
             OutputStream out = new FileOutputStream(outputDirectory + File.separator + zipEntry.getName())) {
            int c;
            byte[] by = new byte[1024];
            while ((c = in.read(by)) != -1) {
                out.write(by, 0, c);
            }
            out.flush();
        } catch (IOException e) {
            Logger.log("UnzipFile", e);
        }
    }
}
