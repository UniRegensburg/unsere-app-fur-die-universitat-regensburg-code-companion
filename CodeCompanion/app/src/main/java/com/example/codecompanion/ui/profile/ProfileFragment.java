package com.example.codecompanion.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.R;
import com.example.codecompanion.cache.StatsCache;
import com.example.codecompanion.db.DocumentInformation;
import com.example.codecompanion.db.ProjectInformation;
import com.example.codecompanion.services.WebRTC;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.StatsChangedListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Profile fragment is displayed upon tapping the "profile" button in the bottom navigation bar
 * Displays statistics, such as lines of codes, coding time, errors and warnings
 * Updates in near-real time
 *
 * Shows statistics for the <i>current project</i> while connect
 * Shows statistics for <i>ALL projects</i> while disconnected
 *
 * For Project Information see {@link ProjectInformation}
 * For Document Information see {@link DocumentInformation}
 */
public class ProfileFragment extends Fragment implements StatsChangedListener {

    private MessageManager messageManager;
    private TextView linesOfCodeTextView;
    private TextView totalErrorsCounter;
    private TextView totalWarningsCounter;
    private TextView timeSpentView;
    private Period timePeriod;
    private PeriodFormatter formatter;
    private Timer timer;
    private boolean isTimerRunning;
    private boolean isFirstUpdate = true;
    private int linesOfCodeExceptOpenDocument;
    private static final int DEFAULT_THREAD_POOL_SIZE = 8;
    private final ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        findTextViews(root);

        createTimeFormatter();
        messageManager = MessageManager.getInstance();
        StatsCache.setStatsChangedListener(this);

        if (StatsCache.currentProject != null) {
            linesOfCodeChanged();
            setErrors();
            setWarnings();
            setTime();
        } else {
            sendRequestForCurrentProject();
            displayTotalStats();
        }

        ImageButton helpButton = root.findViewById(R.id.help_button);
        helpButton.setOnClickListener(v -> Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.viewPagerFragment));
        return root;
    }


    private void displayTotalStats() {
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            int totalErrors = 0;
            int totalWarnings = 0;
            int totalLinesOfCode = 0;
            int totalSecondsSpent = 0;
            List<ProjectInformation> allProjects = MainActivity.db.projectInformationDAO().findAll();

            for (ProjectInformation project : allProjects) {
                totalErrors += project.totalErrors;
                totalWarnings += project.totalWarnings;
                totalSecondsSpent += project.secondsSpentOnProject;
            }

            List<Integer> linesOfCodeList = MainActivity.db.documentInformationDAO().getAllLinesOfCode();
            for (Integer integer : linesOfCodeList) {
                totalLinesOfCode += integer;
            }
            showTotalStatsOnUi(totalErrors, totalWarnings, totalLinesOfCode, totalSecondsSpent, handler);
        });
    }

    private void showTotalStatsOnUi(int totalErrors, int totalWarnings, int totalLinesOfCode, int totalSecondsSpent, Handler handler) {
        Period period = Seconds.seconds(totalSecondsSpent).toPeriod();
        String timeSpent = formatter.print(period.normalizedStandard());
        handler.post(() -> {
            totalErrorsCounter.setText(String.valueOf(totalErrors));
            totalWarningsCounter.setText(String.valueOf(totalWarnings));
            linesOfCodeTextView.setText(String.valueOf(totalLinesOfCode));
            timeSpentView.setText(timeSpent);
        });
    }

    private void sendRequestForCurrentProject() {
        try {
            WebRTC.sendData("REQUEST_PROJECT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTimeFormatter() {
        formatter = new PeriodFormatterBuilder()
                .minimumPrintedDigits(2)
                .printZeroAlways()
                .appendHours()
                .appendLiteral(":")
                .appendMinutes()
                .appendLiteral(":")
                .appendSeconds()
                .toFormatter();
    }

    private void setTime() {
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            DateTime now = DateTime.now();
            timePeriod = new Period(StatsCache.projectOpenedDate, now);

            // read seconds from db and add them to the session time
            long secondsFromDb = MainActivity.db.projectInformationDAO().findSecondsSpentOnProjectById(StatsCache.currentProject.getId());
            Period dbPeriod = new Period(secondsFromDb * 1000);
            timePeriod = timePeriod.plus(dbPeriod).normalizedStandard();

            String timeToDisplay = formatter.print(timePeriod);
            handler.post(() -> timeSpentView.setText(timeToDisplay));

            initTimeUpdateTimer();
        });
    }

    private void initTimeUpdateTimer() {
        if (isTimerRunning) {
            return;
        }

        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> continuouslyUpdateTimer());
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        isTimerRunning = true;
    }

    private void continuouslyUpdateTimer() {
        timePeriod = timePeriod.plus(new Period(1000)).normalizedStandard();
        timeSpentView.setText(formatter.print(timePeriod));
    }


    private void setWarnings() {
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            int warnings = MainActivity.db.projectInformationDAO().findTotalWarningsByProjectId(StatsCache.currentProject.getId());
            warnings += StatsCache.currentProject.totalWarnings;
            String textToDisplay = String.valueOf(warnings);
            handler.post(() -> totalWarningsCounter.setText(textToDisplay));
        });
    }

    private void setErrors() {
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            int errors = MainActivity.db.projectInformationDAO().findTotalErrorsByProjectId(StatsCache.currentProject.getId());
            errors += StatsCache.currentProject.totalErrors;
            String textToDisplay = String.valueOf(errors);
            handler.post(() -> totalErrorsCounter.setText(textToDisplay));
        });
    }

    private void findTextViews(View root) {
        linesOfCodeTextView = root.findViewById(R.id.linesOfCodeTextView);
        totalErrorsCounter = root.findViewById(R.id.profileTotalErrorsCounter);
        totalWarningsCounter = root.findViewById(R.id.profileTotalWarningsCounter);
        timeSpentView = root.findViewById(R.id.profileTimeSpentView);
    }

    private void setCurrentLinesOfCode() {
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<Integer> integerList = MainActivity.db.documentInformationDAO().getLinesOfCodeByProjectId(
                    StatsCache.currentProject.getId());
            int finalLinesOfCode = 0;
            for (Integer integer : integerList) {
                finalLinesOfCode += integer;
            }
            String textToDisplay = String.valueOf(finalLinesOfCode);
            handler.post(() -> linesOfCodeTextView.setText(textToDisplay));
        });
    }

    @Override
    public void onDestroy() {
        if (isTimerRunning) {
            cancelTimer();
        }

        StatsCache.updateCurrentProject();
        super.onDestroy();
    }

    @Override
    public void errorsChanged() {
        getActivity().runOnUiThread(() -> {
            String errorText = String.valueOf(StatsCache.currentProject.totalErrors);
            totalErrorsCounter.setText(errorText);
        });
    }

    @Override
    public void warningsChanged() {
        getActivity().runOnUiThread(() -> {
            String warningText = String.valueOf(StatsCache.currentProject.totalWarnings);
            totalWarningsCounter.setText(warningText);
        });
    }

    @Override
    public void linesOfCodeChanged() {
        if (isFirstUpdate) {
            isFirstUpdate = false;
            documentChanged();
            return;
        }

        int totalLines = linesOfCodeExceptOpenDocument + StatsCache.currentDocument.getLinesOfCode();
        String textToDisplay = String.valueOf(totalLines);
        getActivity().runOnUiThread(() -> linesOfCodeTextView.setText(textToDisplay));
    }

    @Override
    public void documentChanged() {
        executorService.execute(() -> {
            List<Integer> linesOfCode = MainActivity.db.documentInformationDAO()
                    .getLinesOfCodeByProjectIdExceptFromDocument(StatsCache.currentProject.getId(), StatsCache.currentDocument.getId());

            linesOfCodeExceptOpenDocument = 0;
            for (Integer integer : linesOfCode) {
                linesOfCodeExceptOpenDocument += integer;
            }
            linesOfCodeChanged();
        });
    }

    @Override
    public void connectionClosed() {
        if (isTimerRunning) {
            cancelTimer();
        }

        getActivity().runOnUiThread(() -> {
            totalErrorsCounter.setText(R.string.placeholder_stat);
            totalWarningsCounter.setText(R.string.placeholder_stat);
            linesOfCodeTextView.setText(R.string.placeholder_stat);
            timeSpentView.setText(R.string.placeholder_stat);
        });
    }

    private void cancelTimer() {
        if (isTimerRunning) {
            timer.cancel();
            timer.purge();
            timer = null;
            isTimerRunning = false;
        }
    }
}
