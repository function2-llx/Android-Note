package com.se.npe.androidnote.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.util.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Note entity
 * I've thought about making it an interface, such as INote
 * But I now think this may not bring much convenience and make Note an concrete class
 *
 * @author MashPlant
 */

public class Note {
    private static final String LOG_TAG = Note.class.getSimpleName();

    public static class PreviewData {
        // using public field just for convenience
        public @NonNull
        String title;
        public @NonNull
        String text;

        public @NonNull
        String groupName;

        public @NonNull
        String picturePath;
        public @NonNull
        Date startTime;
        public @NonNull
        Date modifyTime;

        public PreviewData(@NonNull String title, @NonNull String text, @NonNull String groupName, @NonNull String picturePath, @NonNull Date startTime, @NonNull Date modifyTime) {
            this.title = title;
            this.text = text;
            this.groupName = groupName;
            this.picturePath = picturePath;
            this.startTime = startTime;
            this.modifyTime = modifyTime;
        }
    }

    private String title;
    private List<IData> content;
    private Date startTime = new Date(0);
    private Date modifyTime = new Date(0);
    private List<String> tag;
    private int indexDB = -1;
    private String groupName = "";

    public Note() {
        this.title = "this is tile for " + indexDB;
        this.content = new ArrayList<>();
        this.tag = new ArrayList<>();
    }

    public Note(String title, List<IData> content) {
        this.title = title;
        this.content = content;
        this.tag = new ArrayList<>();
    }

    public Note(String title, List<IData> content, List<String> tag) {
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

    public Note(String title, List<IData> content, int index, String timeStart, String timeModify, List<String> tag, String groupName) {
        this.indexDB = index;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.startTime.setTime(Long.parseLong(timeStart));
        this.modifyTime.setTime(Long.parseLong(timeModify));
        this.groupName = groupName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public List<IData> getContent() {
        return content;
    }

    public void setContent(List<IData> content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date time) {
        startTime = time;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date time) {
        modifyTime = time;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return indexDB;
    }

    public void setIndex(int index) {
        indexDB = index;
    }

    public PreviewData getPreview() {
        String text = null;
        String picPath = null;
        List<IData> content = getContent();
        for (int i = 0; i < content.size(); i++) {
            if (picPath == null && content.get(i).toString().charAt(0) == 'P') {
                picPath = content.get(i).toString().split(TableConfig.FileSave.LINE_SEPARATOR)[1];
            } else if (text == null && content.get(i).toString().charAt(0) == 'T') {
                text = content.get(i).toString().split(TableConfig.FileSave.LINE_SEPARATOR)[1];
            }
        }
        if (text == null) text = "无预览文字";
        if (picPath == null) picPath = "";
        return new Note.PreviewData(title, text, groupName, picPath, startTime, modifyTime);
    }

    public void loadFromFile(String fileName) {

        //文件夹生成

        File savepath = new File(TableConfig.SAVE_PATH);
        if (!savepath.exists()) {
            savepath.mkdir();
        }
        File notesave = new File(TableConfig.SAVE_PATH + "/NoteSave");
        if (!notesave.exists()) {
            notesave.mkdirs();
        }
        File tempfloder = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder");
        if (!tempfloder.exists()) {
            tempfloder.mkdirs();
        }

        //文件解压缩
        FileOperate.unzip(fileName, TableConfig.SAVE_PATH + "/NoteSave/TempFloder");

        //文件解压测试
        Log.d("debug0001", "TestFileUnzip");
        File fa[] = tempfloder.listFiles();
        for (File fs : fa) {
            if (fs.isDirectory()) {
                Log.d("debug0001", fs.getPath() + "目录");
            } else {
                Log.d("debug0001", fs.getPath() + "文件");
            }
        }

        //Note标题解析

        File file = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/data.txt");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return;
        }

        byte b[] = new byte[(int) file.length()];
        try {
            int len = inputStream.read(b);
            if (len == -1) return;
        } catch (Exception e) {
            Logger.log(LOG_TAG, e);
        }

        String tempcontent = new String(b);
        String[] StrArray = tempcontent.split(TableConfig.FileSave.LIST_SEPARATOR);

        title = StrArray[0];

        //Note资源文件转移

        tempfloder.renameTo(new File(TableConfig.SAVE_PATH + "/NoteSave/" + title + "_unzip"));

        //转移测试

        Log.d("debug0001", "TestFileMove");
        File unzip = new File(TableConfig.SAVE_PATH + "/NoteSave/" + title + "_unzip");
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

        content = new ArrayList<IData>();

        for (int i = 1; i < StrArray.length; i++) {
            Log.d("debug0001", StrArray[i]);
            if (StrArray[i].charAt(0) == 'S') {
                String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                SoundData tempSoundData = new SoundData(tempArray[1], tempArray[2]);
                content.add(tempSoundData);
            } else if (StrArray[i].charAt(0) == 'T') {
                String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                TextData tempTextData = new TextData(tempArray[1]);
                content.add(tempTextData);
            } else if (StrArray[i].charAt(0) == 'V') {
                String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                VideoData tempVideoData = new VideoData(tempArray[1]);
                content.add(tempVideoData);
            } else if (StrArray[i].charAt(0) == 'P') {
                String[] tempArray = StrArray[i].split(TableConfig.FileSave.LINE_SEPARATOR);
                PictureData tempPictureData = new PictureData(tempArray[1]);
                content.add(tempPictureData);
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }
    }


    /**
     *
     * @param fileName name of file to be saved
     * @return  absolute path of the saved file
     */
    public String saveToFile(String fileName) {

        //文件夹生成

        File savepath = new File(TableConfig.SAVE_PATH);
        if (!savepath.exists()) {
            savepath.mkdir();
        }
        File notesave = new File(TableConfig.SAVE_PATH + "/NoteSave");
        if (!notesave.exists()) {
            notesave.mkdirs();
        }
        File tempfloder = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder");
        if (!tempfloder.exists()) {
            tempfloder.mkdirs();
        }

        //Note资源文件拷贝

        File srcFile;
        File desFile;
        List<IData> ContentList = getContent();

        for (int i = 0; i < ContentList.size(); i++) {
            switch (ContentList.get(i).getType()) {
                case "Pic":
                    srcFile = new File(ContentList.get(i).getPath());
                    FileOperate.getSuffix(ContentList.get(i).getPath());
                    desFile = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                    FileOperate.copy(srcFile, desFile);
                    break;
                case "Sound":
                    srcFile = new File(ContentList.get(i).getPath());
                    desFile = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                    FileOperate.copy(srcFile, desFile);
                    break;
                case "Video":
                    srcFile = new File(ContentList.get(i).getPath());
                    desFile = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath()));
                    FileOperate.copy(srcFile, desFile);
                    break;
            }
        }

        //Note结构文件

        File file = new File(TableConfig.SAVE_PATH + "/NoteSave/TempFloder/data.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Logger.log(LOG_TAG, e);
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Logger.log(LOG_TAG, e);
            return "fuck";
        }
        StringBuilder string = new StringBuilder(getTitle() + TableConfig.FileSave.LIST_SEPARATOR);

        for (int i = 0; i < ContentList.size(); i++) {
            switch (ContentList.get(i).getType()) {
                case "Pic": {
                    String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Picdata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                    string.append("Picture").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir);
                    break;
                }
                case "Sound": {
                    String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Sounddata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                    string.append("Sound").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir).append(TableConfig.FileSave.LINE_SEPARATOR).append(ContentList.get(i).getText());
                    break;
                }
                case "Video": {
                    String newdir = TableConfig.SAVE_PATH + "/NoteSave/" + getTitle() + "_unzip/Videodata" + Integer.toString(i) + "." + FileOperate.getSuffix(ContentList.get(i).getPath());
                    string.append("Video").append(TableConfig.FileSave.LINE_SEPARATOR).append(newdir);
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
            Logger.log(LOG_TAG, e);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            Logger.log(LOG_TAG, e);
        }

        //文件压缩
        String zipFileName = TableConfig.SAVE_PATH + "/NoteSave/" + fileName + ".zip";
        FileOperate.zip(TableConfig.SAVE_PATH + "/NoteSave/TempFloder", TableConfig.SAVE_PATH + "/NoteSave/" + fileName + ".zip");


        //文件夹删除
        FileOperate.delete(TableConfig.SAVE_PATH + "/NoteSave/TempFloder");

        //文件存储测试
        Log.d("debug0001", "TestFileSave");
        File fa[] = notesave.listFiles();
        for (File fs : fa) {
            if (fs.isDirectory()) {
                Log.d("debug0001", fs.getPath() + "目录");
            } else {
                Log.d("debug0001", fs.getPath() + "文件");
            }
        }

        return zipFileName;
    }

    @Override
    public boolean equals(Object o) {
        // Identify note by its DBindex
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return indexDB == note.indexDB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexDB);
    }

    @Override
    public String toString() {
        String string = "Title:" + getTitle() + "\nContents:";
        List<IData> templist = getContent();
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        for (int i = 0; i < templist.size(); i++) {
            sb.append('\n');
            sb.append(templist.get(i).toString());
        }
        return sb.toString();
    }
}
