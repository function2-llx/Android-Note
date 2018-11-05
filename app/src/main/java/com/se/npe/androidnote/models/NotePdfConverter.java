package com.se.npe.androidnote.models;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.se.npe.androidnote.interfaces.IData;
import com.se.npe.androidnote.interfaces.INoteFileConverter;
import com.se.npe.androidnote.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Convert between note and pdf
 *
 * @author weijd
 */
public class NotePdfConverter implements INoteFileConverter {

    private Context context;
    private static final String EXCEPTION_TAG = "ITextPdf";

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
            File file = INoteFileConverter.createFileToExport(context, fileName + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(file));
        } catch (Exception e) {
            Logger.log(EXCEPTION_TAG, e);
        }
        document.open();

        // pdf document settings
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addCreator("AndroidNote");

        // line separator
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        // font
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD);
        Font keywordFont = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.ITALIC);
        Font paragraphFont = new Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL);
        Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.ITALIC);
        // font support Chinese
        try {
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titleFont = new Font(baseFont, 22.0f, Font.BOLD);
            keywordFont = new Font(baseFont, 14.0f, Font.ITALIC);
            paragraphFont = new Font(baseFont, 14.0f, Font.NORMAL);
            footerFont = new Font(baseFont, 12.0f, Font.ITALIC);
        } catch (Exception e) {
            Logger.log(EXCEPTION_TAG, e);
        }

        // write pdf document
        // title
        String noteTitle = note.getTitle();
        document.addTitle(noteTitle);
        try {
            Paragraph titleParagraph = new Paragraph(noteTitle, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);
        } catch (DocumentException e) {
            Logger.log(EXCEPTION_TAG, e);
        }
        // group
        String noteGroup = note.getGroupName();
        document.addSubject(noteGroup);
        try {
            document.add(new Paragraph("Group: " + noteGroup, keywordFont));
        } catch (DocumentException e) {
            Logger.log(EXCEPTION_TAG, e);
        }
        // tag
        List<String> noteTags = note.getTag();
        StringBuilder noteTagString = new StringBuilder("Tag:");
        for (String noteTag : noteTags) {
            document.addKeywords(noteTag);
            noteTagString.append(' ');
            noteTagString.append(noteTag);
        }
        try {
            document.add(new Paragraph(noteTagString.toString(), keywordFont));
        } catch (DocumentException e) {
            Logger.log(EXCEPTION_TAG, e);
        }
        // content
        List<IData> noteContent = note.getContent();
        for (IData data : noteContent) {
            try {
                if (data instanceof TextData) {
                    document.add(new Paragraph(data.getText(), paragraphFont));
                } else if (data instanceof PictureData) {
                    Image image = Image.getInstance(data.getPath());
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    document.add(image);
                } else if (data instanceof SoundData) {
                    document.add(new Paragraph(data.getText(), paragraphFont));
                } else if (data instanceof VideoData) {
                    // retrieve the first bitmap of the video
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(data.getPath());
                    Bitmap bitmap = retriever.getFrameAtTime(1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); // retrieve at about 1s
                    // convert bitmap to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    // add bitmap to pdf document
                    Image image = Image.getInstance(stream.toByteArray());
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    document.add(Image.getInstance(image));
                }
            } catch (Exception e) {
                Logger.log(EXCEPTION_TAG, e);
            }
        }
        // create & modify time
        try {
            document.newPage();
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph("Create time of the note: " + note.getStartTime(), footerFont));
            document.add(new Paragraph("Modify time of the note: " + note.getModifyTime(), footerFont));
            document.add(new Paragraph("Auto exported by AndroidNote.", footerFont));
        } catch (DocumentException e) {
            Logger.log(EXCEPTION_TAG, e);
        }

        document.close();
    }
}
