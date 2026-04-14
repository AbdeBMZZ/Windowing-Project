package org.windowing.windowingproject.util;

import org.windowing.windowingproject.model.Point2D;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads segments from the course file format: first line {@code x0 x0' y0 y0'}, then lines
 * {@code x y x' y'} per segment.
 */
public class FileLoader {

    private Window window;

    /**
     * Bounding window read from the first line of the last loaded file.
     */
    public Window getWindow() {
        return window;
    }

    /**
     * @param path path to the data file
     * @return segments in file order
     */
    public List<Segment> loadSegments(String path) throws Exception {
        List<Segment> segments = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));

        String[] firstLine = br.readLine().trim().split("\\s+");
        window = new Window(
                Double.parseDouble(firstLine[0]),
                Double.parseDouble(firstLine[1]),
                Double.parseDouble(firstLine[2]),
                Double.parseDouble(firstLine[3]));

        String line;
        while ((line = br.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            String[] p = line.trim().split("\\s+");
            double x1 = Double.parseDouble(p[0]);
            double y1 = Double.parseDouble(p[1]);
            double x2 = Double.parseDouble(p[2]);
            double y2 = Double.parseDouble(p[3]);

            segments.add(new Segment(
                    new Point2D(x1, y1),
                    new Point2D(x2, y2)));
        }

        br.close();
        return segments;
    }
}
