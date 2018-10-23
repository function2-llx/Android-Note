package com.se.npe.androidnote.models;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

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
            Logger.log("ItextPdf", e);
        }
        // write pdf document
        List<IData> noteContent = note.getContent();
        for (IData data : noteContent) {
            try {
                if (data instanceof TextData) {
                    document.add(new Paragraph(((TextData) data).getText()));
                } else if (data instanceof PictureData) {
                    Image image = Image.getInstance(((PictureData) data).getPicturePath());
                    document.add(image);
                } else if (data instanceof SoundData) {
                    document.add(new Paragraph(((SoundData) data).getText()));
                } else if (data instanceof VideoData) {
                    // retrieve the first bitmap of the video
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(((VideoData) data).getVideoPath());
                    Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    // convert bitmap to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    document.add(Image.getInstance(image));
                }
            } catch (Exception e) {
                Logger.log("ITextPdf", e);
            }
        }

        document.close();
    }
}
