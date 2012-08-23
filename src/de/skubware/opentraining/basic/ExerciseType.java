/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.skubware.opentraining.basic;

import java.io.File;
import java.net.URL;
import java.util.*;


/**
 * An instance of this class represents a certain type of fitness exercise.
 * <p>
 * 
 * The class itself holds all created ExerciseTypes in a Set. If a new Exercise
 * is created, which has the same name as an Exercise that was created before,
 * the older Exercise is returned and no new instance is created.
 * 
 * <p>
 * All Instances of this class are immutable and there can't exist 2
 * ExercisTypes with an equal name.
 * 
 * <p>
 * <b>That means changing Information about an ExerciseType includes removing
 * the old exercise.</b>
 * 
 * 
 * @author Christian Skubich
 * 
 */

public final class ExerciseType implements Comparable<ExerciseType> {


	private final String name; // required

	private final String description; // optional
	private final List<File> imagePaths; // optional
	private final Map<File, String> imageLicenseMap; // optional
	private final int imageWidth; // optional
	private final int imageHeight; // optional
	private final SortedSet<SportsEquipment> requiredEquipment; // optional
	private final SortedSet<Muscle> activatedMuscles; // optional
	private final Map<Muscle, ActivationLevel> activationMap; // optional
	private final SortedSet<ExerciseTag> exerciseTag; // optional
	private final List<URL> relatedURL; // optional
	private final List<String> hints; // optional
	private final File iconPath; // optional

	private String md5; // auto-calculated

	private static final TreeSet<ExerciseType> exerciseTypes = new TreeSet<ExerciseType>(); // stores
																							// all
																							// instances

	public static class Builder {
		// Default values
		private static final String DEFAULT_DESCRIPTION = "Keine Beschreibung verfügbar";
		//private static final String DEFAULT_LICENSE_TEXT = "Keine Lizenzinformationen verfügbar";
		// Collections do not have a default value

		// Required parameters
		private final String name;

		// Optional parameters - initialized to default values
		private String description = DEFAULT_DESCRIPTION;
		private List<File> imagePaths = new ArrayList<File>(); // optional
		private Map<File, String> imageLicenseMap = new HashMap<File, String>();
		private int imageWidth = 214;
		private int imageHeight = 137;
		private SortedSet<SportsEquipment> neededTools = new TreeSet<SportsEquipment>();
		private SortedSet<Muscle> activatedMuscles = new TreeSet<Muscle>();
		private Map<Muscle, ActivationLevel> activationMap = new LinkedHashMap<Muscle, ActivationLevel>();
		private SortedSet<ExerciseTag> exerciseTag = new TreeSet<ExerciseTag>();
		private List<URL> relatedURL = new ArrayList<URL>();
		private List<String> hints = new ArrayList<String>(); // optional
		private File iconPath = new File(""); // optional

		private String md5;

		public Builder(String name) {
			// null name is NOT allowed
			if (name == null) {
				throw new NullPointerException();
			}

			this.name = name;
		}

		public Builder description(String description) {
			if (description != null)
				this.description = description;
			return this;
		}

		public Builder imagePath(List<File> imagePaths) {
			if (imagePaths.size() > 0)
				this.imagePaths = imagePaths;
			return this;
		}

		public Builder imageLicenseText(Map<File, String> imageLicenseMap) {
			if (imageLicenseMap.size() > 0)
				this.imageLicenseMap = imageLicenseMap;
			return this;
		}

		public Builder imageWidth(int imageWidth) {
			if (imageWidth > 0)
				this.imageWidth = imageWidth;
			return this;
		}

		public Builder imageHeigth(int imageHeight) {
			if (imageHeight > 0)
				this.imageHeight = imageHeight;
			return this;
		}

		public Builder neededTools(SortedSet<SportsEquipment> neededTools) {
			if (neededTools.size() > 0)
				this.neededTools = neededTools;
			return this;
		}

		public Builder activatedMuscles(SortedSet<Muscle> activatedMuscles) {
			if (activatedMuscles.size() > 0)
				this.activatedMuscles = activatedMuscles;
			return this;
		}

		public Builder activationMap(Map<Muscle, ActivationLevel> activationMap) {
			if (activationMap.size() > 0)
				this.activationMap = activationMap;
			return this;
		}

		public Builder exerciseTags(SortedSet<ExerciseTag> exerciseTag) {
			if (exerciseTag.size() > 0)
				this.exerciseTag = exerciseTag;
			return this;
		}

		public Builder relatedURL(List<URL> relatedURL) {
			if (relatedURL.size() > 0)
				this.relatedURL = relatedURL;
			return this;
		}

		public Builder md5(String hash) {
			if (hash != null)
				this.md5 = hash;
			return this;
		}

		public Builder hints(List<String> hints) {
			if (hints.size() > 0)
				this.hints = hints;
			return this;
		}

		public Builder iconPath(File iconPath) {
			if (iconPath != null)
				this.iconPath = iconPath;
			return this;
		}

		public ExerciseType build() {
			// build new object
			ExerciseType propablyNewOne = new ExerciseType(this);

			// only return the new created object, if no
			// equal object exists
			for (ExerciseType eT : exerciseTypes) {
				if (eT.equals(propablyNewOne)) {
					return eT;
				}
			}

			// now it is assured, that propablyNewOne is unique
			boolean asserted = exerciseTypes.add(propablyNewOne);
			// just for getting a bit more security
			assert (asserted);
			return propablyNewOne;
		}

	}

	/**
	 * Constructor for ExerciseType
	 * 
	 * @param builder
	 *            The builder
	 */
	private ExerciseType(Builder builder) {
		this.name = builder.name;

		this.description = builder.description;
		this.imageLicenseMap = new HashMap<File, String>(
				builder.imageLicenseMap);
		this.imageHeight = builder.imageHeight;
		this.imageWidth = builder.imageWidth;
		this.exerciseTag = builder.exerciseTag;
		this.relatedURL = new ArrayList<URL>(builder.relatedURL);
		this.md5 = builder.md5;
		this.iconPath = builder.iconPath;

		this.imagePaths = new ArrayList<File>();
		for (File f : builder.imagePaths) {
			imagePaths.add(new File(f.toString()));
		}

		this.hints = new ArrayList<String>(builder.hints);

		// treat some attributes special

		// this copy is necessary, because builder.neededTools may be a
		// unmodifiable collection
		TreeSet<SportsEquipment> tools = new TreeSet<SportsEquipment>(
				builder.neededTools);
		if (tools.size() > 1) {
			tools.remove(SportsEquipment.NONE);
		}
		if (tools.size() == 0) {
			tools.add(SportsEquipment.NONE);
		}
		this.requiredEquipment = tools;

		// activationMap and activatedMuscles must be in sync
		for (Muscle m : builder.activationMap.keySet()) {
			if (!builder.activatedMuscles.contains(m)) {
				builder.activatedMuscles.add(m);
			}
		}

		// activationMap and activatedMuscles must be in sync
		for (Muscle m : builder.activatedMuscles) {
			if (!builder.activationMap.containsKey(m)) {
				builder.activationMap.put(m, ActivationLevel.MEDIUM);
			}
		}

		this.activationMap = builder.activationMap;

		this.activatedMuscles = builder.activatedMuscles;

	}

	/**
	 * Returns all created instances of ExerciseType
	 * 
	 * @return an unmodifiable Set with all ExerciseTypes
	 */
	public static SortedSet<ExerciseType> listExerciseTypes() {
		return java.util.Collections.unmodifiableSortedSet(exerciseTypes); // set
																			// contains
																			// immutable
																			// objects
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public List<File> getImagePaths() {
		return java.util.Collections.unmodifiableList(this.imagePaths);
	}

	public File getIconPath() {
		return new File(this.iconPath.toString());
	}

	public Map<File, String> getImageLicenseMap() {
		return java.util.Collections.unmodifiableMap(this.imageLicenseMap);
	}

	/**
	 * Getter for the width of the image
	 * 
	 * @return the image width
	 */
	public int getImageWidth() {
		return this.imageWidth;
	}

	/**
	 * Getter for the height of the image
	 * 
	 * @return the image height
	 */
	public int getImageHeight() {
		return this.imageHeight;
	}

	public SortedSet<SportsEquipment> getRequiredEquipment() {
		return java.util.Collections.unmodifiableSortedSet(requiredEquipment); // set
																				// contains
																				// immutable
																				// objects
	}

	public SortedSet<Muscle> getActivatedMuscles() {
		return java.util.Collections.unmodifiableSortedSet(activatedMuscles); // set
																				// contains
																				// immutable
																				// objects
	}

	public Map<Muscle, ActivationLevel> getActivationMap() {
		if (activationMap.isEmpty()) {
			throw new AssertionError();
		}
		return java.util.Collections.unmodifiableMap(activationMap);
	}

	public SortedSet<ExerciseTag> getTags() {
		return java.util.Collections.unmodifiableSortedSet(this.exerciseTag); // set
																				// contains
																				// immutable
																				// objects
	}

	public List<URL> getURLs() {
		return java.util.Collections.unmodifiableList(this.relatedURL); // set
																		// contains
																		// immutable
																		// objects
	}

	public List<String> getHints() {
		return java.util.Collections.unmodifiableList(this.hints);
	}

	/**
	 * Gets an ExerciseType by its name
	 * 
	 * @return The ExerciseType or null
	 */
	public static ExerciseType getByName(String name) {
		for (ExerciseType ex : ExerciseType.listExerciseTypes()) {
			if (ex.getName().equals(name))
				return ex;
		}
		return null;
	}

	/**
	 * Tries to delete an ExerciseType, that means removing it from the list and
	 * deleting the files containing it. If there is still a reference out
	 * there, it is NOT destroyed. A possible approach to achieve this would be
	 * a weak reference or a boolean field 'deleted' that is check in each
	 * method.
	 * 
	 * @param exType The ExerciseType to remove
	 * @return true if deleting was successful
	 */
	public static synchronized boolean removeExerciseType(ExerciseType exType) {
		return ExerciseType.exerciseTypes.remove(exType);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * Two instances are equal if their names are equal.
	 * 
	 * @return {@code true} if the given object represents a
	 *         {@code ExerciseType} equivalent to this {@code ExerciseType},
	 *         false otherwise
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ExerciseType)) {
			return false;
		}
		// now it's guaranteed that this cast will work
		ExerciseType e = (ExerciseType) obj;

		return this.name.equalsIgnoreCase(e.name);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		// For a (very small) performance boost you could consider
		// saving the hashcode in a final int variable with lazy
		// initialization, as this class is immutable
		return this.name.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.name;
	}

	/** {@inheritDoc} */
	public int compareTo(ExerciseType o) {
		return this.name.compareTo(o.name);
	}

	/**
	 * Returns the MD5 hash value.
	 * 
	 * @return The MD5 hash value.
	 */
	public String getMd5() {
		// exception if hash does not exist
		if (this.md5 == null)
			throw new UnsupportedOperationException();

		return this.md5;

	}

	public FitnessExercise asFitnessExercise() {
		return new FitnessExercise(this);

	}

	public static Collection<FitnessExercise> asFitnessExercise(
			List<ExerciseType> exes) {
		List<FitnessExercise> fExes = new ArrayList<FitnessExercise>();
		for (ExerciseType ex : exes) {
			fExes.add(new FitnessExercise(ex));
		}
		return fExes;
	}

}
