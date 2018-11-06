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

            File file = new File(INoteFileConverter.getExportDirPath() + "/TempFloder/data.txt");
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

            File tempFloder = new File(INoteFileConverter.getExportDirPath() + "/tempFloder")
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
            List<IData> content = TableOperate.getInstance().decodeNote()


            try {
                inputStream.close();
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }
        }


        String foo() {
            File file = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/data.txt");
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Logger.log(EXCEPTION_TAG, e);
                return;
            }

            byte b[] = new byte[(int) file.length()];
            try {
                int len = inputStream.read(b);
                if (len == -1) return;
            } catch (Exception e) {
                Logger.log(EXCEPTION_TAG, e);
            }

            String tempcontent = new String(b);
            String[] StrArray = tempcontent.split(TableConfig.FileSave.LIST_SEPARATOR);
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
            // temp dir for zip
            File tempDir = new File(getTempDirPath());
            if (tempDir.exists() && !tempDir.delete()) {
                Logger.logError(EXCEPTION_TAG, "delete fails");
            }
            tempDir.mkdirs();

            // the structure of note
            //
            List<IData> content = note.getContent();
            StringBuilder string = new StringBuilder(note.getTitle() + TableConfig.FileSave.LIST_SEPARATOR);

            for (int i = 0; i < content.size(); i++) {
                switch (content.get(i).getType()) {
                    case "Pic": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Picture").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Sound": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Sound").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LINE_SEPARATOR).append(ContentList.get(i).getText()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Video": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Video").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    default:
                        string.append(ContentList.get(i).toString()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                }
            }


            // copy resources (picture, sound, video)
            for (int i = 0; i < content.size(); ++i) {
                switch (content.get(i).getType()) {
                    case "Pic":
                    case "Sound":
                    case "Video":
                        File srcFile = new File(content.get(i).getPath());
                        File desFile = new File(getTempDirPath() + "data" + i + "." + FileOperate.getSuffix(content.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                    default:
                        break;
                }
            }

            File dataFile = new File(getTempDirPath() + "/data.txt");
            try {
                // delete original file
                if (dataFile.exists() && !dataFile.delete()) {
                    throw new IOException("delete fails");
                }
                // create file
                if (!dataFile.createNewFile()) {
                    throw new IOException("createNewFile fails");
                }
            } catch (IOException e) {
                Logger.log("CreateFile", e);
            }

            try (OutputStream outputStream = new FileOutputStream(dataFile)) {

            }


            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                Logger.log(EXCEPTION_TAG, e);
                return "fuck";
            }
            StringBuilder string = new StringBuilder(getTitle() + TableConfig.FileSave.LIST_SEPARATOR);

            for (int i = 0; i < ContentList.size(); i++) {
                switch (ContentList.get(i).getType()) {
                    case "Pic": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Picture").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Sound": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                        string.append("Sound").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LINE_SEPARATOR).append(ContentList.get(i).getText()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                    }
                    case "Video": {
                        String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
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


            try (OutputStream outputStream = new FileOutputStream(file)) {

            }

            // zip
            String zipFilePathName = INoteFileConverter.getExportFilePath(fileName + ".zip");
            FileOperate.zip(getTempDirPath(), zipFilePathName);

            // delete temp dir
            FileOperate.delete(getTempDirPath());

            return zipFilePathName;
        }
    }

    @NonNull
    static String getTempDirPath() {
        return INoteFileConverter.getExportDirPath() + "/temp";
    }
}
