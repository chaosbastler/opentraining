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
        MAT("Gymnastikmatte"),
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