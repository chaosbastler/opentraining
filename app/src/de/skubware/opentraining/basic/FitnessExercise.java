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

/**
 * A FitnessExercise needs an ExerciseType and may have FSets and
 * TrainingEntries.
 * 
 * The differcence between an {@link ExerciseType} and an
 * {@link FitnessExercise} is that the ExerciseType is more abstract. A
 * FitnessExercise is a concrete Exercise the user wants to do, an ExerciseType
 * is a general exercise with a name, description, ...
 * 
 * @author Christian Skubich
 * 
 */
public class FitnessExercise implements IExercise, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	private ExerciseType mExerciseType;
	private ArrayList<FSet> mFSetList = new ArrayList<FSet>();

	/** The name the user assigned to this exercise */
	private String mCustomName;

	/**
	 * For each day there is one TrainingEntry saved in this list. The order of
	 * the list should be according to the dates of the entries.
	 */
	private ArrayList<TrainingEntry> mTrainingEntryList = new ArrayList<TrainingEntry>();

	/**
	 * Constructor of this class
	 * 
	 * @param exType
	 *            The ExerciseType of the new object
	 * @param set
	 *            The FitnessSets for this FitnessExercise, may be emtpy
	 * 
	 * @throws NullPointerException
	 *             if exType is null
	 */
	public FitnessExercise(ExerciseType exType, FSet... set) {
		if (exType == null) {
			throw new NullPointerException();
		}

		for (FSet s : set) {
			if (s == null) {
				throw new NullPointerException();
			}
		}

		// assign given parameters to fields
		this.mExerciseType = exType;
		for (FSet fs : set) {
			this.mFSetList.add(fs);
		}
		this.mCustomName = exType.getLocalizedName();
	}

	/**
	 * Getter for the list with TrainingEntries.
	 * 
	 * @return A list with TrainingEntries
	 */
	public List<TrainingEntry> getTrainingEntryList() {
		return mTrainingEntryList;
	}

	@SuppressWarnings("deprecation")
	TrainingEntry addTrainingEntry(Date date) {
		TrainingEntry entry = new TrainingEntry(date);
		this.mTrainingEntryList.add(entry);
		return entry;
	}

	/**
	 * Removes the given TrainingEntry
	 * 
	 * @param entry
	 *            The {@link TrainingEntry} to remove
	 * @return true if successful, false otherwise
	 */
	public boolean removeTrainingEntry(TrainingEntry entry) {
		return this.mTrainingEntryList.remove(entry);
	}

	/**
	 * Getter for exType
	 * 
	 * @return The type of Exercise
	 */
	public ExerciseType getExType() {
		return mExerciseType;
	}

	/**
	 * Getter for the FitnessSets
	 * 
	 * @return An unmodifiable List with FitnessSets
	 */
	public List<FSet> getFSetList() {
		return Collections.unmodifiableList(this.mFSetList);
	}

	public void setFSetList(List<FSet> fsetList) {
		this.mFSetList = new ArrayList<FSet>(fsetList);
	}

	/**
	 * Returns a String representation of this object. This is identical to the
	 * {@code customName}.
	 * 
	 * @return The custumName
	 */
	@Override
	public String toString() {
		return mCustomName;
	}

	/**
	 * Returns a String that represents this object. Should only be used for
	 * debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();

		builder.append("ExerciseType: " + getLocalizedName() + "\n");
		builder.append("Custom name: " + mCustomName + "\n");
		for (FSet set : mFSetList) {
			builder.append("\n FSet: " + set.toString());
		}
		for (TrainingEntry entry : mTrainingEntryList) {
			builder.append("\n TrainingEntry: " + entry.toDebugString());
		}

		return builder.toString();
	}

	/**
	 * Setter for {@code customName}
	 * 
	 * @param newName
	 *            The new custom name for this FitnessExercise.
	 * 
	 * @throws NullPointerException
	 *             if argument is null
	 */
	public void setCustomName(String newName) {
		if (newName == null)
			throw new NullPointerException("setCustonName() Argument must not be null");

		this.mCustomName = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mExerciseType == null) ? 0 : mExerciseType.hashCode());
		result = prime * result + ((mFSetList == null) ? 0 : mFSetList.hashCode());
		return result;
	}

	/**
	 * Compares two FitnessExercises. Important: a different customName does not
	 * change the result of equals()
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FitnessExercise)) {
			return false;
		}
		FitnessExercise other = (FitnessExercise) obj;
		if (mExerciseType == null) {
			if (other.mExerciseType != null) {
				return false;
			}
		} else if (!mExerciseType.equals(other.mExerciseType)) {
			return false;
		}
		if (mFSetList == null) {
			if (other.mFSetList != null) {
				return false;
			}
		} else if (!mFSetList.equals(other.mFSetList)) {
			return false;
		}
		return true;
	}

	@Override
	public String getUnlocalizedName() {
		return this.mExerciseType.getUnlocalizedName();
	}

	@Override
	public String getLocalizedName() {
		return this.mExerciseType.getLocalizedName();
	}

	@Override
	public String getDescription() {
		return this.mExerciseType.getDescription();
	}

	@Override
	public List<File> getImagePaths() {
		return this.mExerciseType.getImagePaths();
	}

	@Override
	public File getIconPath() {
		return this.mExerciseType.getIconPath();
	}

	@Override
	public Map<File, License> getImageLicenseMap() {
		return this.mExerciseType.getImageLicenseMap();
	}

	@Override
	public int getImageWidth() {
		return this.mExerciseType.getImageWidth();
	}

	@Override
	public int getImageHeight() {
		return this.mExerciseType.getImageHeight();
	}

	@Override
	public SortedSet<SportsEquipment> getRequiredEquipment() {
		return this.mExerciseType.getRequiredEquipment();
	}

	@Override
	public SortedSet<Muscle> getActivatedMuscles() {
		return this.mExerciseType.getActivatedMuscles();
	}

	@Override
	public Map<Muscle, ActivationLevel> getActivationMap() {
		return this.mExerciseType.getActivationMap();
	}

	@Override
	public SortedSet<ExerciseTag> getTags() {
		return this.mExerciseType.getTags();
	}

	@Override
	public List<URL> getURLs() {
		return this.mExerciseType.getURLs();
	}

	@Override
	public List<String> getHints() {
		return this.mExerciseType.getHints();
	}
	
	/**
	 * Returns the last TrainingEntry.
	 * 
	 * @return The last TrainingEntry
	 */
	public TrainingEntry getLastTrainingEntry(){
		if(mTrainingEntryList.isEmpty())
			return null;
		
		return mTrainingEntryList.get(mTrainingEntryList.size() -1);
	}
		
	/**
	 * Checks if a Training has been finished. That means that all FSets of the
	 * TrainingEntry have been set to the status done. Will also return false if
	 * there are no TrainingEntrys.
	 * 
	 * @param entry
	 *            The {@link TrainingEntry} to check
	 * 
	 * @return true if all FSets of the TrainingEntry have been set to the
	 *         status done. Will also return false if there are no
	 *         TrainingEntrys.
	 */
	public boolean isTrainingEntryFinished(TrainingEntry entry){
		if(!mTrainingEntryList.contains(entry))
				throw new IllegalArgumentException("Entry not contained.");
		if(entry.getFSetList().isEmpty())
			return false;
		
		for(FSet set:entry.getFSetList()){
			if(!entry.hasBeenDone(set))
				return false;
		}
		
		return true;
	}

}