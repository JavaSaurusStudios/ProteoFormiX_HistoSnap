/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.control.filter;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public enum DHBMatrixClusterMasses {
    C7H6O4(154.0266f),
    C7H5NaO4(176.0086f),
    C14H10O7(290.04265f),
    C14H7NaO6(294.01403f),
    C14H9NaO7(312.0246f),
    C14H8Na2O7(334.00654f),
    C21H12O9(408.04813f),
    C21H13O9(409.056f),
    C21H14O10(426.0587f),
    C21H13NaO10(448.04064f),
    C21H12Na2O10(470.02259f),
    C28H16O12(544.06418f),
    C28H18O13(562.07474f),
    C28H15NaO12(566.04612f),
    C28H17NaO13(584.05668f),
    C28H18NaO13(585.0645f),
    C35H20O15(680.08022f),
    C35H21O15(681.088f),
    C35H22O16(698.09078f),
    C35H21NaO16(720.07273f),
    C42H24O18(816.09626f),
    C42H25O18(817.1041f),
    C42H26O19(834.10683f),
    C42H28O20(852.11739f),
    C42H26NaO19(857.0966f),
    C42H25NaO19(856.08877f),
    C42H27NaO20(874.09934f),
    C49H30O22(970.12287f),
    C49H29NaO22(992.10482f),
    C49H30NaO22(993.1126f);

    private final float monoIsotopicMass;

    private DHBMatrixClusterMasses(float mass) {
        this.monoIsotopicMass = mass;
    }

    public float getMonoIsotopicMass() {
        return monoIsotopicMass;
    }

}
