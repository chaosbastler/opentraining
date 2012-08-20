package de.skubware.opentraining.basic;

//TODO: make more dynamic

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
        
        // ...
        
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
