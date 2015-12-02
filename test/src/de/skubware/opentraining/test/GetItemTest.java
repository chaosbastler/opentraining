package de.skubware.opentraining.test.RefactoringTest;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import de.skubware.opentraining.activity.create_exercise.CreateExerciseActivity;

/**
 * Created by Joseph Daaboul and Sam Kazerouni on 12/1/2015.
 */

/**
 * Tests for {@link CreateExerciseActivity}.
 *
 */
public class GetItemTest extends AndroidTestCase {

    public static final String TAG = "GetItemTest";
    CreateExerciseActivity exerciseActivity = new CreateExerciseActivity();

    public void TestNameFragment() {
        NameFragment nameFragment = new NameFragment();
        Assert.assertSame("The NameFragment obtained is not the one expected", nameFragment, exerciseActivity.mSectionsPagerAdapter.getItem(0));
    }

    public void TestDescriptionFragment() {
        DescriptionFragment descFragment = new DescriptionFragment();
        Assert.assertSame("The DescriptionFragment obtained is not the one expected", descFragment, exerciseActivity.mSectionsPagerAdapter.getItem(1));
    }

    public void TestImageFragment() {
        ImageFragment imageFragment = new ImageFragment();
        Assert.assertSame("The ImageFragment obtained is not the one expected", imageFragment, exerciseActivity.mSectionsPagerAdapter.getItem(2));
    }

    public void TestMuscleDataFragment() {
        MuscleDataFragment muscleFragment = new MuscleDataFragment();
        Assert.assertSame("The MuscleDataFragment obtained is not the one expected", muscleFragment, exerciseActivity.mSectionsPagerAdapter.getItem(3));
    }

    public void TestEquipmentDataFragment() {
        EquipmentDataFragment equipmentFragment = new EquipmentDataFragment();
        Assert.assertSame("The EquipmentDataFragment obtained is not the one expected", equipmentFragment, exerciseActivity.mSectionsPagerAdapter.getItem(4));
    }

}
