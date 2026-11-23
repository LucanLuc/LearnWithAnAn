
package frontend;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.VocabularyService;
import Backend.WordLearningStats;
import Backend.ReviewHistory;
import model.LearningState;
import model.ReviewRecord;
import model.Word;

public class analyticsPage {

    private final VocabularyService service;

    private final Color BLUE = new Color(52, 152, 219);
    private final Color DARK = new Color(44, 62, 80);
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color GREEN = new Color(22, 160, 133);
    private final Color ORANGE = new Color(230, 126, 34);

    public analyticsPage(VocabularyService service) {

        this.service = service;

        JFrame frame = new JFrame("Learning Analytics");
        frame.setSize(1050, 850);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(30, 35, 30, 35));

        Map<Word, WordLearningStats> stats =
                service.getLearningStats();

        List<Word> words = service.getAllWords();

        ReviewHistory history = service.getReviewHistory();

        List<ReviewRecord> records = history.getResults();

        int totalWords = words.size();
        int totalReviews = records.size();
        int correct = history.getCorrectReviews();
        int incorrect = history.getIncorrectReviews();

        double accuracy = totalReviews == 0
                ? 0
                : (double) correct / totalReviews * 100;

        int dueNow = calculateDueWords(words);

        // HEADER
        JLabel heading = new JLabel("Your Learning Analytics");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Understand your progress and improve your learning."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(heading);
        main.add(Box.createVerticalStrut(8));
        main.add(subtitle);
        main.add(Box.createVerticalStrut(25));

        // SUMMARY CARDS
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(BACKGROUND);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.add(createCard("Vocabulary", totalWords + " words", BLUE));
        cards.add(createCard("Accuracy",
                String.format("%.1f%%", accuracy), GREEN));
        cards.add(createCard("Total Reviews", String.valueOf(totalReviews), BLUE));
        cards.add(createCard("Due for Review", String.valueOf(dueNow), ORANGE));

        main.add(cards);
        main.add(Box.createVerticalStrut(25));

        // LEARNING STATES
        main.add(createSectionTitle("Learning State Distribution"));

        Map<LearningState, Integer> stateCounts =
                new EnumMap<>(LearningState.class);

        for (LearningState state : LearningState.values()) {
            stateCounts.put(state, 0);
        }

        for (Word word : words) {

            WordLearningStats wordStats = stats.get(word);

            LearningState state = wordStats == null
                    ? LearningState.NEW
                    : wordStats.getLearningState();

            stateCounts.put(state, stateCounts.get(state) + 1);
        }

        JPanel statePanel = createPanel();

        for (LearningState state : LearningState.values()) {

            int count = stateCounts.get(state);

            statePanel.add(createProgressRow(
                    state.name(),
                    count,
                    Math.max(1, totalWords)
            ));
        }

        main.add(statePanel);
        main.add(Box.createVerticalStrut(25));

        // WEEKLY ACTIVITY
        main.add(createSectionTitle("Weekly Review Activity"));

        Map<LocalDate, Integer> dailyReviews =
                calculateWeeklyActivity(records);

        JPanel activityPanel = createPanel();
        activityPanel.setLayout(new BorderLayout());

        activityPanel.add(
                new WeeklyChart(dailyReviews),
                BorderLayout.CENTER
        );

        activityPanel.setPreferredSize(new Dimension(800, 190));
        activityPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 190)
        );

        main.add(activityPanel);
        main.add(Box.createVerticalStrut(25));

        // LONG-TERM RETENTION
        main.add(createSectionTitle("Long-Term Retention"));

        int[] delayedStats = new int[2];

        double retention = calculateDelayedRetention(
                records,
                delayedStats
        );

        JPanel retentionPanel = createPanel();

        String retentionText = delayedStats[0] == 0
                ? "Not enough delayed-review data yet."
                : String.format(
                        "Delayed-review accuracy: %.1f%% (%d reviews)",
                        retention,
                        delayedStats[0]
                );

        retentionPanel.add(createLabel(retentionText, 15, DARK));

        main.add(retentionPanel);
        main.add(Box.createVerticalStrut(25));

        // DIFFICULT WORDS
        main.add(createSectionTitle("Words That Need More Practice"));

        JPanel difficultPanel = createPanel();
        difficultPanel.setLayout(new BoxLayout(
                difficultPanel,
                BoxLayout.Y_AXIS
        ));

        List<Word> hardestWords = new ArrayList<>(words);

        hardestWords.sort((a, b) -> {

            WordLearningStats statsA = stats.get(a);
            WordLearningStats statsB = stats.get(b);

            double accuracyA = statsA == null
                    ? 0 : statsA.getAccuracy();

            double accuracyB = statsB == null
                    ? 0 : statsB.getAccuracy();

            return Double.compare(accuracyA, accuracyB);
        });

        int displayed = 0;

        for (Word word : hardestWords) {

            if (displayed >= 5) {
                break;
            }

            WordLearningStats wordStats = stats.get(word);

            if (wordStats == null ||
                    wordStats.getTotalReviews() == 0) {
                continue;
            }

            String description = String.format(
                    "%s     Accuracy: %.1f%%     Reviews: %d     Incorrect: %d",
                    word.getWord(),
                    wordStats.getAccuracy(),
                    wordStats.getTotalReviews(),
                    wordStats.getIncorrectReviews()
            );

            difficultPanel.add(createLabel(
                    description,
                    14,
                    DARK
            ));

            difficultPanel.add(Box.createVerticalStrut(10));

            displayed++;
        }

        if (displayed == 0) {
            difficultPanel.add(createLabel(
                    "No review data available yet.",
                    14,
                    Color.GRAY
            ));
        }

        main.add(difficultPanel);
        main.add(Box.createVerticalStrut(25));

        // CONSISTENCY
        main.add(createSectionTitle("Learning Consistency"));

        int streak = calculateCurrentStreak(records);

        JPanel streakPanel = createPanel();

        streakPanel.add(createLabel(
                "Current learning streak: " + streak + " day(s)",
                16,
                DARK
        ));

        main.add(streakPanel);
        main.add(Box.createVerticalStrut(25));

        // BACK BUTTON
        JButton back = new JButton("Back to Homepage");
        back.setFont(new Font("Arial", Font.BOLD, 14));
        back.setBackground(BLUE);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setAlignmentX(Component.LEFT_ALIGNMENT);

        back.addActionListener(e -> frame.dispose());

        main.add(back);

        JScrollPane scrollPane = new JScrollPane(main);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private JPanel createCard(
            String title,
            String value,
            Color color) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 235)
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = createLabel(title, 13, Color.GRAY);
        JLabel valueLabel = createLabel(value, 24, color);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);

        return card;
    }

    private JLabel createSectionTitle(String text) {

        JLabel label = createLabel(text, 19, DARK);

        label.setBorder(new EmptyBorder(0, 0, 12, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JLabel createLabel(
            String text,
            int size,
            Color color) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JPanel createPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 235)
                ),
                new EmptyBorder(18, 20, 18, 20)
        ));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 300)
        );

        return panel;
    }

    private JPanel createProgressRow(
            String name,
            int value,
            int max) {

        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel label = createLabel(name, 13, DARK);
        label.setPreferredSize(new Dimension(110, 25));

        JProgressBar progress = new JProgressBar(0, max);
        progress.setValue(value);
        progress.setStringPainted(true);
        progress.setString(String.valueOf(value));
        progress.setForeground(BLUE);
        progress.setBackground(new Color(235, 238, 240));

        row.add(label, BorderLayout.WEST);
        row.add(progress, BorderLayout.CENTER);

        return row;
    }

    private int calculateDueWords(List<Word> words) {

        int due = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Word word : words) {

            LocalDateTime next = word.getNextReviewAt();

            if (next == null || !next.isAfter(now)) {
                due++;
            }
        }

        return due;
    }

    private Map<LocalDate, Integer> calculateWeeklyActivity(
            List<ReviewRecord> records) {

        Map<LocalDate, Integer> activity = new TreeMap<>();

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            activity.put(today.minusDays(i), 0);
        }

        for (ReviewRecord record : records) {

            if (record.getReviewedAt() == null) {
                continue;
            }

            LocalDate date = record.getReviewedAt().toLocalDate();

            if (activity.containsKey(date)) {
                activity.put(date, activity.get(date) + 1);
            }
        }

        return activity;
    }

    private double calculateDelayedRetention(
            List<ReviewRecord> records,
            int[] result) {

        Map<Word, LocalDateTime> lastReviews = new HashMap<>();

        List<ReviewRecord> sorted = new ArrayList<>(records);

        sorted.sort(Comparator.comparing(
                ReviewRecord::getReviewedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        int samples = 0;
        int correct = 0;

        for (ReviewRecord record : sorted) {

            Word word = record.getWord();
            LocalDateTime current = record.getReviewedAt();

            if (word == null || current == null) {
                continue;
            }

            LocalDateTime previous = lastReviews.get(word);

            if (previous != null &&
                    java.time.Duration.between(previous, current).toHours() >= 24) {

                samples++;

                if (record.isCorrect()) {
                    correct++;
                }
            }

            lastReviews.put(word, current);
        }

        result[0] = samples;
        result[1] = correct;

        return samples == 0
                ? 0
                : (double) correct / samples * 100;
    }

    private int calculateCurrentStreak(List<ReviewRecord> records) {

        Set<LocalDate> dates = new HashSet<>();

        for (ReviewRecord record : records) {

            if (record.getReviewedAt() != null) {
                dates.add(record.getReviewedAt().toLocalDate());
            }
        }

        LocalDate day = LocalDate.now();

        if (!dates.contains(day)) {

            if (dates.contains(day.minusDays(1))) {
                day = day.minusDays(1);
            } else {
                return 0;
            }
        }

        int streak = 0;

        while (dates.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }

        return streak;
    }

    // Custom weekly bar chart
    private class WeeklyChart extends JPanel {

        private final Map<LocalDate, Integer> data;

        WeeklyChart(Map<LocalDate, Integer> data) {
            this.data = data;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 150));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int bottom = height - 30;
            int top = 15;
            int chartHeight = bottom - top;

            int max = 1;

            for (int value : data.values()) {
                max = Math.max(max, value);
            }

            int count = data.size();
            int slot = width / Math.max(1, count);
            int barWidth = Math.max(12, slot / 3);

            int index = 0;

            for (Map.Entry<LocalDate, Integer> entry :
                    data.entrySet()) {

                int value = entry.getValue();

                int barHeight = (int)
                        ((double) value / max * chartHeight);

                int x = index * slot + (slot - barWidth) / 2;
                int y = bottom - barHeight;

                g2.setColor(BLUE);
                g2.fillRoundRect(
                        x,
                        y,
                        barWidth,
                        barHeight,
                        8,
                        8
                );

                g2.setColor(DARK);
                g2.setFont(new Font("Arial", Font.PLAIN, 11));

                String day = entry.getKey()
                        .getDayOfWeek()
                        .toString()
                        .substring(0, 3);

                g2.drawString(
                        day,
                        x - 4,
                        height - 10
                );

                g2.drawString(
                        String.valueOf(value),
                        x,
                        Math.max(top + 10, y - 5)
                );

                index++;
            }

            g2.dispose();
        }
    }
}
