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

//TODO replace by dynamic variant
public enum SportsEquipment {
        NONE("Keine"), 
        BARBELL_L("Langhantel"),
        BARBELL_SZ("SZ-Stange"),
        BARBELL_S("Kurzhantel"),
        BANK("Trainingsbank"),
        CURL_PULT("Curlpult"),
        LEG_EXTENSION_MACHINE("Beinstrecker Maschine"),
        LEG_PRESS("Beinpresse"),
        MED_BALL("Medizinball"),
        MAT("Gymnastikmatte"),
        SIT_UP_BANK("Sit Up Bank"),
        SWISS_BALL("Swiss Ball"),
        PULL_UP_BAR("Klimmzug Stange"),
        WEIGHT("Hantelscheibe")
        ;

        
        /** The name of the SportsEquipment */
        final private String name;
        
        /**
         * The Constructor
         * 
         * @param name The name of the Tool, names should be used only once
         */
        private SportsEquipment(String name){           
                this.name = name;
        }
        
        
        /**
         * Getter for name
         * @return the name of the SportsEquipment
         */
        public String getName(){
                return this.name;
        }
        
        
        @Override
        public String toString() {
                return name;
        }


        /**
         * @param toolName
         * @return
         */
        public static SportsEquipment getByName(String toolName) {
                for(SportsEquipment s:values()){
                        if(s.getName().equals(toolName)){
                                return s;
                        }
                }
                //return NONE;
                throw new IllegalArgumentException("No " + toolName + " SportsEquipment was found in enum SportsEquipment");
        }

}