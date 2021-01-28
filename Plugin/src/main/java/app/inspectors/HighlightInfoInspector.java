package app.inspectors;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;

import java.util.List;

public class HighlightInfoInspector {

    public static HighlightInfoInspectorResults inspectHighlightList(List<HighlightInfo> list) {
        int warningCount = 0;
        int errorCount = 0;
        for (HighlightInfo info : list) {
            if (info.getSeverity() == HighlightSeverity.WARNING) {
                warningCount++;
            }
            if (info.getSeverity() == HighlightSeverity.ERROR) {
                errorCount++;
            }
        }
        return new HighlightInfoInspectorResults(warningCount, errorCount);
    }

    public static class HighlightInfoInspectorResults {

        public final int warningCount;
        public final int errorCount;
        public final long createdAt;

        public HighlightInfoInspectorResults(int warningCount, int errorCount) {
            createdAt = System.currentTimeMillis();
            this.warningCount = warningCount;
            this.errorCount = errorCount;
        }

        @Override
        public String toString() {
            return "(Warnings:" + warningCount + ";Errors:" + errorCount + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HighlightInfoInspectorResults that = (HighlightInfoInspectorResults) o;
            return warningCount == that.warningCount &&
                    errorCount == that.errorCount;
        }
    }
}