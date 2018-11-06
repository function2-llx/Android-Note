package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.util.AsyncTaskWithResponse;
import com.se.npe.androidnote.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteZipConverter implements INoteFileConverter {

    private static final String EXCEPTION_TAG = "zip";

    @Override
    public void importNoteFromFile(AsyncTaskWithResponse.AsyncResponse<Note> delegate, String filePathName) {
        new ImportNoteFromZipTask(delegate, filePathName).execute();
    }

    @Override
    public void exportNoteToFile(AsyncTaskWithResponse.AsyncResponse<String> delegate, Note note, String fileName) {
        new ExportNoteToZipTask(delegate, note, fileName).execute();
    }

    static class ImportNoteFromZipTask extends AsyncTaskWithResponse<Void, Void, Note> {
        private String filePathName;

        ImportNoteFromZipTask(AsyncResponse<Note> delegate, String filePathName) {
            super(delegate);
            this.filePathName = filePathName;
        }

        @Override
        protected Note doInBackground(Void... voids) {

            // unzip
            FileOperate.unzip(filePathName, INoteFileConverter.getExportDirPath());

            //文件解压测试
//            Log.d("debug0001", "TestFileUnzip");
//            File fa[] = tempfloder.listFiles();
//            for (File fs : fa) {
//                if (fs.isDirectory()) {
//                    Log.d("debug0001", fs.getPath() + "目录");
//                } else {
//                    Log.d("debug0001", fs.getPath() + "文件");
//                }
//            }


            //Note标题解析

            File file = new File(getTempDirPath() + "/data.txt");
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Logger.log(EXCEPTION_TAG, e);
                return new Note();
            }

            byte b[] = new byte[(int) file.length()];
            try {
                int len = inputStream.read(b);
                if (len == -1) return new Note();
            } catch (Exception e) {
                Logger.log(EXCEPTION_TAG, e);
            }

            Note note = new Note();

            String tempcontent = new String(b);
            String[] StrArray = tempcontent.split(TableConfig.FileSave.LIST_SEPARATOR);
            note.setTitle(StrArray[0]);

            //Note资源文件转移

            File tempFloder = new File(INoteFileConverter.getExportDirPath() + "/tempFloder");
            tempFloder.renameTo(new File(INoteFileConverter.getExportDirPath() + note.getTitle() + "_unzip"));

            //转移测试

            Log.d("debug0001", "TestFileMove");
            File unzip = new File(INoteFileConverter.getExportDirPath() + note.getTitle() + "_unzip");
            File fax[] = unzip.listFiles();
            for (File fs : fax) {
                if (fs.isDirectory()) {
                    Log.d("debug0001", fs.getPath() + "目录");
                } else {
                    Log.d("debug0001", fs.getPath() + "文件");
                }
            }

            //Note结构解析
            Log.d("debug0001", "TestNoteStruct");

            for (int i = 1; i < StrArray.length; i++) {
                Log.d("debug0001", StrArray[i]);
                if (StrArray[i].charAt(0) == 'S') {
                    String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    SoundData tempSoundData = new SoundData(INoteFileConverter.getExportDirPath() + tempArray[1],tempArray[2]);
                    note.getContent().add(tempSoundData);
                } else if (StrArray[i].charAt(0) == 'T') {
                    String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    TextData tempTextData = new TextData(tempArray[1]);
                    note.getContent().add(tempTextData);
                } else if (StrArray[i].charAt(0) == 'V') {
                    String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    VideoData tempVideoData = new VideoData(INoteFileConverter.getExportDirPath() + tempArray[1]);
                    note.getContent().add(tempVideoData);
                } else if (StrArray[i].charAt(0) == 'P') {
                    String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    PictureData tempPictureData = new PictureData(INoteFileConverter.getExportDirPath() + tempArray[1]);
                    note.getContent().add(tempPictureData);
                }
            }

            try {
                inputStream.close();
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }
            return note;
        }
    }

    static class ExportNoteToZipTask extends AsyncTaskWithResponse<Void, Void, String> {
        private Note note;
        private String fileName;

        ExportNoteToZipTask(AsyncResponse<String> delegate, Note note, String fileName) {
            super(delegate);
            this.note = note;
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(Void... voids) {

            //文件夹生成

            File tempFloder = new File(getTempDirPath());
            if (!tempFloder.exists()) {
                tempFloder.mkdirs();
            }

            //Note资源文件拷贝

            File srcFile;
            File desFile;
            List<IData> ContentList = note.getContent();

            for (int i = 0; i < ContentList.size(); i++) {
                switch (ContentList.get(i).getType()) {
                    case "Pic":
                        srcFile = new File(ContentList.get(i).getPath());
                        FileOperate.getSuffix(ContentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                    case "Sound":
                        srcFile = new File(ContentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                    case "Video":
                        srcFile = new File(ContentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                }
            }

            //Note结构文件

            File file = new File(getTempDirPath() + "/data.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Logger.log(EXCEPTION_TAG, e);
                }
            }
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                Logger.log(EXCEPTION_TAG, e);
                return "fuck";
            }
            StringBuilder string = new StringBuilder(note.getTitle() + TableConfig.FileSave.LIST_SEPARATOR);

            for (int i = 0; i < ContentList.size(); i++) {
                switch (ContentList.get(i).getType()) {
                    case "Pic": {
                        String newdir = "/TempFloder/" + note.getTitle() + "_unzip/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Picture").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Sound": {
                        String newdir = "/TempFloder/" + note.getTitle() + "_unzip/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Sound").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LINE_SEPARATOR).append(ContentList.get(i).getText()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Video": {
                        String newdir = "/TempFloder/" + note.getTitle() + "_unzip/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Video").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    default:
                        string.append(ContentList.get(i).toString()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                }
            }

            byte[] bs = string.toString().getBytes();
            try {
                outputStream.write(bs);
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }

            try {
                outputStream.close();
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }

            //文件压缩
            String zipFileName = INoteFileConverter.getExportDirPath() + "/" + fileName + ".note";
            FileOperate.zip(getTempDirPath(), zipFileName);


            //文件夹删除
            FileOperate.delete(getTempDirPath());

            //文件存储测试
            Log.d("debug0001", "TestFileSave");
            File noteSave = new File(INoteFileConverter.getExportDirPath());
            File fa[] = noteSave.listFiles();
            for (File fs : fa) {
                if (fs.isDirectory()) {
                    Log.d("debug0001", fs.getPath() + "目录");
                } else {
                    Log.d("debug0001", fs.getPath() + "文件");
                }
            }

            return zipFileName;
        }
    }

    @NonNull
    static String getTempDirPath() {
        return INoteFileConverter.getExportDirPath() + "/TempFloder";
    }
}