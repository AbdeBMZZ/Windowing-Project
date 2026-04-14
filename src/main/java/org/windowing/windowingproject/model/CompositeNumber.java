package org.windowing.windowingproject.model;

/**
 * Nombre composite tel que défini à la Section 5.5 de de Berg et al.
 *
 * <p>
 * Un nombre composite {@code (a|b)} est une paire de réels ordonnée
 * lexicographiquement : {@code (a|b) < (a'|b')} ssi {@code a < a'}, ou
 * {@code a == a'} et {@code b < b'}.
 * </p>
 *
 * <p>
 * La technique consiste à remplacer chaque point {@code p = (px, py)} par
 * {@code p̂ = ((px|py), (py|px))}. Les premières coordonnées de deux points
 * distincts de {@code P̂} sont toujours distinctes, idem pour les secondes.
 * On peut ainsi construire un PST sans hypothèse de généricité sur les
 * coordonnées réelles des points d'origine.
 * </p>
 *
 * <p>
 * Une requête {@code [x : x'] × [y : y']} doit être transformée en
 * {@code [(x|−∞) : (x'|+∞)] × [(y|−∞) : (y'|+∞)]} dans l'espace composite.
 * </p>
 *
 * @see <a href="https://www.springer.com/gp/book/9783540779735">
 *      de Berg et al., Computational Geometry, 3rd ed., Section 5.5</a>
 */
public final class CompositeNumber implements Comparable<CompositeNumber> {

    /** Composante principale (coordonnée réelle du point). */
    private final double primary;

    /**
     * Composante secondaire (autre coordonnée du même point, utilisée pour
     * départager les égalités sur {@code primary}).
     */
    private final double secondary;

    /**
     * Crée un nombre composite {@code (primary|secondary)}.
     *
     * @param primary   composante principale
     * @param secondary composante secondaire (tie-breaker)
     */
    public CompositeNumber(double primary, double secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    // -------------------------------------------------------------------------
    // Bornes infinies prêtes à l'emploi pour la transformation des requêtes
    // -------------------------------------------------------------------------

    /**
     * Borne inférieure composite pour une valeur {@code a} :
     * {@code (a|−∞)}. Utilisée pour transformer la borne basse d'une requête.
     *
     * @param a valeur réelle de la borne
     * @return {@code (a|−∞)}
     */
    public static CompositeNumber lowerBound(double a) {
        return new CompositeNumber(a, Double.NEGATIVE_INFINITY);
    }

    /**
     * Borne supérieure composite pour une valeur {@code a} :
     * {@code (a|+∞)}. Utilisée pour transformer la borne haute d'une requête.
     *
     * @param a valeur réelle de la borne
     * @return {@code (a|+∞)}
     */
    public static CompositeNumber upperBound(double a) {
        return new CompositeNumber(a, Double.POSITIVE_INFINITY);
    }

    // -------------------------------------------------------------------------
    // Transformation d'un point
    // -------------------------------------------------------------------------

    /**
     * Transforme la coordonnée X d'un point {@code (px, py)} en nombre
     * composite : {@code (px|py)}.
     *
     * @param px coordonnée x du point
     * @param py coordonnée y du point
     * @return nombre composite représentant la première coordonnée de {@code p̂}
     */
    public static CompositeNumber compositeX(double px, double py) {
        return new CompositeNumber(px, py);
    }

    /**
     * Transforme la coordonnée Y d'un point {@code (px, py)} en nombre
     * composite : {@code (py|px)}.
     *
     * @param px coordonnée x du point
     * @param py coordonnée y du point
     * @return nombre composite représentant la seconde coordonnée de {@code p̂}
     */
    public static CompositeNumber compositeY(double px, double py) {
        return new CompositeNumber(py, px);
    }

    // -------------------------------------------------------------------------
    // Ordre lexicographique
    // -------------------------------------------------------------------------

    /**
     * Ordre lexicographique : compare d'abord {@code primary}, puis
     * {@code secondary} en cas d'égalité.
     *
     * {@inheritDoc}
     */
    @Override
    public int compareTo(CompositeNumber other) {
        int cmp = Double.compare(this.primary, other.primary);
        if (cmp != 0) {
            return cmp;
        }
        return Double.compare(this.secondary, other.secondary);
    }

    /**
     * Retourne {@code true} si {@code this ≤ other} dans l'ordre
     * lexicographique.
     *
     * @param other l'autre nombre composite
     * @return {@code this.compareTo(other) <= 0}
     */
    public boolean leq(CompositeNumber other) {
        return this.compareTo(other) <= 0;
    }

    /**
     * Retourne {@code true} si {@code this ≥ other} dans l'ordre
     * lexicographique.
     *
     * @param other l'autre nombre composite
     * @return {@code this.compareTo(other) >= 0}
     */
    public boolean geq(CompositeNumber other) {
        return this.compareTo(other) >= 0;
    }

    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    /**
     * @return composante principale
     */
    public double getPrimary() {
        return primary;
    }

    /**
     * @return composante secondaire
     */
    public double getSecondary() {
        return secondary;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CompositeNumber))
            return false;
        CompositeNumber other = (CompositeNumber) obj;
        return Double.compare(primary, other.primary) == 0
                && Double.compare(secondary, other.secondary) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(primary);
        result = 31 * result + Double.hashCode(secondary);
        return result;
    }

    @Override
    public String toString() {
        return "(" + primary + "|" + secondary + ")";
    }
}
