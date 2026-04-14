package org.windowing.windowingproject.util;

import org.windowing.windowingproject.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    private Window window;

    public Window getWindow() {
        return window;
    }

    public List<Segment> loadSegments(String path) throws Exception {
        List<Segment> segments = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));

        String[] firstLine = br.readLine().trim().split("\\s+");
        window = new Window(
                Double.parseDouble(firstLine[0]),
                Double.parseDouble(firstLine[1]),
                Double.parseDouble(firstLine[2]),
                Double.parseDouble(firstLine[3])
        );

        String line;
        while ((line = br.readLine()) != null) {
            String[] p = line.trim().split("\\s+");
            double x1 = Double.parseDouble(p[0]);
            double y1 = Double.parseDouble(p[1]);
            double x2 = Double.parseDouble(p[2]);
            double y2 = Double.parseDouble(p[3]);

            segments.add(new Segment(
                    new Point2D(x1, y1),
                    new Point2D(x2, y2)
            ));
        }

        br.close();
        return segments;
    }
}
