package com.se.npe.androidnote.sound;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VideoCliper {
    private String inPath;
    private String outPath;
    // the time is 0 at the beginning of the video, rather than the time starting from 1970.1.1
    private double startTime;
    private double endTime;

    public VideoCliper(String inPath, String outPath, double startTime, double endTime) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static double correctTimeToSyncSample(Track track, double cutHere,
                                                 boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];
            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(),
                        currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta
                    / (double) track.getTrackMetaData().getTimescale();
            currentSample++;
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    public void clip() {
        try {
            Movie movie = MovieCreator.build(inPath);

            List<Track> tracks = movie.getTracks();
            movie.setTracks(new LinkedList<Track>());
            //移除旧的通道
            boolean timeCorrected = false;

            //计算剪切时间
            for (Track track : tracks) {
                if (track.getSyncSamples() != null
                        && track.getSyncSamples().length > 0) {
                    if (timeCorrected) {
                        throw new RuntimeException(
                                "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                    }
                    startTime = correctTimeToSyncSample(track, startTime, false);
                    endTime = correctTimeToSyncSample(track, endTime, true);
                    timeCorrected = true;
                }
            }

            for (Track track : tracks) {
                long currentSample = 0;
                double currentTime = 0;
                double lastTime = 0;
                long startSample1 = -1;
                long endSample1 = -1;

                for (int i = 0; i < track.getSampleDurations().length; i++) {
                    long delta = track.getSampleDurations()[i];
                    if (currentTime > lastTime && currentTime <= startTime) {
                        startSample1 = currentSample;
                    }
                    if (currentTime > lastTime && currentTime <= endTime) {
                        endSample1 = currentSample;
                    }
                    lastTime = currentTime;
                    currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
                movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// new
            }

            //合成视频mp4
            Container out = new DefaultMp4Builder().build(movie);
            FileOutputStream fos = new FileOutputStream(new File(outPath));
            FileChannel fco = fos.getChannel();
            out.writeContainer(fco);
            fco.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
