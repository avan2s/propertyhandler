package org.andy;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andy on 05.03.2017.
 */
public class App {

    public static void main(String[] args) {


        int appMin = PropertyHandler.getInstance().getAppMin();

        // Properties for warrantTarget's startDate
        int startDateFromWarrantTargetMinDaysInFuture = -60;
        int startDateFromWarrantTargetMaxDaysInFuture = 10;

        // build warrantTarget's startdate in past, present or future
        int daysToAdd = generateRandomNumber(startDateFromWarrantTargetMinDaysInFuture, startDateFromWarrantTargetMaxDaysInFuture);
        Calendar startDateFromWarrantTarget = Calendar.getInstance();
        startDateFromWarrantTarget.add(Calendar.DAY_OF_YEAR, daysToAdd);

        // build warrantTarget's enddate from startDate
        int warrantTargetMinObservableDays = 1;
        int warrantTargetMaxObservableDays = 180;
        Calendar endDateFromWarrantTarget = Calendar.getInstance();
        endDateFromWarrantTarget.setTime(startDateFromWarrantTarget.getTime());
        daysToAdd = generateRandomNumber(warrantTargetMinObservableDays, warrantTargetMaxObservableDays);
        endDateFromWarrantTarget.add(Calendar.DAY_OF_YEAR, daysToAdd);

        // build request-target's startDate
        boolean setToNullIfStartDateIsInThePast = true;
        int requestTargetMinObservableDays = 1;
        int requestTargetMaxObservableDays = 30;

        Calendar startDateFromRequestTarget = Calendar.getInstance();
        Calendar endDateFromRequestTarget = Calendar.getInstance();

        // find out the maximal allowed days to observe by the warrant-target
        long millisecondDifference = Math.abs(endDateFromWarrantTarget.getTimeInMillis() - startDateFromWarrantTarget.getTimeInMillis());
        int maxAllowedDaysToObserve = (int)TimeUnit.DAYS.convert(millisecondDifference, TimeUnit.MILLISECONDS);

        // get random days to observe, and if they don't match the warrant-target-timebounds -> adjust it
        int randomDaysToObserve = generateRandomNumber(requestTargetMinObservableDays, requestTargetMaxObservableDays);
        if (randomDaysToObserve > maxAllowedDaysToObserve) {
            randomDaysToObserve = maxAllowedDaysToObserve;
        }

        // if the warrantTarget's startDate is not after the currentDate (means warrantTarget's startDate is in present or past,..
        // and there is no need do define a startDate in this case.
        if (!startDateFromWarrantTarget.after(startDateFromRequestTarget) && setToNullIfStartDateIsInThePast) {
            // the request target's start date can be set to null (in etsi it means : "From now on")
            startDateFromRequestTarget = null;
            endDateFromRequestTarget.add(Calendar.DAY_OF_YEAR, randomDaysToObserve);
        }
        // if the warrantTarget's startDate is in the future
        else {
            // the requestTarget's start date have to be set to a valid timePeriod
            long randomDaysToObserveInMilliseconds = TimeUnit.DAYS.toMillis(randomDaysToObserve);
            long latestPossibleStartTimeInMilliSeconds = endDateFromWarrantTarget.getTimeInMillis() - randomDaysToObserveInMilliseconds;
            Calendar latestPossibleStartTime = Calendar.getInstance();
            latestPossibleStartTime.setTimeInMillis(latestPossibleStartTimeInMilliSeconds);
            Date dateBetween = generateRandomDateBetween(startDateFromWarrantTarget.getTime(), latestPossibleStartTime.getTime());
            startDateFromRequestTarget.setTime(dateBetween);
            endDateFromRequestTarget.setTime(startDateFromRequestTarget.getTime());
            endDateFromRequestTarget.add(Calendar.DAY_OF_YEAR, randomDaysToObserve);
            System.out.println(startDateFromRequestTarget.getTime());
        }

        System.out.println(endDateFromRequestTarget.getTime());

    }

    private static int generateRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static Date generateRandomDateBetween(Date lowerInclusiveBound, Date upperInclusiveBound) {
        if (lowerInclusiveBound.getTime() > upperInclusiveBound.getTime()) {
            throw new RuntimeException("Upper bound is bigger than lower Bound");
        }
        long diff = upperInclusiveBound.getTime() - lowerInclusiveBound.getTime() + 1;
        long randomTimeInMilliseconds = lowerInclusiveBound.getTime() + (long) (Math.random() * diff);
        return new Date(randomTimeInMilliseconds);
    }
}
