package edu.brown.cs.student.main.models.markov.model;

import com.squareup.moshi.Json;
import edu.brown.cs.student.main.models.exceptions.FormatterFailureException;
import edu.brown.cs.student.main.models.exceptions.InvalidDistributionException;
import edu.brown.cs.student.main.models.formatters.EmissionFormatter;
import edu.brown.cs.student.main.server.RandomGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The MarkovModel class, which takes in a start distribution of hidden states. This model can
 * generate sequences of emissions from these hidden states, but may be updated in the future to
 * support more complex tasks (such as finding the most likely sequence of states, or the most
 * likely outcome using the forward/backward algorithm).
 */
public class MarkovModel {

  private final Set<HiddenState> states;
  private final HashMap<HiddenState, Double> startDistribution;

  /**
   * The constructor for the MarkovModel class, which takes in a start distribution and validates
   * it.
   *
   * @param startDistribution - the start distribution for this model.
   * @throws InvalidDistributionException if the start distribution is not a valid probability
   *     distribution.
   */
  public MarkovModel(@Json(name = "startdist") HashMap<HiddenState, Double> startDistribution)
      throws InvalidDistributionException {
    if (startDistribution == null) {
      throw new InvalidDistributionException(
          "Start distribution was missing when generating a new MarkovModel.", new HashMap());
    }
    this.states = startDistribution.keySet();
    this.startDistribution = startDistribution;
    RandomGenerator.validateDistribution(HiddenState.class, startDistribution);
    this.checkStateDistributions();
  }

  /**
   * This method verifies that all states in the listed start distribution include all other states
   * in their transition distribution, for guaranteed success during random generation. Also ensures
   * that all potential states have valid transition and emission distributions.
   *
   * @throws InvalidDistributionException if a state has a transition distribution with the wrong
   *     keys or either of its distributions are invalid.
   */
  private void checkStateDistributions() throws InvalidDistributionException {
    for (HiddenState state : this.states) {
      if (!this.states.equals(state.potentialStates())) {
        throw new InvalidDistributionException(
            "Hidden state "
                + state
                + " had a state distribution that "
                + "contained foreign states or did not contain all states relevant to the start distribution.",
            this.startDistribution);
      }
      state.checkDistributions();
    }
  }

  /**
   * The critical functionality of the MarkovModel in early stages of the project. This method takes
   * in a length and a formatter, and returns the result of the formatter formatting a list of
   * emissions of the length inputted.
   *
   * @param len - the length of the sequence to generate.
   * @param formatter - the strategy for formatting the emissions.
   * @return the formatted emissions.
   * @param <T> the object type that the formatter returns.
   * @throws InvalidDistributionException if random generation cannot generate a number; most likely
   *     the result of a probability distribution not summing to 1. This condition will most often
   *     be pre-checked.
   * @throws FormatterFailureException if the formatter is unable to format the sequence of
   *     emissions.
   */
  public <T> T generateFormattedEmissions(int len, EmissionFormatter<T> formatter)
      throws InvalidDistributionException, FormatterFailureException {
    return formatter.formatEmissions(this.generateRandomSequence(len));
  }

  /**
   * This method generates a random list of Emissions based on the hidden states in the model. It
   * selects a start state, then emits/transitions between states len times.
   *
   * @param len - the length of the sequence of Emissions to be generated.
   * @return the generated list of Emissions.
   * @throws InvalidDistributionException if random generation cannot generate a number; most likely
   *     the result of a probability distribution not summing to 1. This condition will most often
   *     be pre-checked.
   */
  private List<Emission> generateRandomSequence(int len) throws InvalidDistributionException {
    HiddenState currState =
        RandomGenerator.generateRandomFromDistribution(HiddenState.class, this.startDistribution);
    ArrayList<Emission> sequence = new ArrayList<>();
    for (int i = 0; i < len; i++) {
      sequence.add(currState.emit());
      currState = currState.transition();
    }
    return sequence;
  }

  /**
   * A method for getting data about the model for testing.
   *
   * @return the number of states in the model.
   */
  public int getNumberOfStates() {
    return this.states.size();
  }

  /**
   * This method overrides the normal equals method, ensuring that two MarkovModels are equal if
   * their states and start distribution are the same.
   *
   * @param o - the object to check for equality.
   * @return whether o was equal to this instance or not.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    MarkovModel that = (MarkovModel) o;
    return Objects.equals(this.states, that.states)
        && Objects.equals(this.startDistribution, that.startDistribution);
  }

  /**
   * Overrides the toString method for MarkovModels in order for its representation to be
   * informative about its states and distributions.
   *
   * @return the MarkovModel as a string.
   */
  @Override
  public String toString() {
    return "MarkovModel{" + "states=" + states + ", startDistribution=" + startDistribution + '}';
  }
}
