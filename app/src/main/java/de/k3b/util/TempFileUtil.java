/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of CameraFolder https://github.com/k3b/VirtualCamera/
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.util;

import java.io.File;

public class TempFileUtil {
    public static final String TEMP_FILE_SUFFIX = "_llcrop.jpg";

    // temp file will be deleted after 2 hours
    private static final long TEMP_FILE_LIFETIME_IN_MILLI_SECS = 1000 * 60 * 60 * 2;

    /**
     * #11: remove unused temporary crops from send/get_content after some time.
     * */
    public static void removeOldTempFiles(File dir, long nowInMilliSecs) {
        if (dir != null) {
            for (File candidate : dir.listFiles()) {
                if (candidate.isFile() && shouldDeleteTempFile(candidate, nowInMilliSecs)) {
                    candidate.delete();
                }
            }
        }
    }

    private static boolean shouldDeleteTempFile(File candidate, long nowInMilliSecs) {
        if (candidate == null) return false;
        return shouldDeleteTempFile(candidate.getName(), candidate.lastModified(), nowInMilliSecs);
        //
    }

    // static package to allow unit testing
    static boolean shouldDeleteTempFile(String fileName, long lastModifiedInMilliSecs, long nowInMilliSecs) {
        if ((fileName == null) || (lastModifiedInMilliSecs == 0)) return false;

        final long candidateAgeInMilliSecs = nowInMilliSecs - lastModifiedInMilliSecs;
        return (candidateAgeInMilliSecs > TEMP_FILE_LIFETIME_IN_MILLI_SECS)
                && fileName.endsWith(TEMP_FILE_SUFFIX);
    }

}
