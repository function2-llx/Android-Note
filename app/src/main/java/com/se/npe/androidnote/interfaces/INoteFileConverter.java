package com.se.npe.androidnote.interfaces;

import android.support.annotation.NonNull;

import com.se.npe.androidnote.models.Note;
import com.se.npe.androidnote.models.TableConfig;
import com.se.npe.androidnote.util.AsyncTaskWithResponse;
import com.se.npe.androidnote.util.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Convert between note and file
 *
 * @author weijd
 */
public interface INoteFileConverter {
    /**
     * Convert note to file
     *
     * @param delegate     Override processFinish() to response when process finish
     * @param filePathName The path of the file (not directory)
     */
    void importNoteFromFile(AsyncTaskWithResponse.AsyncResponse<Note> delegate, String filePathName);

    /**
     * Convert file to note
     *
     * @param note     note in RAM
     * @param fileName The name (not path) of the file
     */
    void exportNoteToFile(Note note, String fileName);

    /**
     * Create file to export
     * File not existed -> create an empty file and its parent directory
     * File existed -> delete original file and create an empty file
     *
     * @param fileName The name (not path) of the file
     * @return File to export
     */
    static File createFileToExport(String fileName) {
        String exportDirPath = getExportDirPath();
        File exportDir = new File(exportDirPath);
        // create dir
        exportDir.mkdirs();

        String exportFilePath = getExportFilePath(fileName);
        File exportFile = new File(exportFilePath);
        try {
            // delete original file
            if (exportFile.exists() && !exportFile.delete()) {
                throw new IOException("delete fails");
            }
            // create file
            if (!exportFile.createNewFile()) {
                throw new IOException("createNewFile fails");
            }
        } catch (IOException e) {
            Logger.log("CreateFile", e);
        }

        return exportFile;
    }

    @NonNull
    static String getExportDirPath() {
        return TableConfig.SAVE_PATH + "/Export";
    }

    @NonNull
    static String getExportFilePath(String fileName) {
        return getExportDirPath() + '/' + fileName;
    }
}