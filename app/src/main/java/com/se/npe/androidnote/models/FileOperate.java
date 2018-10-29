package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.util.Logger;

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

    @NonNull
    public static String getSuffix(@NonNull String path) {
        String[] stringList = path.split("\\.");
        if (stringList.length == 0)
            throw new NullPointerException("There is no suffix");
        return stringList[stringList.length - 1];
    }

    /**
     * @Title: copy
     * @Description: 使用Stream拷贝文件
     * @param: @param source
     * @param: @param dest
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
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Logger.logError("Delete", "删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Logger.logInfo("DeleteFile", "删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Logger.logError("DeleteFile", "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Logger.logError("DeleteFile", "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(@NonNull String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Logger.logError("DeleteDir", "删除目录失败：" + dir + "不存在！");
            return false;
        }

        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length && flag; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
            }
        }
        if (!flag) {
            Logger.logError("DeleteDir", "删除目录失败！");
            return false;
        }

        // 删除当前目录
        if (dirFile.delete()) {
            Logger.logInfo("DeleteDir", "删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

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

    private static void zipFileOrDirectory(ZipOutputStream out, @NonNull File fileOrDirectory, @NonNull String curPath) {
        //从文件中读取字节的输入流
        if (fileOrDirectory.isFile()) {
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
                Logger.log("Zip", e);
            }
        } else if (fileOrDirectory.isDirectory()) {
            // 压缩目录
            File[] entries = fileOrDirectory.listFiles();
            for (int i = 0; i < entries.length; i++) {
                // 递归压缩，更新curPaths
                zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void unzip(String zipFileName, String outputDirectory) {
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Enumeration e = zipFile.entries();
            File dest = new File(outputDirectory);
            dest.mkdirs();
            while (e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // 创建目录
                    File f = new File(outputDirectory + File.separator + zipEntry.getName());
                    f.mkdirs();
                } else {
                    int index = entryName.lastIndexOf("\\");
                    if (index != -1) {
                        File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
                        df.mkdirs();
                    }
                    index = entryName.lastIndexOf("/");
                    if (index != -1) {
                        File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
                        df.mkdirs();
                    }
                    try (InputStream in = zipFile.getInputStream(zipEntry);
                         OutputStream out = new FileOutputStream(outputDirectory + File.separator + zipEntry.getName())) {
                        int c;
                        byte[] by = new byte[1024];
                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    } catch (IOException ex) {
                        Logger.log("Unzip", ex);
                    }
                }
            }
        } catch (IOException e) {
            Logger.log("Unzip", e);
        }
    }

    private FileOperate() {
        // A private constructor to hide the implicit public one
    }
}
