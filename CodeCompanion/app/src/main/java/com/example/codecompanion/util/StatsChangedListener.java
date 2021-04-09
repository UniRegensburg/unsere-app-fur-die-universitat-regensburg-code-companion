package com.example.codecompanion.util;

/**
 * Listener class that listens for statistics changes (lines of codes, errors, warnings, time)
 */
public interface StatsChangedListener {

	void errorsChanged();

	void warningsChanged();

	void linesOfCodeChanged();

	void documentChanged();

	void connectionClosed();
}
