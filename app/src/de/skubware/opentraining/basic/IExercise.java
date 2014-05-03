package de.skubware.opentraining.basic;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public interface IExercise {

	/**
	 * Getter for the unlocalized name. This is the default name of the
	 * ExerciseType
	 * 
	 * @return the unlocalized name
	 */
	public abstract String getUnlocalizedName();

	/**
	 * Getter for the unlocalized name. If there is no translation available,
	 * the result will be the same as {@link #getUnlocalizedName()}
	 * 
	 * @return the unlocalized name
	 */
	public abstract String getLocalizedName();

	/**
	 * Getter for the description of the exercise.
	 * 
	 * @return the description, may be null or empty
	 */
	public abstract String getDescription();

	/**
	 * Getter for a list of paths to the images.
	 * 
	 * @return list of paths of the images
	 */
	public abstract List<File> getImagePaths();

	/**
	 * Getter for the icon path. The icon should be small (like a symbol) and
	 * the user should be able to recognize the exercise when he sees the icon.
	 * 
	 * @return the icon path.
	 */
	public abstract File getIconPath();

	/**
	 * Getter for the image license map. This map stores the license for every
	 * image.
	 * 
	 * @return a map containing the licenses for the images
	 */
	public abstract Map<File, License> getImageLicenseMap();

	/**
	 * Getter for the width of the image
	 * 
	 * @return the image width
	 */
	public abstract int getImageWidth();

	/**
	 * Getter for the height of the image
	 * 
	 * @return the image height
	 */
	public abstract int getImageHeight();

	public abstract SortedSet<SportsEquipment> getRequiredEquipment();

	public abstract SortedSet<Muscle> getActivatedMuscles();

	public abstract Map<Muscle, ActivationLevel> getActivationMap();

	public abstract SortedSet<ExerciseTag> getTags();

	public abstract List<URL> getURLs();

	public abstract List<String> getHints();

}