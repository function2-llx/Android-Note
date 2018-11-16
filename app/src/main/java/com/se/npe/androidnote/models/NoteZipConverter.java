package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.util.AsyncTaskWithResponse;
import com.se.npe.androidnote.util.Logger;
import com.se.npe.androidnote.util.ReturnValueEater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
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

        private String getUnusedDir(String dir) {
            int index;
            for (index = 0; ; index++) {
                File file = new File(dir + Integer.toString(index));
                if (!file.exists()) {
                    break;
                }
            }
            return dir + Integer.toString(index);
        }

        @Override
        protected Note doInBackground(Void... voids) {

            // unzip
            FileOperate.unzip(filePathName, INoteFileConverter.getExportDirPath());

            // read the file "data.txt"
            File file = new File(getTempDirPath() + "/data.txt");
            byte[] b = new byte[(int) file.length()];
            try (InputStream inputStream = new FileInputStream(file)) {
                int len = inputStream.read(b);
                if (len == -1) return new Note();
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }
            String[] strArray = new String(b).split(TableConfig.FileSave.LIST_SEPARATOR);

            Note note = new Note();

            // title
            note.setTitle(strArray[0]);

            // resource files (picture, sound, video)
            File tempFolder = new File(INoteFileConverter.getExportDirPath() + "/temp");
            String savingPath = getUnusedDir(INoteFileConverter.getExportDirPath() + '/' + note.getTitle() + "_unzip");
            boolean ok = tempFolder.renameTo(new File(savingPath));
            ReturnValueEater.eat(ok);

            // structure of the note
            for (int i = 1; i < strArray.length; i++) {
                if (strArray[i].charAt(0) == 'S') {
                    String[] tempArray = strArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    SoundData tempSoundData = new SoundData(savingPath + tempArray[1], tempArray[2]);
                    note.getContent().add(tempSoundData);
                } else if (strArray[i].charAt(0) == 'T') {
                    String[] tempArray = strArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    TextData tempTextData = new TextData(tempArray[1]);
                    note.getContent().add(tempTextData);
                } else if (strArray[i].charAt(0) == 'V') {
                    String[] tempArray = strArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    VideoData tempVideoData = new VideoData(savingPath + tempArray[1]);
                    note.getContent().add(tempVideoData);
                } else if (strArray[i].charAt(0) == 'P') {
                    String[] tempArray = strArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                    PictureData tempPictureData = new PictureData(savingPath + tempArray[1]);
                    note.getContent().add(tempPictureData);
                }
            }

            // time
            note.setStartTime(new Date());
            note.setModifyTime(new Date());

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

            // temp folder to place resource files (picture, sound, video)
            File tempFolder = new File(getTempDirPath());
            if (!tempFolder.exists()) {
                boolean ok = tempFolder.mkdirs();
                ReturnValueEater.eat(ok);
            }

            // copy resource files
            File srcFile;
            File desFile;
            List<IData> contentList = note.getContent();
            for (int i = 0; i < contentList.size(); i++) {
                switch (contentList.get(i).getType()) {
                    case "Pic":
                        srcFile = new File(contentList.get(i).getPath());
                        FileOperate.getSuffix(contentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                    case "Sound":
                        srcFile = new File(contentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;
                    case "Video":
                        srcFile = new File(contentList.get(i).getPath());
                        desFile = new File(getTempDirPath() + "/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath()));
                        FileOperate.copy(srcFile, desFile);
                        break;

                    default:
                        break;
                }
            }

            // structure of the note
            StringBuilder string = new StringBuilder(note.getTitle() + TableConfig.FileSave.LIST_SEPARATOR);
            for (int i = 0; i < contentList.size(); i++) {
                String newDir;
                switch (contentList.get(i).getType()) {
                    case "Pic":
                        newDir = "/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath());
                        string.append("Picture").append(TableConfig.FileSave.LINE_SEPARATOR).append(newDir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;

                    case "Sound":
                        newDir = "/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath());
                        string.append("Sound").append(TableConfig.FileSave.LINE_SEPARATOR).append(newDir).append(TableConfig.FileSave.LINE_SEPARATOR).append(contentList.get(i).getText()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;

                    case "Video":
                        newDir = "/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(contentList.get(i).getPath());
                        string.append("Video").append(TableConfig.FileSave.LINE_SEPARATOR).append(newDir).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;

                    default:
                        string.append(contentList.get(i).toString()).append(TableConfig.FileSave.LIST_SEPARATOR);
                        break;
                }
            }
            byte[] bs = string.toString().getBytes();

            // write structure of the note into "data.txt"
            File file = new File(getTempDirPath() + "/data.txt");
            try {
                // delete original file
                if (file.exists()) {
                    ReturnValueEater.eat(file.delete());
                }
                // create file
                ReturnValueEater.eat(file.createNewFile());
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }
            try (OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(bs);
            } catch (IOException e) {
                Logger.log(EXCEPTION_TAG, e);
            }

            // zip
            String zipFileName = INoteFileConverter.getExportDirPath() + File.separator + fileName + ".note";
            FileOperate.zip(getTempDirPath(), zipFileName);

            // delete temp folder
            ReturnValueEater.eat(tempFolder.delete());

            return zipFileName;
        }
    }

    @NonNull
    static String getTempDirPath() {
        return INoteFileConverter.getExportDirPath() + "/temp";
    }
}