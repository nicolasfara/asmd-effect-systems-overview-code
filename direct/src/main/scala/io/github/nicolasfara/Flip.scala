package io.github.nicolasfara

// TODO[4]: Implement the Flip effect that simulates a coin flip.
object Flip:
  /** * Simulate a coin flip that can be "Heads" or "Tails".
    *
    * @return "Heads" or "Tails"
    */
  def flip(): String = ???

  /**
    * Simulate a coin flip that can be "Heads" or "Tails".
    * Moreover, with some probability, the coin can be "Drunk", which is a special state that we want to handle in our logic.
    * If the coin is "Drunk", we accidentally drop it, and we want to handle this case as well.
    *
    * @return "Heads" or "Tails"
    */
  def drunkFlip(): String = ???
