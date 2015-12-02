
package de.skubware.opentraining.activity.create_exercise;

import android.support.v4.app.Fragment;

/**
 * Created by Joseph Daaboul and Sam Kazerouni on 12/1/2015.
 */
public class FragmentFactory {

    public Fragment generateFragment(int position)
    {
        if(position == 0)
            return new NameFragment();
        else if(position == 1)
            return new DescriptionFragment();
        else if(position == 2)
            return new ImageFragment();
        else if(position == 3)
            return new MuscleDataFragment();
        else if(position == 4)
            return new EquipmentDataFragment();
        else
            throw new IllegalStateException("No fragment for position: " + position);
    }
}