package com.example.codecompanion.util;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import java.util.TimerTask;

public class TimeSpentTimerTask extends TimerTask {

	private Period timePeriod;
	private PeriodFormatter formatter;

	public TimeSpentTimerTask(Period timePeriod, PeriodFormatter formatter) {
		this.timePeriod = timePeriod;
		this.formatter = formatter;
	}

	@Override
	public void run() {
		timePeriod = timePeriod.plus(new Period(1000).normalizedStandard());
		final String continousTime = formatter.print(timePeriod);
	}
}
