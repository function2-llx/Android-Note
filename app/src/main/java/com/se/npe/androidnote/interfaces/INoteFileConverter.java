package com.se.npe.androidnote.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;

import com.se.npe.androidnote.models.Note;
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
     * @param note         note in RAM
     * @param filePathName The path of the file (not directory)
     */
    void importNoteFromFile(Note note, String filePathName);

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
     * @param context  activity.getApplicationContext()
     * @param fileName The name (not path) of the file
     * @return File to export
     */
    static File createFileToExport(@NonNull Context context, String fileName) {
        String exportDirPath = getExportDirPath(context);
        File exportDir = new File(exportDirPath);
        // create dir
        exportDir.mkdirs();

        String exportFilePath = getExportFilePath(context, fileName);
        File exportFile = new File(exportFilePath);
        try {
            // delete original file
            if (exportFile.exists() && exportDir.delete()) {
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
    static String getExportDirPath(@NonNull Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + "/Export/";
    }

    @NonNull
    static String getExportFilePath(@NonNull Context context, String fileName) {
        // ".pdf" will be automatically appended by ITextPdf
        return getExportDirPath(context) + fileName;
    }
}