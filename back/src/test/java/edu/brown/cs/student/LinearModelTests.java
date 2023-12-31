package edu.brown.cs.student;

import edu.brown.cs.student.main.handlers.GenerateLinearPlan;
import edu.brown.cs.student.main.models.exceptions.FormatterFailureException;
import edu.brown.cs.student.main.models.exceptions.InvalidDistributionException;
import edu.brown.cs.student.main.models.exceptions.InvalidScheduleException;
import edu.brown.cs.student.main.models.exceptions.NoWorkoutTypeException;
import edu.brown.cs.student.main.models.formatters.ScheduleFormatter;
import edu.brown.cs.student.main.models.formattypes.Day;
import edu.brown.cs.student.main.models.formattypes.Schedule;
import edu.brown.cs.student.main.models.formattypes.Week;
import edu.brown.cs.student.main.models.markov.model.Emission;
import edu.brown.cs.student.main.models.markov.model.MarkovModel;
import edu.brown.cs.student.main.models.markov.modelbuilding.Workout;
import edu.brown.cs.student.main.rowing.distributiongenerators.RowingWorkoutByName;
import edu.brown.cs.student.main.rowing.modelbuilders.LinearModelBuilder;
import edu.brown.cs.student.main.rowing.modelbuilders.ScheduleBuilder;
import edu.brown.cs.student.main.server.RandomGenerator;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinearModelTests {

  public static int NUM_TRIALS = 1000;

  /**
   * A method for ensuring that schedules are shaped the way they should be.
   *
   * @param schedule - the schedule to be validated
   * @param minutes - the number of minutes per week the schedule was built on
   * @return whether or not the schedule was valid (boolean)
   */
  private boolean validateSchedule(Schedule schedule, int minutes) {
    for (Week week : schedule.weeks()) {
      int minuteCount = 0;
      for (Day day : week.days()) {
        if (day.getEmissions().size() != day.getNumberOfWorkouts() && day.verifyDay()) {
          return false;
        }
        for (Emission emission : day.getEmissions()) {
          minuteCount += emission.getTime();
        }
      }
      if (week.days().size() == 7) {
        if (minuteCount > minutes + 60 || minuteCount < minutes - 60) {
          return false;
        }
      }
    }
    return true;
  }

  /** A method for testing the maximum number of minutes per week */
  @Test
  public void testLinearModelMax()
      throws InvalidDistributionException, InvalidScheduleException, IOException,
          NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            1200,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 25),
            0.2,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Assertions.assertEquals(model.getNumberOfStates(), 10);
  }

  /** A method for testing the minimum number of minutes per week */
  @Test
  public void testLinearModelMin()
      throws InvalidDistributionException, InvalidScheduleException, IOException,
          NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            120,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 25),
            0.2,
            Workout.of("30r20"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Assertions.assertEquals(model.getNumberOfStates(), 2);
  }

  /** A method for testing the minimum number of days */
  @Test
  public void testLinearModelMinVaryingStartEndDates()
      throws InvalidDistributionException, InvalidScheduleException, IOException,
          NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    for (int i = 10; i < 17; i++) {
      for (int j = 19; j < 26; j++) {
        toBuild =
            builder.minutesWithDates(
                120,
                LocalDate.of(2023, 5, i),
                LocalDate.of(2023, 5, j),
                0.2,
                Workout.of("30r20"),
                Workout.of("UT2"));
        MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
        Assertions.assertEquals(model.getNumberOfStates(), 2);
      }
    }
  }

  /** A method for validating a schedule with a big number of minutes */
  @Test
  public void testLinearMax()
      throws IOException, InvalidScheduleException, InvalidDistributionException,
          FormatterFailureException, NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            1200,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 25),
            0.2,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Schedule schedule =
        model.generateFormattedEmissions(toBuild.getLength(), new ScheduleFormatter(toBuild));
    Assertions.assertTrue(this.validateSchedule(schedule, 1200));
  }

  /** A method for validating a schedule with a small number of minutes */
  @Test
  public void testLinearMin()
      throws IOException, InvalidScheduleException, InvalidDistributionException,
          FormatterFailureException, NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            120,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 25),
            0.2,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Schedule schedule =
        model.generateFormattedEmissions(toBuild.getLength(), new ScheduleFormatter(toBuild));
    Assertions.assertTrue(this.validateSchedule(schedule, 120));
  }

  /** A method for validating a short workout plan */
  @Test
  public void testLinearShortPlan()
      throws IOException, InvalidScheduleException, InvalidDistributionException,
          FormatterFailureException, NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            600,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 4),
            0.2,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Schedule schedule =
        model.generateFormattedEmissions(toBuild.getLength(), new ScheduleFormatter(toBuild));
    Assertions.assertTrue(this.validateSchedule(schedule, 600));
  }

  /**
   * An edge case test which validates a workout plan that ends on a Sunday, knowing that it is a
   * rest day
   */
  @Test
  public void testLinearEndsOnSunday()
      throws IOException, InvalidScheduleException, InvalidDistributionException,
          FormatterFailureException, NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            600,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 13),
            0.2,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Schedule schedule =
        model.generateFormattedEmissions(toBuild.getLength(), new ScheduleFormatter(toBuild));
    Assertions.assertTrue(this.validateSchedule(schedule, 120));
  }

  /** An edge case test which validates a workout plan that ends with a high intensity workout */
  @Test
  public void testLinearEndsHigherIntensity()
      throws IOException, InvalidScheduleException, InvalidDistributionException,
          FormatterFailureException, NoWorkoutTypeException {
    ScheduleBuilder builder = new ScheduleBuilder();
    Schedule toBuild = null;
    toBuild =
        builder.minutesWithDates(
            600,
            LocalDate.of(2023, 5, 3),
            LocalDate.of(2023, 5, 13),
            0.5,
            Workout.of("2k"),
            Workout.of("UT2"));
    MarkovModel model = new LinearModelBuilder(new RowingWorkoutByName()).build(toBuild);
    Schedule schedule =
        model.generateFormattedEmissions(toBuild.getLength(), new ScheduleFormatter(toBuild));
    Assertions.assertTrue(this.validateSchedule(schedule, 120));
  }

  /** A fuzz test for validation of random generations within boundaries */
  @Test
  public void fuzzLinearGeneratorValid()
      throws InvalidDistributionException, InvalidScheduleException, NoWorkoutTypeException,
          IOException, FormatterFailureException {
    for (int i = 0; i < NUM_TRIALS; i++) {
      int mins = RandomGenerator.getRandomInt(120, 1200);
      int startMonth = RandomGenerator.getRandomInt(1, 12);
      int startDay = RandomGenerator.getRandomInt(1, 28);
      int startYear = RandomGenerator.getRandomInt(2015, 2023);
      int endMonth = RandomGenerator.getRandomInt(1, 12);
      int endDay = RandomGenerator.getRandomInt(1, 28);
      int endYear = startYear + RandomGenerator.getRandomInt(1, 3);
      double highPercent = RandomGenerator.getRandomPositiveDouble(0.0, 1.0);
      Schedule schedule =
          new GenerateLinearPlan()
              .generate(
                  mins,
                  LocalDate.of(startYear, startMonth, startDay),
                  LocalDate.of(endYear, endMonth, endDay),
                  Workout._2K,
                  Workout.UT_2,
                  highPercent);
      Assertions.assertEquals(
          schedule.weeks().get(0).days().get(0).getDate().get(),
          LocalDate.of(startYear, startMonth, startDay));
      Assertions.assertEquals(
          schedule
              .weeks()
              .get(schedule.weeks().size() - 1)
              .days()
              .get(schedule.weeks().get(schedule.weeks().size() - 1).days().size() - 1)
              .getDate()
              .get(),
          LocalDate.of(endYear, endMonth, endDay));
    }
  }
}
