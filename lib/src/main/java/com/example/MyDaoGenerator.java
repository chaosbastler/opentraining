package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;


public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "de.skubware.opentraining");

        addMuscle(schema);

        new DaoGenerator().generateAll(schema, "./app/src/");
    }

    private static void addMuscle(Schema schema) {
        Entity translation = schema.addEntity("DBTranslation");

        Property translationLocaleProperty = translation.addStringProperty("locale").getProperty();
        translation.addStringProperty("translated_name");


        Entity muscle = schema.addEntity("DBMuscle");
        Property musclePrimaryKey = muscle.addStringProperty("primary_name").primaryKey().getProperty();
        muscle.addToMany(translation, translationLocaleProperty);

        translation.addToOne(muscle, musclePrimaryKey);

    }


}
