/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
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
import java.io.Serializable;
import java.net.URL;
import java.util.*;

import android.util.Log;

/**
 * An instance of this class represents a certain type of (fitness) exercise.
 * 
 */

public final class ExerciseType implements Comparable<ExerciseType>, IExercise, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "ExerciseType";

	private String name; // required
	private ExerciseSource mExerciseSource; // required
	
	private String localizedName; // optional

	private Map<Locale, String> translationMap; // optional
	private String description; // optional
	private List<File> imagePaths; // optional
	private Map<File, License> imageLicenseMap; // optional
	private int imageWidth; // optional
	private int imageHeight; // optional
	private SortedSet<SportsEquipment> requiredEquipment; // optional
	private SortedSet<Muscle> activatedMuscles; // optional
	private Map<Muscle, ActivationLevel> activationMap; // optional
	private SortedSet<ExerciseTag> exerciseTag; // optional
	private List<URL> relatedURL; // optional
	private List<String> hints; // optional
	private File iconPath; // optional

	
	/**
	 * Indicates where an exercise is from.
	 */
	public enum ExerciseSource{
		/** Default exercise of OpenTraining */
		DEFAULT,
		/** Downloaded from a service like wger.de */
		SYNCED,
		/** Created by the user himself */
		CUSTOM;
	}

	/**
	 * Inner builder class for creating new instances of {@link ExerciseType}.
	 * 
	 */
	public static class Builder {

		// Required parameters
		private final String name;

		// Optional parameters - initialized to default values
		private Map<Locale, String> translationMap = new HashMap<Locale, String>(); // optional
		private String description = ""; // optional
		private List<File> imagePaths = new ArrayList<File>(); // optional
		private Map<File, License> imageLicenseMap = new HashMap<File, License>(); // optional
		private int imageWidth = 214;
		private int imageHeight = 137;
		private SortedSet<SportsEquipment> neededTools = new TreeSet<SportsEquipment>(); // optional
		private SortedSet<Muscle> activatedMuscles = new TreeSet<Muscle>(); // optional
		private Map<Muscle, ActivationLevel> activationMap = new LinkedHashMap<Muscle, ActivationLevel>(); // optional
		private SortedSet<ExerciseTag> exerciseTag = new TreeSet<ExerciseTag>(); // optional
		private List<URL> relatedURL = new ArrayList<URL>(); // optional
		private List<String> hints = new ArrayList<String>(); // optional
		private File iconPath = new File(""); // optional
		private ExerciseSource mExerciseSource; // required

		
		public Builder(String name, ExerciseSource exerciseSource) {
			// null name is NOT allowed
			if (name == null) {
				throw new NullPointerException("Name of exercise must not be null");
			}
			if (exerciseSource == null) {
				throw new NullPointerException("Source of exercise must not be null");
			}

			this.name = name;
			this.mExerciseSource = exerciseSource;
		}

		public Builder translationMap(Map<Locale, String> translationMap) {
			if (translationMap != null)
				this.translationMap = translationMap;
			return this;
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

		public Builder imageLicenseMap(Map<File, License> imageLicenseMap) {
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
			return new ExerciseType(this);
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
		this.mExerciseSource = builder.mExerciseSource;

		this.translationMap = builder.translationMap;
		this.description = builder.description;
		this.imageLicenseMap = new HashMap<File, License>(builder.imageLicenseMap);
		this.imageHeight = builder.imageHeight;
		this.imageWidth = builder.imageWidth;
		this.exerciseTag = builder.exerciseTag;
		this.relatedURL = new ArrayList<URL>(builder.relatedURL);
		this.iconPath = builder.iconPath;

		this.imagePaths = new ArrayList<File>();
		for (File f : builder.imagePaths) {
			imagePaths.add(new File(f.toString()));
		}

		this.hints = new ArrayList<String>(builder.hints);

		// treat some attributes special

		// localize name
		Locale currentLocale = Locale.getDefault();
		currentLocale = new Locale(currentLocale.getLanguage());

		if (this.translationMap.get(currentLocale) == null) {
			localizedName = this.name;
			Log.i(TAG, "Found no localized name for: " + currentLocale.toString() + ". Using unlocalized exercise name:" + this.name);
		} else {
			localizedName = this.translationMap.get(currentLocale);
			// Log.v(TAG, "Localized " + this.name + " to language " + currentLocale + ": " + localizedName);
		}

		// this copy is necessary, because builder.neededTools may be a
		// unmodifiable collection
		TreeSet<SportsEquipment> tools = new TreeSet<SportsEquipment>(builder.neededTools);
		/*
		 * if (tools.size() > 1) {
		 * tools.remove(SportsEquipment.getByName("None")); } if (tools.size()
		 * == 0) { tools.add(SportsEquipment.getByName("None")); }
		 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getUnlocalizedName()
	 */
	@Override
	public String getUnlocalizedName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getLocalizedName()
	 */
	@Override
	public String getLocalizedName() {
		return localizedName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getImagePaths()
	 */
	@Override
	public List<File> getImagePaths() {
		return java.util.Collections.unmodifiableList(this.imagePaths);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getIconPath()
	 */
	@Override
	public File getIconPath() {
		return new File(this.iconPath.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getImageLicenseMap()
	 */
	@Override
	public Map<File, License> getImageLicenseMap() {
		return java.util.Collections.unmodifiableMap(this.imageLicenseMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getImageWidth()
	 */
	@Override
	public int getImageWidth() {
		return this.imageWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getImageHeight()
	 */
	@Override
	public int getImageHeight() {
		return this.imageHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getRequiredEquipment()
	 */
	@Override
	public SortedSet<SportsEquipment> getRequiredEquipment() {
		return java.util.Collections.unmodifiableSortedSet(requiredEquipment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getActivatedMuscles()
	 */
	@Override
	public SortedSet<Muscle> getActivatedMuscles() {
		return java.util.Collections.unmodifiableSortedSet(activatedMuscles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getActivationMap()
	 */
	@Override
	public Map<Muscle, ActivationLevel> getActivationMap() {
		if (activationMap.isEmpty()) {
			throw new AssertionError();
		}
		return java.util.Collections.unmodifiableMap(activationMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getTags()
	 */
	@Override
	public SortedSet<ExerciseTag> getTags() {
		return java.util.Collections.unmodifiableSortedSet(this.exerciseTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getURLs()
	 */
	@Override
	public List<URL> getURLs() {
		return java.util.Collections.unmodifiableList(this.relatedURL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.skubware.opentraining.basic.IExercise#getHints()
	 */
	@Override
	public List<String> getHints() {
		return java.util.Collections.unmodifiableList(this.hints);
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
		return this.name.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getLocalizedName();
	}

	/** {@inheritDoc} */
	public int compareTo(ExerciseType o) {
		return this.getLocalizedName().toLowerCase(Locale.GERMANY).compareTo(o.getLocalizedName().toLowerCase(Locale.GERMANY));
	}

	/**
	 * Generates a FitnessExercise.
	 * 
	 * @return A new {@link FitnessExercise} with this ExerciseType.
	 */
	public FitnessExercise asFitnessExercise() {
		return new FitnessExercise(this);
	}

	/**
	 * Turns a list of ExerciseTypes to a collection of FitnessExercises.
	 * 
	 * @param exes
	 *            The ExerciseTypes
	 * @return The created FitnessExercises
	 */
	public static Collection<FitnessExercise> asFitnessExercise(List<ExerciseType> exes) {
		List<FitnessExercise> fExes = new ArrayList<FitnessExercise>();
		for (ExerciseType ex : exes) {
			fExes.add(new FitnessExercise(ex));
		}
		return fExes;
	}

	/**
	 * Creates and returns a set containing all the names of this exercise for
	 * all languages.
	 * 
	 * @return A set containing the names of this exercise for all languages.
	 */
	public Set<String> getAlternativeNames() {
		return new HashSet<String>(this.translationMap.values());
	}

	/**
	 * Returns the map that contains the translations.
	 * 
	 * @return {@link #translationMap}
	 */
	public Map<Locale, String> getTranslationMap(){
		return new HashMap<Locale, String>(translationMap);
	}

	/**
	 * @return The {@link ExerciseSource} of this exercise
	 */
	public ExerciseSource getExerciseSource(){
		return mExerciseSource;
	}
	
}
