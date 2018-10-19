package com.se.npe.androidnote.models;


import android.content.Context;
import android.support.annotation.NonNull;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.util.Logger;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Convert between note and pdf
 *
 * @author weijd
 */
public class NotePdfConverter implements INoteFileConverter {

    private Context context;
    private File file;

    public NotePdfConverter(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void importNoteFromFile(Note note, String filePathName) {
        throw new UnsupportedOperationException("Import note from pdf is not supported.");
    }

    @Override
    public void exportNoteToFile(Note note, String fileName) {
        // create pdf document
        Document document = new Document();
        try {
            file = INoteFileConverter.createFileToExport(context, fileName + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(file));
        } catch (Exception e) {
            Logger.log("ITextPdf", e);
        }
        document.open();

//        Image image = Image.getInstance(directoryPath + "/" + "example.jpg");  // Change image's name and extension.
//
//        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//        image.scalePercent(scaler);
//        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

        try {
            document.add(new Paragraph("test"));
        } catch (Exception e) {
            Logger.log("ITextPdf", e);
        }
        document.close();
    }
}
