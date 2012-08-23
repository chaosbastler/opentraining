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

//TODO: Find a way for I18N

/**
 * An enumeration class for the different muscles of a men(or women).
 * There might be missing some muscles - they have to be added with
 * future releases of this program. Internationalization is also a
 * missing feature.
 * 
 * @author Christian Skubich
 *
 */
public enum Muscle{

        // define the different muscles
        // new muscles must be added here
                
        // chest
        BRUSTMUSKEL("Brustmuskel"), 
        
        // stomach
        BAUCHMUSKELN("Bauchmuskeln"),
        
        // back
        RÜCKENMUSKELN("Rückenmuskeln"),
        
        PO("Po"),
        
        // schoulders
        SCHULTER("Schulter"),
        
        // arms
        BIZEPS("Bizeps"),
        TRIZEPS("Trizeps"),
        
        // leg
        OBERSCHENKELMUSKEL("Oberschenkelmuskel"),
        UNTERSCHENKELMUSKEL("Unterschenkelmuskel");
        
        
        private final String name;

        /**
         * Constructor for muscle
         * @param name The name of the muscle
         */
        private Muscle(String name) {
                this.name = name;
        }
        
        /**
         * Getter for the name of the muscle
         * @return The name of the muscle
         */
        public String getName(){
                return this.name;
        }

        @Override
        public String toString() {
                return name;
        }
        
        /**
         * Return the corresponding Muscle
         * 
         * @param s
         * 
         * @return
         * 
         * @throws IllegalArgumentExeption
         */
        public static Muscle getByName(String s){
                for(Muscle m:values()){
                        if(m.getName().equalsIgnoreCase(s)){
                                return m;
                        }
                }
                
                throw new IllegalArgumentException( s + " was not found in enum Muscle");
        }


}
