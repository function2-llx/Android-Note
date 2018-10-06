package com.se.npe.androidnote.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioUtil {
    private static final int audioSource = MediaRecorder.AudioSource.MIC;
    //录音的采样频率
    private static final int audioRate = 16000;
    //录音的声道，单声道
    private static final int audioChannel = AudioFormat.CHANNEL_IN_MONO;
    //量化的深度
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //缓存的大小
    private static final int bufferSize = AudioRecord.getMinBufferSize(audioRate, audioChannel, audioFormat);

    private static final long byteRate = 16 * audioRate / 8;

    public static class AudioRecordThread extends Thread {
        private AudioRecord recorder;
        private String pcmPath;

        public AudioRecordThread(String pcmPath) {
            this.recorder = new AudioRecord(audioSource, audioRate, audioChannel, audioFormat, bufferSize);
            this.pcmPath = pcmPath;
            recorder.startRecording();
        }

        public void stopRecording() {
            recorder.stop();
        }

        @Override
        public void run() {
            byte[] noteArray = new byte[bufferSize];
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pcmPath))) {
                while (true) {
                    int recordSize = recorder.read(noteArray, 0, bufferSize);
                    if (recordSize > 0) {
                        os.write(noteArray);
                        os.flush();
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pcmToWav(String pcmPath, String wavPath,
                                long startMillis, long endMillis) throws IOException {
        try (FileInputStream in = new FileInputStream(pcmPath);
             FileOutputStream out = new FileOutputStream(wavPath)) {
            long totalAudioLen = in.getChannel().size();
            long startByte = (startMillis * byteRate) / 1000;
            long endByte = Math.min(totalAudioLen, (endMillis * byteRate) / 1000);
            long wantAudioLen = endByte - startByte;
            byte[] data = new byte[(int) wantAudioLen];
            in.skip(startByte);
            in.read(data);
            pcmToFile(out, data, audioRate, 1, 16);
        }
    }

    private static void pcmToFile(OutputStream os, byte[] data, int srate, int channel, int format) throws IOException {
        byte[] header = new byte[44];

        long totalDataLen = data.length + 36;
        long bitrate = srate * channel * format;

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = (byte) format;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channel;
        header[23] = 0;
        header[24] = (byte) (srate & 0xff);
        header[25] = (byte) ((srate >> 8) & 0xff);
        header[26] = (byte) ((srate >> 16) & 0xff);
        header[27] = (byte) ((srate >> 24) & 0xff);
        header[28] = (byte) ((bitrate / 8) & 0xff);
        header[29] = (byte) (((bitrate / 8) >> 8) & 0xff);
        header[30] = (byte) (((bitrate / 8) >> 16) & 0xff);
        header[31] = (byte) (((bitrate / 8) >> 24) & 0xff);
        header[32] = (byte) ((channel * format) / 8);
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (data.length & 0xff);
        header[41] = (byte) ((data.length >> 8) & 0xff);
        header[42] = (byte) ((data.length >> 16) & 0xff);
        header[43] = (byte) ((data.length >> 24) & 0xff);

        os.write(header, 0, 44);
        os.write(data);
        os.close();
    }
}