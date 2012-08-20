package de.skubware.opentraining.basic;

public enum ActivationLevel {
	LOW(1), MEDIUM(3), ENORMOUS(5);

	private final int level;
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 5;

	/**
	 * Constructor for this Enum
	 * 
	 * @param level
	 *            The level, must be between {@value #MIN_LEVEL} and
	 *            {@value #MAX_LEVEL}
	 */
	ActivationLevel(int level) {
		// check argument
		if (!(MIN_LEVEL <= level && level <= ActivationLevel.MAX_LEVEL)) {
			throw new IllegalArgumentException("Not valid level: " + level);
		}
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * Gets an ActivationLevel by the int value of it's level.
	 * 
	 * @param level The level of the ActivationLevel
	 * 
	 * @return The corresponding ActivationLevel
	 * 
	 * @throws IllegalArgumentException  if there is no such ActivationLevel
	 */
	public static ActivationLevel getByLevel(int level) {
		for (ActivationLevel a : ActivationLevel.values()) {
			if (a.level == level) {
				return a;
			}
		}

		throw new IllegalArgumentException("An ActivationLevel with the level "
				+ level + " does not exist");
	}

}