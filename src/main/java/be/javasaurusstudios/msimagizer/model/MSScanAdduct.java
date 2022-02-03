package be.javasaurusstudios.msimagizer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a collection of common mass scan adducts to select
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSScanAdduct {

    /**
     * The list of adducts that currently are set to be automatically considered
     */
    public static List<ADDUCTS> ENABLED_ADDUCTS = new ArrayList<ADDUCTS>();

    /**
     * Common adducts that can be considered
     */
    public static enum ADDUCTS {

        //CATIONS
        NH4_CATION(false, 18), Na_CATION(false, 23), CH3OH_CATION(false, 33), K_CATION(false, 39), CH3CNH_CATION(false, 42), DMSO_CATION(false, 79),
        //ANIONS
        Na_ANION(true, 21), Cl_ANION(true, 35), HCOO_ANION(true, 45), OAc_ANION(true, 59), K_ANION(true, 37), TFA_ANION(true, 113);

        /**
         * The mass deficit for the adduct
         */
        private final float massDeficit;
        /**
         * Boolean indicating if the adduct is anionic or cationic
         */
        private final boolean isAnion;

        /**
         * Constructor for an adduct
         *
         * @param isAnion Boolean indicating if the adduct is anionic or
         * cationic
         * @param massDeficit The mass deficit for the adduct
         */
        private ADDUCTS(boolean isAnion, float massDeficit) {
            this.massDeficit = massDeficit;
            this.isAnion = isAnion;
        }

        public float getMassDeficit() {
            return massDeficit;
        }

        public boolean isAnionic() {
            return isAnion;
        }

        /**
         * Returns the cationic adducts
         *
         * @return a list of cationic adducts
         */
        public static List<ADDUCTS> GetCations() {
            List<ADDUCTS> adducts = new ArrayList<>();
            for (ADDUCTS adduct : values()) {
                if (!adduct.isAnion) {
                    adducts.add(adduct);
                }
            }
            return adducts;
        }

        /**
         * Returns the anionic adducts
         *
         * @return a list of anionic adducts
         */
        public static List<ADDUCTS> GetAnions() {
            List<ADDUCTS> adducts = new ArrayList<>();
            for (ADDUCTS adduct : values()) {
                if (adduct.isAnion) {
                    adducts.add(adduct);
                }
            }
            return adducts;
        }
    }

    /**
     * Prints a list of all adducts
     */
    public static void PrintMassDeficits() {
        StringBuilder result = new StringBuilder();
        for (ADDUCTS adduct : ADDUCTS.values()) {
            result.append(adduct.toString()).append("\t").append(adduct.getMassDeficit()).append(System.lineSeparator());
        }
    }

}
